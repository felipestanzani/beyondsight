package com.felipestanzani.beyondsight.dto;

import java.util.List;

public record MethodImpactResponse(
                String methodSignature,
                List<ClassImpactResponse> impactedClasses) {
}
