package com.felipestanzani.beyondsight.service.interfaces;

import com.felipestanzani.beyondsight.model.enums.ParseStatus;

public interface ProjectIndexingService {
    void rescanProject(String path);

    ParseStatus getParseStatus();
}
