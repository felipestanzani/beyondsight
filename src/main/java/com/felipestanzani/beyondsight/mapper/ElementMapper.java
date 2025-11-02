package com.felipestanzani.beyondsight.mapper;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.ElementImpactQueryResult;
import com.felipestanzani.beyondsight.dto.ImpactedMethodResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElementMapper {

    private ElementMapper() {
    }

    public static Map<String, ClassImpactResponse> mapResultsToClassImpactResponses(
            List<ElementImpactQueryResult> results) {
        return results.stream()
                .collect(Collectors.groupingBy(
                        ElementImpactQueryResult::className,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                rows -> {
                                    String filePath = rows.getFirst().filePath();
                                    List<ImpactedMethodResponse> methods = rows.stream()
                                            .map(row -> new ImpactedMethodResponse(
                                                    row.methodName(),
                                                    row.methodSignature(),
                                                    row.methodFilePath(),
                                                    row.impactType()))
                                            .distinct()
                                            .toList();
                                    return new ClassImpactResponse(
                                            rows.getFirst().className(),
                                            filePath,
                                            null,
                                            methods,
                                            List.of() // No fields in field impact analysis
                                    );
                                })));
    }
}
