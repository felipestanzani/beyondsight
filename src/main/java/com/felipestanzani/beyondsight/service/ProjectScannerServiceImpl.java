package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.exception.ProjectParsingException;
import com.felipestanzani.beyondsight.service.interfaces.ParsingServiceFactory;
import com.felipestanzani.beyondsight.service.interfaces.ProjectScannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectScannerServiceImpl implements ProjectScannerService {

    private final ParsingServiceFactory router;

    @Override
    public void scan(String projectPath) {
        try (var files = Files.walk(Path.of(projectPath))) {
            var supportedLanguages = router.getSupportedLanguages();

            supportedLanguages.forEach(lang -> {
                var langFiles = files.filter(f -> f.toString().endsWith("." + lang));
                var service = router.forLanguage(lang);
                if (service != null)
                    langFiles.forEach(service::parseFile);
            });


        } catch (Exception e) {
            throw new ProjectParsingException(projectPath, e);
        }
    }
}
