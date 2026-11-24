package com.study.messages.dto;

import com.study.messages.exceptions.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private int code;
    private String error;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, null, "OK", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(200, null, message, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, null, "Created", data);
    }

    public static <T> ApiResponse<T> error(ErrorCode code, T data) {
        return new ApiResponse<>(code.getStatus(), code.name(), code.getMessage(), data);
    }
}
