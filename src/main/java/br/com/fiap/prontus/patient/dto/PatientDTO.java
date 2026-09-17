package br.com.fiap.prontus.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientDTO {

    public record CreatePatientRequest(
            @NotBlank @Size(max = 150) String fullName,
            @NotNull @Past LocalDate birthDate,
            String comorbidities
            ){}

    public record PatientResponse(
            Long id,
            String fullName,
            LocalDate birthDate,
            String comorbidities,
            LocalDateTime createdAt
    ){}
}
