package com.felipestanzani.beyondsight.dto;

import java.util.List;

public record FileResponseDto(
        String name,
        String absolutePath,
        List<TypeResponseDto> types
) implements FileResponse {
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public List<TypeResponse> getTypes() {
        return types.stream()
                .map(t -> (TypeResponse) t)
                .toList();
    }
}

