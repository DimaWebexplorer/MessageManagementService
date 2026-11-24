package com.study.messages.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {

    private Integer id;
    private String text;
    private Integer authorId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
