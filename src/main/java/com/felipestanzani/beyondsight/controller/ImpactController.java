package com.felipestanzani.beyondsight.controller;

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
    @GetMapping(value = "/field/full", produces = "application/x-toon")
    public ResponseEntity<String> getFullFieldImpact(
            @RequestParam String fieldName,
            @RequestParam String className) {
        var response = fieldImpactService.getFullFieldImpact(fieldName, className);
        return ResponseEntity.ok(JToon.encode(response));
    }

    /**
     * Gets full transitive impact analysis for a method.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the method.
     */
    @GetMapping(value = "/method/full", produces = "application/x-toon")
    public ResponseEntity<String> getFullMethodImpact(
            @RequestParam String methodSignature,
            @RequestParam String className) {
        var response = methodImpactService.getFullMethodImpact(methodSignature, className);
        return ResponseEntity.ok(JToon.encode(response));
    }

    /**
     * Gets full transitive impact analysis for a class.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the class.
     */
    @GetMapping(value = "/class/full", produces = "application/x-toon")
    public ResponseEntity<String> getFullClassImpact(
            @RequestParam String className) {
        var response = classImpactService.getFullClassImpact(className);
        return ResponseEntity.ok(JToon.encode(response));
    }
}
