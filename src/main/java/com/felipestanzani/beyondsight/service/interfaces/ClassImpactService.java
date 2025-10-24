package com.felipestanzani.beyondsight.service.interfaces;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;

public interface ClassImpactService {
    ClassImpactResponse getFullClassImpact(String className);
}
