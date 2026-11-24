package com.study.messages.controllers;

import com.study.messages.dto.ApiResponse;
import com.study.messages.dto.MySubscription;
import com.study.messages.dto.Subscription;
import com.study.messages.dto.SubscriptionCheck;
import com.study.messages.dto.SubscriptionRequest;
import com.study.messages.services.SubscriptionService;
import com.study.messages.validators.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final RequestValidator validator;
    private final SubscriptionService subscriptionService;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Subscription>>> subscribe(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody SubscriptionRequest body) {
        return Mono.fromCallable(() -> {
                    Integer producerId = body == null ? null : body.getProducerId();
                    validator.validateId(producerId, "producer_id");
                    int subscriberId = Integer.parseInt(jwt.getSubject());
                    validator.validateNotSelfSubscription(subscriberId, producerId);
                    return subscriptionService.subscribe(subscriberId, producerId, jwt.getTokenValue());
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(subscription -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(subscription)));
    }

    @DeleteMapping("/{producer_id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> unsubscribe(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("producer_id") int producerId) {
        return Mono.fromCallable(() -> {
                    validator.validateId(producerId, "producer_id");
                    int subscriberId = Integer.parseInt(jwt.getSubject());
                    subscriptionService.unsubscribe(subscriberId, producerId);
                    return ApiResponse.<Void>ok("Unsubscribed", null);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(ResponseEntity::ok);
    }

    @GetMapping("/my")
    public Mono<ResponseEntity<ApiResponse<List<MySubscription>>>> getMySubscriptions(
            @AuthenticationPrincipal Jwt jwt) {
        return Mono.fromCallable(() -> {
                    int subscriberId = Integer.parseInt(jwt.getSubject());
                    return subscriptionService.getMySubscriptions(subscriberId);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(subscriptions -> ResponseEntity.ok(ApiResponse.ok(subscriptions)));
    }

    @GetMapping("/{producer_id}/check")
    public Mono<ResponseEntity<ApiResponse<SubscriptionCheck>>> checkSubscription(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("producer_id") int producerId) {
        return Mono.fromCallable(() -> {
                    validator.validateId(producerId, "producer_id");
                    int subscriberId = Integer.parseInt(jwt.getSubject());
                    return subscriptionService.checkSubscription(subscriberId, producerId);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(result -> ResponseEntity.ok(ApiResponse.ok(result)));
    }
}
