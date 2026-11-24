package com.study.messages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class MySubscription {
    private Integer producerId;
    private OffsetDateTime subscribedAt;
}
