package com.study.messages.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteApiResponse<T> {
    private int code;
    private String error;
    private String message;
    private T data;
}
