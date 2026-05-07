package com.fitness.gateway.user;

import com.fitness.gateway.dto.RegisterRequest;
import com.fitness.gateway.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserService {

    @Autowired
    private WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {

            return (userServiceWebClient.get().uri("api/users/{userId}/validate", userId)
                    .retrieve().bodyToMono(Boolean.class).onErrorResume(WebClientResponseException.class,e -> {
                        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                            throw new RuntimeException("User not found");
                        }
                        else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            throw new RuntimeException("Invalid Request");
                        }
                        return Mono.just(false);
                    }));

    }

    public Mono<UserResponse> registerUser(RegisterRequest request) {
        log.info("Calling User Registration API for email: {}", request.getEmail());

        return userServiceWebClient.post().uri("api/users/register").bodyValue(request).retrieve().bodyToMono(UserResponse.class) .onErrorResume(WebClientResponseException.class, e -> {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                return Mono.error(new RuntimeException("Bad Request: " + e.getMessage()));
            else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                return Mono.error(new RuntimeException("Internal Server Error: " + e.getMessage()));
            return Mono.error(new RuntimeException("Unexpected error: " + e.getMessage()));
        });
    }
}