package com.felipestanzani.beyondsight.service.interfaces;

import com.felipestanzani.beyondsight.dto.MethodImpactResponseDto;
import com.felipestanzani.beyondsight.model.JavaMethod;

import java.util.List;

public interface MethodImpactService {
    List<JavaMethod> getUpstreamCallers(String methodName);

    List<JavaMethod> getDownstreamCallees(String methodSignature);

    MethodImpactResponseDto getFullMethodImpact(String methodSignature);
}
