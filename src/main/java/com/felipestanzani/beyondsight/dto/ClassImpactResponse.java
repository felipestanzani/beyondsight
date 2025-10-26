package com.felipestanzani.beyondsight.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClassImpactResponse(
                String name,
                String filePath,
                List<ClassImpactResponse> impactedClasses,
                List<ImpactedMethodResponse> impactedMethods,
                List<ImpactedFieldResponse> impactedFields) {
}
