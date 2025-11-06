package com.felipestanzani.beyondsight.dto;

import java.util.List;

public interface MemberResponse {
    String getName();
    String getSignature();
    List<MemberResponse> getCalledMethods();
    List<FieldResponse> getReadFields();
    List<FieldResponse> getWrittenFields();
    Integer getLineNumber();
}
