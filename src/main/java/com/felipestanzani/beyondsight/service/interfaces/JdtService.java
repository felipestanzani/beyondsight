package com.felipestanzani.beyondsight.service.interfaces;

import com.felipestanzani.beyondsight.dto.Symbol;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;

import java.util.List;

public interface JdtService {
    Symbol resolve(String name) throws Exception;
    List<Symbol> references(String name) throws Exception;
    List<Symbol> callers(String name) throws Exception;
    List<Symbol> callees(String name) throws Exception;
    /**
     * Workspace symbol search (like "Open Type" in Eclipse).
     * Searches for types (classes, interfaces, enums) matching the text.
     * @param text Search query (e.g., "User", "UserService", "user*")
     * @return List of matching symbols
     */
    List<Symbol> workspaceSearch(String text) throws CoreException;
}
