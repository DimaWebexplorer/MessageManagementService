package com.study.messages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FollowersPage {
    private List<Integer> subscriberIds;
    private Integer nextAfterId;
}
