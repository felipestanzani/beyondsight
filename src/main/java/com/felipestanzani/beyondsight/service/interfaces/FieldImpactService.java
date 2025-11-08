package com.felipestanzani.beyondsight.service.interfaces;

import com.felipestanzani.beyondsight.dto.FileResponse;

import java.util.List;

public interface FieldImpactService {
    List<FileResponse> getFullFieldImpact(String fieldName, String className);
}
