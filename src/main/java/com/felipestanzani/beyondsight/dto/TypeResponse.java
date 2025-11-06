package com.felipestanzani.beyondsight.dto;

import java.util.List;

public interface TypeResponse {
    String getName();

    Integer getLineNumber();

    List<FieldResponse> getFields();

    List<MemberResponse> getMembers();
}
