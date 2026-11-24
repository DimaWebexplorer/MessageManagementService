package com.study.messages.controllers;

import com.study.messages.dto.ApiResponse;
import com.study.messages.dto.Message;
import com.study.messages.dto.MessagePage;
import com.study.messages.dto.MessageRequest;
import com.study.messages.services.MessageService;
import com.study.messages.validators.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final RequestValidator validator;
    private final MessageService messageService;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Message>>> createMessage(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody MessageRequest body) {
        return Mono.fromCallable(() -> {
                    validator.validateMessageText(body);
                    int authorId = Integer.parseInt(jwt.getSubject());
                    return messageService.createMessage(body.getText(), authorId);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(message -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(message)));
    }

    @GetMapping("/feed")
    public Mono<ResponseEntity<ApiResponse<MessagePage>>> getFeed(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "before_ts", required = false) String beforeTs,
            @RequestParam(value = "before_id", required = false) Integer beforeId) {
        return Mono.fromCallable(() -> {
                    OffsetDateTime cursorTs = validator.validateFeedParams(limit, beforeTs, beforeId);
                    int subscriberId = Integer.parseInt(jwt.getSubject());
                    return messageService.getFeed(subscriberId, cursorTs, beforeId, limit);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(page -> ResponseEntity.ok(ApiResponse.ok(page)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Message>>> getMessage(@PathVariable("id") int id) {
        return Mono.fromCallable(() -> {
                    validator.validateId(id, "id");
                    return messageService.getMessage(id);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(message -> ResponseEntity.ok(ApiResponse.ok(message)));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<MessagePage>>> getAuthorMessages(
            @RequestParam("author_id") Integer authorId,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "before_ts", required = false) String beforeTs,
            @RequestParam(value = "before_id", required = false) Integer beforeId) {
        return Mono.fromCallable(() -> {
                    validator.validateId(authorId, "author_id");
                    OffsetDateTime cursorTs = validator.validateFeedParams(limit, beforeTs, beforeId);
                    return messageService.getAuthorMessages(authorId, cursorTs, beforeId, limit);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(page -> ResponseEntity.ok(ApiResponse.ok(page)));
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Message>>> updateMessage(
            @PathVariable("id") int id,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication,
            @RequestBody MessageRequest body) {
        return Mono.fromCallable(() -> {
                    validator.validateId(id, "id");
                    validator.validateMessageText(body);
                    int currentUserId = Integer.parseInt(jwt.getSubject());
                    boolean isAdmin = hasRole(authentication, "ROLE_administrator");
                    return messageService.updateMessage(id, body.getText(), currentUserId, isAdmin);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(message -> ResponseEntity.ok(ApiResponse.ok(message)));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteMessage(
            @PathVariable("id") int id,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        return Mono.fromCallable(() -> {
                    validator.validateId(id, "id");
                    int currentUserId = Integer.parseInt(jwt.getSubject());
                    boolean isAdmin = hasRole(authentication, "ROLE_administrator");
                    messageService.deleteMessage(id, currentUserId, isAdmin);
                    return ApiResponse.<Void>ok("Deleted", null);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(ResponseEntity::ok);
    }

    private boolean hasRole(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> authority.equals(a.getAuthority()));
    }
}
