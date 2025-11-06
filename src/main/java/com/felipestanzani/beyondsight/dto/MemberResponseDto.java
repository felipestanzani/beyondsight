package com.felipestanzani.beyondsight.dto;

import java.util.List;

public record MemberResponseDto(
        String name,
        String signature,
        Integer lineNumber,
        List<MemberResponseDto> calledMethods,
        List<FieldResponseDto> readFields,
        List<FieldResponseDto> writtenFields
) implements MemberResponse {
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public Integer getLineNumber() {
        return lineNumber;
    }

    @Override
    public List<MemberResponse> getCalledMethods() {
        return calledMethods.stream()
                .map(m -> (MemberResponse) m)
                .toList();
    }

    @Override
    public List<FieldResponse> getReadFields() {
        return readFields.stream()
                .map(f -> (FieldResponse) f)
                .toList();
    }

    @Override
    public List<FieldResponse> getWrittenFields() {
        return writtenFields.stream()
                .map(f -> (FieldResponse) f)
                .toList();
    }
}

