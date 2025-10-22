package com.learn.springboot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Standardized API response format to match Node.js API structure
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        Boolean success,
        Boolean error,
        T data,
        String message,
        Integer statusCode,
        String timestamp
) {

    /**
     * Create a successful response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                null,
                data,
                null,
                null,
                Instant.now().toString()
        );
    }

    /**
     * Create a successful response with data and status code
     */
    public static <T> ApiResponse<T> success(T data, Integer statusCode) {
        return new ApiResponse<>(
                true,
                null,
                data,
                null,
                statusCode,
                Instant.now().toString()
        );
    }

    /**
     * Create an error response
     */
    public static <T> ApiResponse<T> error(String message, Integer statusCode) {
        return new ApiResponse<>(
                null,
                true,
                null,
                message,
                statusCode,
                Instant.now().toString()
        );
    }

    /**
     * Create an error response with details
     */
    public static <T> ApiResponse<T> error(String message, Integer statusCode, T details) {
        return new ApiResponse<>(
                null,
                true,
                details,
                message,
                statusCode,
                Instant.now().toString()
        );
    }
}
