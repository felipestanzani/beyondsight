package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.*;
import com.felipestanzani.beyondsight.repository.java.JavaClassRepository;
import com.felipestanzani.beyondsight.service.interfaces.ClassImpactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ClassImpactServiceImpl implements ClassImpactService {

    private final JavaClassRepository classRepository;

    @Override
    public List<FileResponse> getFullClassImpact(String className) {
        return classRepository.findClassReferences(className);
    }
}