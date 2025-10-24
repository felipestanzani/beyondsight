package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.service.interfaces.ParsingService;
import com.felipestanzani.beyondsight.service.interfaces.ProjectIndexingService;
import org.springframework.stereotype.Service;

@Service
public class ProjectIndexingServiceImpl implements ProjectIndexingService {

    private final ParsingService parsingService;

    public ProjectIndexingServiceImpl(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    /**
     * Triggers a full re-scan of a project. Clears the entire database first.
     * 
     * @param path The absolute file path to the root of the Java project.
     */
    @Override
    public void rescanProject(String path) {
        parsingService.clearDatabase();
        parsingService.parseProject(path);
    }
}
