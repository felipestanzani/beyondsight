package com.felipestanzani.beyondsight.dto;

public record ClassImpactQueryResult(
                String className,
                String filePath,
                String methodName,
                String methodSignature,
                String methodFilePath,
                String fieldName,
                String fieldType,
                String impactType) {
}
