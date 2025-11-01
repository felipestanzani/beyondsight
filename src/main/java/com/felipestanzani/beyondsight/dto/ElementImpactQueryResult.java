package com.felipestanzani.beyondsight.dto;

public record ElementImpactQueryResult(
                String className,
                String filePath,
                String methodName,
                String methodSignature,
                String methodFilePath,
                String impactType) {
}
