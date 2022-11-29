package com.dubroushchyk.clever.webclients;

import com.dubroushchyk.clever.dto.ClientNoteRequestDto;
import com.dubroushchyk.clever.dto.NoteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NoteWebClient {

    public static final String URI_NOTES = "/notes";

    private final WebClient webClient;

    public List<NoteResponseDto> getNotesByClients(final List<ClientNoteRequestDto> listDto) {
        return Flux.fromIterable(listDto)
                .flatMap(this::getNoteByClient)
                .collectList()
                .block();
    }

    private Flux<NoteResponseDto> getNoteByClient(final ClientNoteRequestDto dto) {
        return webClient.post()
                .uri(URI_NOTES)
                .bodyValue(dto)
                .retrieve()
                .bodyToFlux(NoteResponseDto.class);
    }

}
