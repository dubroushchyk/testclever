package com.dubroushchyk.clever.repository;

import com.dubroushchyk.clever.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("select p from Patient p " +
            "where p.status in :status " +
            "and p.oldClientGuid is not null")
    List<Patient> getPatientsWithStatusAndOldClientGuid(@Param("status") List<Short> status);

}
