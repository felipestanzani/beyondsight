package com.felipestanzani.beyondsight.service.interfaces;

import com.felipestanzani.beyondsight.dto.FieldImpactResponse;
import com.felipestanzani.beyondsight.model.JavaMethod;

import java.util.List;

public interface FieldImpactService {
    List<JavaMethod> getFieldWriters(String fieldName);

    List<JavaMethod> getFieldReaders(String fieldName);

    FieldImpactResponse getFullFieldImpact(String fieldName, String className);
}
