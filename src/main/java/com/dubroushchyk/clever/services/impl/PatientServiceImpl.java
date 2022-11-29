package com.dubroushchyk.clever.services.impl;

import com.dubroushchyk.clever.entities.Patient;
import com.dubroushchyk.clever.repository.PatientRepository;
import com.dubroushchyk.clever.services.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PatientServiceImpl implements PatientService {

    public static final String DELIMITER = ",";

    private final PatientRepository patientRepo;

    public List<Patient> findPatients() {
        log.info("Start search active patients");
        List<Short> statuses = Stream.of(200, 210, 230).map(Integer::shortValue).toList();
        List<Patient> activePatients = patientRepo.getPatientsWithStatusAndOldClientGuid(statuses);
        log.info("Number of found patients is {}", activePatients.size());
        return activePatients;
    }

    @Override
    public Map<String, Patient> getPatientsWithGuids() {
        Map<String, Patient> guidsOfPatients = new HashMap<>();
        List<Patient> patients = findPatients();
        patients.forEach(patient -> fillGuidPatients(guidsOfPatients, patient));
        return guidsOfPatients;
    }

    private void fillGuidPatients(Map<String, Patient> guidPatients, Patient patient) {
        String oldGuid = patient.getOldClientGuid();
        String[] guids = oldGuid.split(DELIMITER);
        Arrays.stream(guids).forEach(guid -> guidPatients.put(guid, patient));
    }

}
