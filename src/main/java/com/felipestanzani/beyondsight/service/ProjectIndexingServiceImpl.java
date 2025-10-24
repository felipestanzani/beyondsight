package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.exception.ParseAlreadyRunningException;
import com.felipestanzani.beyondsight.model.ParseStatus;
import com.felipestanzani.beyondsight.service.interfaces.ParsingService;
import com.felipestanzani.beyondsight.service.interfaces.ProjectIndexingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProjectIndexingServiceImpl implements ProjectIndexingService {

    private static final Logger log = LoggerFactory.getLogger(ProjectIndexingServiceImpl.class);

    private final ParsingService parsingService;
    private volatile ParseStatus parseStatus = ParseStatus.IDLE;
    private final Object lock = new Object();

    public ProjectIndexingServiceImpl(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    /**
     * Triggers a full re-scan of a project. Clears the entire database first.
     * This method now runs asynchronously using Virtual Threads.
     * 
     * @param path The absolute file path to the root of the Java project.
     * @throws ParseAlreadyRunningException if a parse operation is already running
     */
    @Override
    public void rescanProject(String path) {
        synchronized (lock) {
            if (parseStatus == ParseStatus.RUNNING) {
                throw new ParseAlreadyRunningException();
            }

            parseStatus = ParseStatus.RUNNING;
        }

        // Launch Virtual Thread for async parsing
        Thread.startVirtualThread(() -> {
            try {
                log.info("Starting async project parse at: {}", path);
                parsingService.clearDatabase();
                parsingService.parseProject(path);
                parseStatus = ParseStatus.COMPLETED;
                log.info("Async project parse completed successfully");
            } catch (Exception e) {
                parseStatus = ParseStatus.FAILED;
                log.error("Async project parse failed", e);
            }
        });

        log.info("Async project parse initiated for path: {}", path);
    }

    @Override
    public ParseStatus getParseStatus() {
        return parseStatus;
    }
}
