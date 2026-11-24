package com.study.messages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscriptionStats {
    private long followersCount;
    private long followingCount;
}
