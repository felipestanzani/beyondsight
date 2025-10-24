package com.felipestanzani.beyondsight.dto;

public record FieldImpactQueryResult(
                String className,
                String filePath,
                String methodName,
                String methodSignature,
                String methodFilePath,
                String impactType) {
}
