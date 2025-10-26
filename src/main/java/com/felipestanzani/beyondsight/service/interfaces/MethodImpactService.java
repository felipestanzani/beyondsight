package com.felipestanzani.beyondsight.service.interfaces;

import com.felipestanzani.beyondsight.dto.MethodImpactResponse;

public interface MethodImpactService {
    MethodImpactResponse getFullMethodImpact(String methodSignature);
}
