package com.study.messages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class MessagePage {
    private List<Message> messages;
    private OffsetDateTime nextBeforeTs;
    private Integer nextBeforeId;
}
