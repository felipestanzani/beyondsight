package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.repository.CompilationUnitRepository;
import com.felipestanzani.beyondsight.repository.MemberNodeRepository;
import com.felipestanzani.beyondsight.repository.ReferenceRepository;
import com.felipestanzani.beyondsight.repository.TypeNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewParserService {

    private CompilationUnitRepository cuRepo;
    private TypeNodeRepository typeRepo;
    private MemberNodeRepository memberRepo;
    private ReferenceRepository refRepo;
}
