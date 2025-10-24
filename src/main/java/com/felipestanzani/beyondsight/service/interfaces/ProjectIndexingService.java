package com.felipestanzani.beyondsight.service.interfaces;

import com.felipestanzani.beyondsight.model.ParseStatus;

public interface ProjectIndexingService {
    void rescanProject(String path);

    ParseStatus getParseStatus();
}
