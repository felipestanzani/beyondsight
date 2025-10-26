package com.felipestanzani.beyondsight.controller;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.FieldImpactResponse;
import com.felipestanzani.beyondsight.dto.MethodImpactResponseDto;
import com.felipestanzani.beyondsight.model.JavaMethod;
import com.felipestanzani.beyondsight.service.interfaces.ClassImpactService;
import com.felipestanzani.beyondsight.service.interfaces.FieldImpactService;
import com.felipestanzani.beyondsight.service.interfaces.MethodImpactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/impact")
public class ImpactController {
    private final FieldImpactService fieldImpactService;
    private final MethodImpactService methodImpactService;
    private final ClassImpactService classImpactService;

    public ImpactController(FieldImpactService fieldImpactService,
            MethodImpactService methodImpactService,
            ClassImpactService classImpactService) {
        this.fieldImpactService = fieldImpactService;
        this.methodImpactService = methodImpactService;
        this.classImpactService = classImpactService;
    }

    /**
     * Finds all methods that WRITE to a specific field.
     */
    @GetMapping("/field/writers")
    public ResponseEntity<List<JavaMethod>> getFieldWriters(@RequestParam String fieldName) {
        List<JavaMethod> methods = fieldImpactService.getFieldWriters(fieldName);
        return ResponseEntity.ok(methods);
    }

    /**
     * Finds all methods that READ a specific field.
     */
    @GetMapping("/field/readers")
    public ResponseEntity<List<JavaMethod>> getFieldReaders(@RequestParam String fieldName) {
        List<JavaMethod> methods = fieldImpactService.getFieldReaders(fieldName);
        return ResponseEntity.ok(methods);
    }

    /**
     * Finds all methods that (directly or indirectly) CALL a target method.
     * This is a "who calls me?" or "upstream" analysis.
     */
    @GetMapping("/upstream/callers")
    public ResponseEntity<List<JavaMethod>> getUpstreamCallers(@RequestParam String methodName) {
        List<JavaMethod> methods = methodImpactService.getUpstreamCallers(methodName);
        return ResponseEntity.ok(methods);
    }

    /**
     * Killer Query: Finds all methods that (directly or indirectly) are CALLED BY
     * a target method. This is a "who do I call?" or "downstream" analysis.
     */
    @GetMapping("/downstream/callees")
    public ResponseEntity<List<JavaMethod>> getDownstreamCallees(@RequestParam String methodSignature) {
        List<JavaMethod> methods = methodImpactService.getDownstreamCallees(methodSignature);
        return ResponseEntity.ok(methods);
    }

    /**
     * Gets full transitive impact analysis for a field.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the field.
     */
    @GetMapping("/field/full")
    public ResponseEntity<FieldImpactResponse> getFullFieldImpact(
            @RequestParam String fieldName,
            @RequestParam String className) {
        FieldImpactResponse response = fieldImpactService.getFullFieldImpact(fieldName, className);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets full transitive impact analysis for a method.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the method.
     */
    @GetMapping("/method/full")
    public ResponseEntity<MethodImpactResponseDto> getFullMethodImpact(
            @RequestParam String methodSignature) {
        MethodImpactResponseDto response = methodImpactService.getFullMethodImpact(methodSignature);
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
