package com.felipestanzani.beyondsight.mapper;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.ElementImpactQueryResult;

import java.util.List;
import java.util.Map;

public class FieldMapper {

    private FieldMapper() {
    }

    /**
     * Maps a list of ElementImpactQueryResult to a Map of ClassImpactResponse
     * objects,
     * grouped by className.
     *
     * @param results List of ElementImpactQueryResult
     * @return Map where key is className and value is ClassImpactResponse
     */
    public static Map<String, ClassImpactResponse> mapFieldResultsToClassImpactResponses(
            List<ElementImpactQueryResult> results) {
        return ElementMapper.mapResultsToClassImpactResponses(results);
    }
}
