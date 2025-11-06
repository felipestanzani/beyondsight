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
        return new TypeResponseDto(
                (String) typeMap.get("name"),
                toInteger(typeMap.get("lineNumber")),
                ((List<Map<String, Object>>) typeMap.get("fields")).stream()
                        .map(this::convertFieldToDto)
                        .toList(),
                ((List<Map<String, Object>>) typeMap.get("members")).stream()
                        .map(this::convertMemberToDto)
                        .toList());
    }

    @SuppressWarnings("unchecked")
    private MemberResponseDto convertMemberToDto(Map<String, Object> memberMap) {
        return new MemberResponseDto(
                (String) memberMap.get("name"),
                (String) memberMap.get("signature"),
                toInteger(memberMap.get("lineNumber")),
                ((List<Map<String, Object>>) memberMap.get("calledMethods")).stream()
                        .map(this::convertMemberToDto)
                        .toList(),
                ((List<Map<String, Object>>) memberMap.get("readFields")).stream()
                        .map(this::convertFieldToDto)
                        .toList(),
                ((List<Map<String, Object>>) memberMap.get("writtenFields")).stream()
                        .map(this::convertFieldToDto)
                        .toList());
    }

    private FieldResponseDto convertFieldToDto(Map<String, Object> fieldMap) {
        return new FieldResponseDto(
                (String) fieldMap.get("name"),
                toInteger(fieldMap.get("lineNumber")));
    }

    private Integer toInteger(Object value) {
        return switch (value) {
            case Integer intValue -> intValue;
            case Long longValue -> longValue.intValue();
            case Number numberValue -> numberValue.intValue();
            default -> null;
        };
    }
}
