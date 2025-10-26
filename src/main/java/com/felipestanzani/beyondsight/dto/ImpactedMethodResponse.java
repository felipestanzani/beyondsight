package com.felipestanzani.beyondsight.dto;

public record ImpactedMethodResponse(
                String name,
                String signature,
                String filePath,
                String impactType) {
}
