package com.felipestanzani.beyondsight.dto;

import java.util.List;

public record MethodImpactResponseDto(
                String methodSignature,
                List<ClassImpactResponse> impactedClasses) {
}
