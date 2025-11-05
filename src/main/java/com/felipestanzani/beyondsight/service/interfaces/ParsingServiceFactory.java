package com.felipestanzani.beyondsight.service.interfaces;

import java.nio.file.Path;
import java.util.List;

public interface ParsingServiceFactory {
    List<String> getSupportedLanguages();

    ParsingService forLanguage(String language);

    ParsingService forFile(Path file);
}
