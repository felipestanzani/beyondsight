package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.service.interfaces.ParsingService;
import com.felipestanzani.beyondsight.service.interfaces.ParsingServiceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParsingServiceFactoryImpl implements ParsingServiceFactory {
    private final Map<String, ParsingService> parsingServiceMap;

    public List<String> getSupportedLanguages() {

        return parsingServiceMap.keySet().stream().toList();
    }

    public ParsingService forLanguage(String language) {
        return parsingServiceMap.get(language);
    }

    public ParsingService forFile(Path file) {
        String ext = com.google.common.io.Files.getFileExtension(
                file.getFileName().toString()).toLowerCase();
        return forLanguage(ext);
    }
}
