package com.felipestanzani.beyondsight.mappers;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.FieldImpactQueryResult;
import com.felipestanzani.beyondsight.dto.ImpactedMethodResponse;
import com.felipestanzani.beyondsight.dto.MethodImpactQueryResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JavaClassMapper {

    private JavaClassMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Maps a list of MethodImpactQueryResult to a Map of ClassImpactResponse
     * objects,
     * grouped by className.
     *
     * @param results List of MethodImpactQueryResult
     * @return Map where key is className and value is ClassImpactResponse
     */
    public static Map<String, ClassImpactResponse> mapMethodResultsToClassImpactResponses(
            List<MethodImpactQueryResult> results) {
        return results.stream()
                .collect(Collectors.groupingBy(
                        MethodImpactQueryResult::className,
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
                                            List.of() // No fields in method impact analysis
                                    );
                                })));
    }

    /**
     * Maps a list of FieldImpactQueryResult to a Map of ClassImpactResponse
     * objects,
     * grouped by className.
     *
     * @param results List of FieldImpactQueryResult
     * @return Map where key is className and value is ClassImpactResponse
     */
    public static Map<String, ClassImpactResponse> mapFieldResultsToClassImpactResponses(
            List<FieldImpactQueryResult> results) {
        return results.stream()
                .collect(Collectors.groupingBy(
                        FieldImpactQueryResult::className,
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
