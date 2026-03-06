package com.flashsale.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private int status_code;
    private String message;
    private T data;

    // Generic success method
    public static <T> ResponseEntity<ApiResponse<T>> success(
            HttpStatus status,
            String message,
            T data
    ) {
        return ResponseEntity
                .status(status)
                .body(new ApiResponse<>(
                        status.value(),
                        message,
                        data
                ));
    }

    // Shortcut cho 200 OK
    public static <T> ResponseEntity<ApiResponse<T>> ok(
            String message,
            T data
    ) {
        return success(HttpStatus.OK, message, data);
    }

    // Shortcut cho 201 CREATED
    public static <T> ResponseEntity<ApiResponse<T>> created(
            String message,
            T data
    ) {
        return success(HttpStatus.CREATED, message, data);
    }
}