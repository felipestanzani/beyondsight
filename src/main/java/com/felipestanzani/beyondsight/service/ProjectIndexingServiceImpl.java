package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.exception.ParseAlreadyRunningException;
import com.felipestanzani.beyondsight.exception.AsyncParsingException;
import com.felipestanzani.beyondsight.model.ParseStatus;
import com.felipestanzani.beyondsight.service.interfaces.ParsingService;
import com.felipestanzani.beyondsight.service.interfaces.ProjectIndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectIndexingServiceImpl implements ProjectIndexingService {

    private final ParsingService parsingService;
    private volatile ParseStatus parseStatus = ParseStatus.IDLE;
    private final Object lock = new Object();

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
                parsingService.clearDatabase();
                parsingService.parseProject(path);
                parseStatus = ParseStatus.COMPLETED;
            } catch (Exception e) {
                parseStatus = ParseStatus.FAILED;
                throw new AsyncParsingException(e);
            }
        });
    }

    @Override
    public ParseStatus getParseStatus() {
        return parseStatus;
    }
}
