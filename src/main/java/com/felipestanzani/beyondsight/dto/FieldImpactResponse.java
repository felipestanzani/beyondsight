package com.felipestanzani.beyondsight.dto;

import java.util.List;

public record FieldImpactResponse(
                String fieldName,
                String className,
                List<ClassImpactResponse> impactedClasses) {
}
