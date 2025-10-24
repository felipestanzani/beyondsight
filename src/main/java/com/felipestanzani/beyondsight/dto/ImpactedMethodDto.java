package com.felipestanzani.beyondsight.dto;

public record ImpactedMethodDto(
                String name,
                String signature,
                String filePath,
                String impactType) {
}
