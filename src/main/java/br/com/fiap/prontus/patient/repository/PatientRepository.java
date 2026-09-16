package br.com.fiap.prontus.patient.repository;

import br.com.fiap.prontus.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
