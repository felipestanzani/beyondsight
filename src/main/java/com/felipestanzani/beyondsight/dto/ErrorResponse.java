package com.felipestanzani.beyondsight.dto;

import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * DTO class representing an error response structure.
 * Provides type-safe error information for API responses.
 */
public record ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {

    @Override
    public @NonNull String toString() {
        return "ErrorResponse{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
