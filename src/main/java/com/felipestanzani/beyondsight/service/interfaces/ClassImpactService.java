package com.felipestanzani.beyondsight.service.interfaces;

import com.felipestanzani.beyondsight.dto.FileResponse;

import java.util.List;

public interface ClassImpactService {
    List<FileResponse> getFullClassImpact(String className);
}
