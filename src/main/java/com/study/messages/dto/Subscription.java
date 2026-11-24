package com.study.messages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    private Integer subscriberId;
    private Integer producerId;
    private OffsetDateTime createdAt;
}
