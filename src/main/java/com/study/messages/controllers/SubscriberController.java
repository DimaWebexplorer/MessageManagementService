package com.study.messages.controllers;

import com.study.messages.dto.ApiResponse;
import com.study.messages.dto.FollowersPage;
import com.study.messages.dto.SubscriptionStats;
import com.study.messages.services.SubscriptionService;
import com.study.messages.validators.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/v1/subscribers")
@RequiredArgsConstructor
public class SubscriberController {

    private final RequestValidator validator;
    private final SubscriptionService subscriptionService;

    @GetMapping("/{producer_id}/followers")
    public Mono<ResponseEntity<ApiResponse<FollowersPage>>> getFollowers(
            @PathVariable("producer_id") int producerId,
            @RequestParam(value = "after_id", defaultValue = "0") int afterId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return Mono.fromCallable(() -> {
                    validator.validateId(producerId, "producer_id");
                    validator.validateFollowersParams(afterId, limit);
                    return subscriptionService.getFollowers(producerId, afterId, limit);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(followers -> ResponseEntity.ok(ApiResponse.ok(followers)));
    }

    @GetMapping("/{user_id}/stats")
    public Mono<ResponseEntity<ApiResponse<SubscriptionStats>>> getStats(
            @PathVariable("user_id") int userId) {
        return Mono.fromCallable(() -> {
                    validator.validateId(userId, "user_id");
                    return subscriptionService.getStats(userId);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(stats -> ResponseEntity.ok(ApiResponse.ok(stats)));
    }
}
