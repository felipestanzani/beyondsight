package com.felipestanzani.beyondsight.dto;

import java.util.List;
import java.util.Map;

// Simple record to receive data from Neo4j query
// The types field contains Map<String, Object> because Spring Data Neo4j
// returns nested maps for complex structures
public record FileResponseRecord(
        String name,
        String absolutePath,
        List<Map<String, Object>> types
) {
}

