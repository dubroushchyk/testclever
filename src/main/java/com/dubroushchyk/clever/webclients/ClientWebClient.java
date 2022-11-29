package com.dubroushchyk.clever.webclients;

import com.dubroushchyk.clever.dto.ClientResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClientWebClient {

    public static final String URI_CLIENTS = "/clients";

    private final WebClient webClient;

    public List<ClientResponseDto> getClients() {
        return webClient.post()
                .uri(URI_CLIENTS)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(ClientResponseDto.class)
                .collectList()
                .block();
    }

}
