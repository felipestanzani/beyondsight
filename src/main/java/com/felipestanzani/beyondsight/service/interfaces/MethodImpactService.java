package com.felipestanzani.beyondsight.service.interfaces;

import com.felipestanzani.beyondsight.dto.FileResponse;

import java.util.List;

public interface MethodImpactService {
    List<FileResponse> getFullMethodImpact(String methodSignature, String className);
}
