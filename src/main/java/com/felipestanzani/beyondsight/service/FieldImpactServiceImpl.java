package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.*;
import com.felipestanzani.beyondsight.repository.FieldRepository;
import com.felipestanzani.beyondsight.service.interfaces.FieldImpactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FieldImpactServiceImpl implements FieldImpactService {

    private final FieldRepository fieldRepository;

    /**
     * Gets full transitive impact analysis for a field.
     * 
     * @param fieldName The name of the field to analyze
     * @param className The name of the class containing the field
     * @return Complete field impact response with hierarchical structure
     */
    @Override
    public List<FileResponse> getFullFieldImpact(String fieldName, String className) {
        return fieldRepository.findFieldReferences(fieldName, className).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private FileResponseDto convertToDto(FileResponseRecord fileRecord) {
        return new FileResponseDto(
                fileRecord.name(),
                fileRecord.absolutePath(),
                fileRecord.types().stream()
                        .map(this::convertTypeToDto)
                        .toList());
    }

    @SuppressWarnings("unchecked")
    private TypeResponseDto convertTypeToDto(Map<String, Object> typeMap) {
        List<Map<String, Object>> fields = (List<Map<String, Object>>) typeMap.get("fields");
        List<Map<String, Object>> members = (List<Map<String, Object>>) typeMap.get("members");

        return new TypeResponseDto(
                (String) typeMap.get("name"),
                toInteger(typeMap.get("lineNumber")),
                fields != null ? fields.stream()
                        .map(this::convertFieldToDto)
                        .toList() : List.of(),
                members != null ? members.stream()
                        .map(this::convertMemberToDto)
                        .toList() : List.of());
    }

    @SuppressWarnings("unchecked")
    private MemberResponseDto convertMemberToDto(Map<String, Object> memberMap) {
        List<Map<String, Object>> calledMethods = (List<Map<String, Object>>) memberMap.get("calledMethods");
        List<Map<String, Object>> readFields = (List<Map<String, Object>>) memberMap.get("readFields");
        List<Map<String, Object>> writtenFields = (List<Map<String, Object>>) memberMap.get("writtenFields");

        return new MemberResponseDto(
                (String) memberMap.get("name"),
                (String) memberMap.get("signature"),
                toInteger(memberMap.get("lineNumber")),
                calledMethods != null ? calledMethods.stream()
                        .map(this::convertMemberToDto)
                        .toList() : List.of(),
                readFields != null ? readFields.stream()
                        .map(this::convertFieldToDto)
                        .toList() : List.of(),
                writtenFields != null ? writtenFields.stream()
                        .map(this::convertFieldToDto)
                        .toList() : List.of());
    }

    private FieldResponseDto convertFieldToDto(Map<String, Object> fieldMap) {
        return new FieldResponseDto(
                (String) fieldMap.get("name"),
                toInteger(fieldMap.get("lineNumber")));
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case Integer intValue -> intValue;
            case Long longValue -> longValue.intValue();
            case Number numberValue -> numberValue.intValue();
            default -> null;
        };
    }
}
