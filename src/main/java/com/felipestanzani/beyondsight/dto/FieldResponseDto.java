package com.felipestanzani.beyondsight.dto;

public record FieldResponseDto(
        String name,
        Integer lineNumber
) implements FieldResponse {
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getLineNumber() {
        return lineNumber;
    }
}

