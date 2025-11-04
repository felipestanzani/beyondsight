package com.felipestanzani.beyondsight.controller;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.FieldImpactResponse;
import com.felipestanzani.beyondsight.dto.MethodImpactResponse;
import com.felipestanzani.beyondsight.service.interfaces.ClassImpactService;
import com.felipestanzani.beyondsight.service.interfaces.FieldImpactService;
import com.felipestanzani.beyondsight.service.interfaces.MethodImpactService;
import com.felipestanzani.jtoon.JToon;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/impact")
public class ImpactController {
    private final FieldImpactService fieldImpactService;
    private final MethodImpactService methodImpactService;
    private final ClassImpactService classImpactService;

    /**
     * Gets full transitive impact analysis for a field.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the field.
     */
    @GetMapping(value = "/field/full", produces = "text/plain")
    public ResponseEntity<String> getFullFieldImpact(
            @RequestParam String fieldName,
            @RequestParam String className) {
        FieldImpactResponse response = fieldImpactService.getFullFieldImpact(fieldName, className);
        return ResponseEntity.ok(JToon.encode(response));
    }

    /**
     * Gets full transitive impact analysis for a method.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the method.
     */
    @GetMapping("/method/full")
    public ResponseEntity<MethodImpactResponse> getFullMethodImpact(
            @RequestParam String methodSignature) {
        MethodImpactResponse response = methodImpactService.getFullMethodImpact(methodSignature);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets full transitive impact analysis for a class.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the class.
     */
    @GetMapping("/class/full")
    public ResponseEntity<ClassImpactResponse> getFullClassImpact(
            @RequestParam String className) {
        ClassImpactResponse response = classImpactService.getFullClassImpact(className);
        return ResponseEntity.ok(response);
    }
}
