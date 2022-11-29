package com.dubroushchyk.clever.services.impl;

import com.dubroushchyk.clever.dto.ClientNoteRequestDto;
import com.dubroushchyk.clever.dto.ClientResponseDto;
import com.dubroushchyk.clever.dto.NoteResponseDto;
import com.dubroushchyk.clever.entities.Note;
import com.dubroushchyk.clever.entities.Patient;
import com.dubroushchyk.clever.entities.User;
import com.dubroushchyk.clever.repository.NoteRepository;
import com.dubroushchyk.clever.services.NoteService;
import com.dubroushchyk.clever.services.PatientService;
import com.dubroushchyk.clever.services.UserService;
import com.dubroushchyk.clever.webclients.ClientWebClient;
import com.dubroushchyk.clever.webclients.NoteWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepo;
    private final PatientService patientService;
    private final ClientWebClient clientWebClient;
    private final NoteWebClient noteWebClient;
    private final UserService userService;

    @Override
    @Scheduled(cron = "${cron.start-import-notes}")
    public void importNotesFromOldSystem() {

        Map<String, Patient> patientsWithGuid = getPatients();

        List<NoteResponseDto> notesFromOldSystem = getNotesFromOldSystem(patientsWithGuid);

        if (CollectionUtils.isEmpty(notesFromOldSystem)) {
            log.info("No patient with certain statuses in the new system");
            return;
        }
        Map<String, Note> existingNotesForPatients = new HashMap<>();

        patientsWithGuid.forEach((guid, patient) -> {
            Set<Note> notes = patient.getNotes();
            notes.forEach(note ->
                    existingNotesForPatients.put(note.getOldNoteGuid(), note));
        });
        saveNotesInNewSystem(patientsWithGuid, notesFromOldSystem,
                existingNotesForPatients);
    }

    private void saveNotesInNewSystem(Map<String, Patient> patientsWithGuid,
                                      List<NoteResponseDto> notesFromServer,
                                      Map<String, Note> existingNotesForPatients) {

        notesFromServer.forEach(noteDto -> {
            String guidNoteDto = noteDto.getGuid();
            Patient patient = patientsWithGuid.get(noteDto.getClientGuid());
            if (existingNotesForPatients.containsKey(guidNoteDto)) {
                Note existingNote = existingNotesForPatients.get(guidNoteDto);
                if (checkDifferences(noteDto, existingNote)) {
                    update(noteDto, patient, existingNote);
                }
            } else {
                create(noteDto, patient);
            }
        });
    }

    private Map<String, Patient> getPatients() {
        return patientService.getPatientsWithGuids();
    }

    private List<NoteResponseDto> getNotesFromOldSystem(Map<String, Patient> patientsWithGuid) {
        List<ClientResponseDto> clientsFromOldSystem = clientWebClient.getClients();
        List<ClientNoteRequestDto> requestBody = prepareRequestBody(patientsWithGuid, clientsFromOldSystem);
        List<NoteResponseDto> notesFromOldSystem = noteWebClient.getNotesByClients(requestBody);
        log.info("{} notes received from old system", notesFromOldSystem.size());
        return notesFromOldSystem;
    }

    private List<ClientNoteRequestDto> prepareRequestBody(Map<String, Patient> patientsWithGuid,
                                                          List<ClientResponseDto> clientsFromOldSystem) {
        return clientsFromOldSystem.stream()
                .filter(client -> patientsWithGuid.containsKey(client.getGuid()))
                .map(this::buildRequestBody)
                .toList();
    }

    private boolean checkDifferences(NoteResponseDto noteDto, Note existingNote) {
        return !isContentEquals(noteDto, existingNote) ||
                !isModifiedDateTimeEquals(noteDto, existingNote) ||
                !isCreatedDateTimeEquals(noteDto, existingNote) ||
                !isUserEquals(noteDto, existingNote) ||
                !isPatientEquals(noteDto, existingNote);
    }

    private void update(NoteResponseDto noteDto, Patient patient, Note existingNote) {
        if (noteDto.getModifiedDateTime().isAfter(existingNote.getModifiedDateTime())) {
            User user =
                    userService.findByLoginOrCreate(noteDto.getLoggedUser());
            if (user != null) {
                existingNote.setNote(noteDto.getComments());
                existingNote.setModifiedDateTime(noteDto.getModifiedDateTime());
                existingNote.setCreatedDateTime(noteDto.getCreatedDateTime());
                existingNote.setUserEditor(user);
                existingNote.setPatient(patient);
                noteRepo.save(existingNote);
            }
        }
    }

    private void create(NoteResponseDto noteDto, Patient patient) {
        User user =
                userService.findByLoginOrCreate(noteDto.getLoggedUser());
        if (user != null) {
            Note newNote = Note.builder()
                    .createdDateTime(noteDto.getCreatedDateTime())
                    .modifiedDateTime(noteDto.getModifiedDateTime())
                    .userCreator(user)
                    .userEditor(user)
                    .note(noteDto.getComments())
                    .patient(patient)
                    .oldNoteGuid(noteDto.getGuid())
                    .build();
            noteRepo.save(newNote);
        }
    }

    private ClientNoteRequestDto buildRequestBody(ClientResponseDto client) {
        return ClientNoteRequestDto.builder()
                .agency(client.getAgency())
                .dateFrom(client.getCreatedDateTime().toLocalDate())
                .dateTo(LocalDate.now())
                .clientGuid(client.getGuid())
                .build();
    }

    private boolean isPatientEquals(NoteResponseDto noteDto, Note existingNote) {
        return existingNote.getPatient().getOldClientGuid().contains(noteDto.getClientGuid());
    }

    private boolean isUserEquals(NoteResponseDto noteDto, Note existingNote) {
        return noteDto.getLoggedUser().equals(existingNote.getUserEditor().getLogin());
    }

    private boolean isCreatedDateTimeEquals(NoteResponseDto noteDto, Note existingNote) {
        return noteDto.getCreatedDateTime().equals(existingNote.getCreatedDateTime());
    }

    private boolean isModifiedDateTimeEquals(NoteResponseDto noteDto, Note existingNote) {
        return noteDto.getModifiedDateTime().equals(existingNote.getModifiedDateTime());
    }

    private boolean isContentEquals(NoteResponseDto noteDto, Note existingNote) {
        return noteDto.getComments().equals(existingNote.getNote());
    }

}
