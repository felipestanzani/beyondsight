package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.Location;
import com.felipestanzani.beyondsight.dto.Symbol;
import com.felipestanzani.beyondsight.service.interfaces.JdtService;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.search.*;
import org.eclipse.core.resources.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class JdtServiceImpl implements JdtService {

    private IJavaProject javaProject;
    private final SearchEngine searchEngine = new SearchEngine();

    public void init(String workspacePath) throws Exception {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        String projName = "mcp-index-" + Integer.toHexString(workspacePath.hashCode());
        IProject project = root.getProject(projName);

        IProgressMonitor monitor = new NullProgressMonitor();
        if (!project.exists()) project.create(monitor);
        project.open(monitor);

        // Set real location
        IProjectDescription desc = ResourcesPlugin.getWorkspace().newProjectDescription(projName);
        desc.setLocation(org.eclipse.core.runtime.Path.fromOSString(workspacePath));
        project.setDescription(desc, monitor);

        IJavaProject javaProject = JavaCore.create(project);
        List<IPath> srcPaths = findSourceFolders(Path.of(workspacePath));
        IClasspathEntry[] cp = srcPaths.stream()
                .map(p -> JavaCore.newSourceEntry(project.getFullPath().append(p)))
                .toArray(IClasspathEntry[]::new);

        javaProject.setRawClasspath(cp, monitor);
        javaProject.setOutputLocation(project.getFullPath().append("bin"), monitor);

        this.javaProject = javaProject;
    }

    private List<IPath> findSourceFolders(Path root) {
        List<IPath> sources = new ArrayList<>();
        try (var stream = Files.walk(root, 3)) {
            stream.filter(Files::isDirectory)
                    .filter(p -> {
                        String name = p.getFileName().toString();
                        return "src".equals(name) ||
                                (p.getParent() != null && "main".equals(p.getParent().getFileName().toString()) && "java".equals(name));
                    })
                    .map(root::relativize)
                    .map(rel -> org.eclipse.core.runtime.Path.fromPortableString(rel.toString()))
                    .forEach(sources::add);
        } catch (IOException e) {
            // ignore
        }
        if (sources.isEmpty()) {
            sources.add(org.eclipse.core.runtime.Path.fromPortableString("")); // root as source
        }
        return sources;
    }

    // ---------- Exact resolve ----------
    public Symbol resolve(String name) throws Exception {
        SearchPattern pat = SearchPattern.createPattern(name,
                IJavaSearchConstants.METHOD, IJavaSearchConstants.DECLARATIONS,
                SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
        List<IMethod> methods = search(pat, IJavaSearchConstants.METHOD);
        if (!methods.isEmpty()) return toSymbol(methods.get(0));
        // fallback to types
        pat = SearchPattern.createPattern(name, IJavaSearchConstants.TYPE,
                IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
        List<IType> types = search(pat, IJavaSearchConstants.TYPE);
        return types.isEmpty() ? null : toSymbol(types.get(0));
    }

    // ---------- References ----------
    public List<Symbol> references(String name) throws Exception {
        IMethod m = findMethod(name);
        if (m == null) return List.of();
        SearchPattern pat = SearchPattern.createPattern(m, IJavaSearchConstants.REFERENCES);
        return search(pat, IJavaSearchConstants.METHOD).stream()
                .map(this::toSymbol).toList();
    }

    // ---------- Callers / Callees ----------
    public List<Symbol> callers(String name) throws Exception {
        IMethod m = findMethod(name);
        if (m == null) return List.of();
        return callHierarchy(m, true);
    }

    public List<Symbol> callees(String name) throws Exception {
        IMethod m = findMethod(name);
        if (m == null) return List.of();
        return callHierarchy(m, false);
    }
    public List<Symbol> workspaceSearch(String text) throws CoreException {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }

        // Build pattern: exact + prefix + pattern matching
        int matchRule = SearchPattern.R_EXACT_MATCH |
                SearchPattern.R_PREFIX_MATCH |
                SearchPattern.R_PATTERN_MATCH |
                SearchPattern.R_CAMELCASE_MATCH;

        SearchPattern pattern = SearchPattern.createPattern(
                text,
                IJavaSearchConstants.TYPE,
                IJavaSearchConstants.DECLARATIONS,
                matchRule
        );

        // Use generic search helper (from earlier)
        List<IType> types = search(pattern, IJavaSearchConstants.TYPE);

        // Convert to Symbols
        return types.stream()
                .map(this::toSymbol)
                .filter(Objects::nonNull)
                .toList();
    }

    // ---------- Helpers ----------
    private <T extends IJavaElement> List<T> search(SearchPattern pattern, int elementType)
            throws CoreException {
        List<T> results = new ArrayList<>();

        SearchParticipant participant = SearchEngine.getDefaultSearchParticipant();

        SearchRequestor requestor = new SearchRequestor() {
            @Override
            public void acceptSearchMatch(SearchMatch match) {
                Object element = match.getElement();
                if (element instanceof IJavaElement javaElement &&
                        (elementType == -1 || javaElement.getElementType() == elementType)) {

                    @SuppressWarnings("unchecked")
                    T t = (T) javaElement;
                    results.add(t);
                }
            }
        };

        IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
        IProgressMonitor monitor = new NullProgressMonitor();

        searchEngine.search(
                pattern,
                new SearchParticipant[] { participant },
                scope,
                requestor,
                monitor
        );

        return results;
    }

    private IMethod findMethod(String fqName) throws Exception {
        // "com.app.UserService.save"
        int hash = fqName.lastIndexOf('#');
        if (hash == -1) return null;
        String type = fqName.substring(0, hash).replace(".", "/");
        String method = fqName.substring(hash + 1);
        IType t = javaProject.findType(type.replace("/", "."));
        if (t == null) return null;
        return Arrays.stream(t.getMethods())
                .filter(m -> m.getElementName().equals(method.split("\\(")[0]))
                .findFirst().orElse(null);
    }

    private Symbol toSymbol(IJavaElement el) {
        try {
            if (!(el instanceof ISourceReference src)) {
                return null;
            }

            ISourceRange range = src.getNameRange();
            if (range == null || range.getOffset() == -1) {
                return null;
            }

            ICompilationUnit cu = (ICompilationUnit) el.getAncestor(IJavaElement.COMPILATION_UNIT);
            if (cu == null || !cu.exists()) {
                return null;
            }

            // === KEY FIX: Parse to AST to build line tables ===
            ASTParser parser = ASTParser.newParser(AST.JLS24);  // Latest Java (21+ for 2025)
            parser.setProject(javaProject);
            parser.setSource(cu);
            parser.setResolveBindings(true);  // Enables full resolution
            parser.setStatementsRecovery(true);
            parser.setIgnoreMethodBodies(false);

            IProgressMonitor monitor = new NullProgressMonitor();
            CompilationUnit ast = (CompilationUnit) parser.createAST(monitor);
            if (ast == null) {
                return null;
            }

            // Now getLineNumber() works on the AST root
            int line = ast.getLineNumber(range.getOffset());
            if (line <= 0) {
                line = 1;  // Fallback
            }

            int column = ast.getColumnNumber(range.getOffset());
            if (column < 0) {
                column = 0;
            }

            String file = cu.getPath().toOSString();

            // Javadoc
            String doc = "";
            if (el instanceof IMember member) {
                try {
                    String javadoc = member.getAttachedJavadoc(monitor);
                    if (javadoc != null && !javadoc.isEmpty()) {
                        doc = javadoc;
                    }
                } catch (JavaModelException ignored) {
                    // No Javadoc available
                }
            }

            // Signature
            String signature = "";
            if (el instanceof IMethod method) {
                signature = method.getSignature();
            }

            return new Symbol(
                    el.getElementName(),
                    kindOf(el),
                    new Location(file, line, column),
                    signature,
                    doc
            );

        } catch (JavaModelException | IllegalStateException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String kindOf(IJavaElement el) {
        return switch (el.getElementType()) {
            case IJavaElement.METHOD -> "method";
            case IJavaElement.TYPE -> "type";
            case IJavaElement.FIELD -> "field";
            case IJavaElement.PACKAGE_FRAGMENT -> "package";
            default -> "element";
        };
    }

    private List<Symbol> callHierarchy(IMethod root, boolean callers) throws Exception {
        // Real implementation uses org.eclipse.jdt.internal.core.hierarchy
        // For brevity: just return empty – replace with full CallHierarchy
        return List.of();
    }
}
