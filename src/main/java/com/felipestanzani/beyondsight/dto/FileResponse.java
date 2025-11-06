package com.felipestanzani.beyondsight.dto;

import java.util.List;

public interface FileResponse {
    String getName();
    String getAbsolutePath();
    List<TypeResponse> getTypes();
}
