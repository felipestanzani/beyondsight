package com.felipestanzani.beyondsight.controller;

import com.felipestanzani.beyondsight.service.interfaces.ProjectIndexingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TraceabilityController {

    private final ProjectIndexingService projectIndexingService;

    public TraceabilityController(ProjectIndexingService projectIndexingService) {
        this.projectIndexingService = projectIndexingService;
    }

    /**
     * Triggers a full re-scan of a project. Clears the entire database first.
     * 
     * @param path The absolute file path to the root of the Java project.
     */
    @PostMapping("/index/rescan")
    public ResponseEntity<String> indexProject(@RequestParam String path) {
        projectIndexingService.rescanProject(path);
        return ResponseEntity.ok("Project re-scan initiated.");
    }
}