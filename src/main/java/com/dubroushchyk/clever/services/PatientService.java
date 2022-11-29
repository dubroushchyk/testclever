package com.dubroushchyk.clever.services;

import com.dubroushchyk.clever.entities.Patient;

import java.util.Map;

public interface PatientService {

    Map<String, Patient> getPatientsWithGuids();

}
