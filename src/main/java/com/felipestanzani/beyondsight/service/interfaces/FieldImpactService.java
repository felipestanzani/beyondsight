package com.felipestanzani.beyondsight.service.interfaces;

import com.felipestanzani.beyondsight.dto.FileResponseRecord;

import java.util.List;

public interface FieldImpactService {
    List<FileResponseRecord> getFullFieldImpact(String fieldName, String className);
}
