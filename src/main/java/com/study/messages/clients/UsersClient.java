package com.study.messages.clients;

import com.study.messages.dto.RemoteApiResponse;
import com.study.messages.dto.RemoteUser;
import com.study.messages.exceptions.ApiException;
import com.study.messages.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
public class UsersClient {

    private static final ParameterizedTypeReference<RemoteApiResponse<RemoteUser>> USER_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final WebClient usersWebClient;
    private final Duration timeout;

    public UsersClient(WebClient usersWebClient, @Value("${app.users-service.timeout}") Duration timeout) {
        this.usersWebClient = usersWebClient;
        this.timeout = timeout;
    }

    public RemoteUser getUser(int userId, String bearerToken) {
        try {
            RemoteApiResponse<RemoteUser> response = usersWebClient.get()
                    .uri("/api/v1/users/{id}", userId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .exchangeToMono(clientResponse -> {
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return clientResponse.bodyToMono(USER_RESPONSE_TYPE);
                        }
                        if (clientResponse.statusCode().value() == HttpStatus.NOT_FOUND.value()) {
                            return clientResponse.releaseBody()
                                    .then(Mono.error(new ApiException(ErrorCode.USER_NOT_FOUND)));
                        }
                        if (clientResponse.statusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                            return clientResponse.releaseBody()
                                    .then(Mono.error(new ApiException(ErrorCode.UNAUTHORIZED)));
                        }
                        return clientResponse.releaseBody()
                                .then(Mono.error(new ApiException(ErrorCode.SERVICE_UNAVAILABLE)));
                    })
                    .block(timeout);

            if (response == null || response.getCode() != 200 || response.getData() == null
                    || !Integer.valueOf(userId).equals(response.getData().getId())
                    || response.getData().getRoles() == null) {
                throw new ApiException(ErrorCode.SERVICE_UNAVAILABLE);
            }
            return response.getData();
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Failed to call Users service for user_id={}: {}", userId, e.getMessage());
            throw new ApiException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }
}
