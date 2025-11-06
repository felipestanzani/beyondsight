package com.felipestanzani.beyondsight.dto;

import java.util.List;

public record TypeResponseDto(
        String name,
        Integer lineNumber,
        List<FieldResponseDto> fields,
        List<MemberResponseDto> members
) implements TypeResponse {
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getLineNumber() {
        return lineNumber;
    }

    @Override
    public List<FieldResponse> getFields() {
        return fields.stream()
                .map(f -> (FieldResponse) f)
                .toList();
    }

    @Override
    public List<MemberResponse> getMembers() {
        return members.stream()
                .map(m -> (MemberResponse) m)
                .toList();
    }
}

