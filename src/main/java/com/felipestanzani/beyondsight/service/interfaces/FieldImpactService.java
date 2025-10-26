package com.felipestanzani.beyondsight.service.interfaces;

import com.felipestanzani.beyondsight.dto.FieldImpactResponse;

public interface FieldImpactService {
    FieldImpactResponse getFullFieldImpact(String fieldName, String className);
}
