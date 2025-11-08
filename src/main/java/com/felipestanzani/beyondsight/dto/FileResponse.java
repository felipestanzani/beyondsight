package com.felipestanzani.beyondsight.dto;

import java.util.List;
import java.util.Map;

public record FileResponse(
        String name,
        String absolutePath,
        List<Map<String, Object>> types
) {}

