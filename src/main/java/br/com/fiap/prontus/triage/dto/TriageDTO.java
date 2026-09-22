package br.com.fiap.prontus.triage.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public class TriageDTO {

    public record CreateTriageRequest(
            Long patientId,
            @NotBlank String symptoms
    ){}

    public record TriageResponse(
         Long id,
         Long patientId,
         String symptoms,
         String severity,
         Integer riskScore,
         String ticketCode,
         LocalDateTime createdAt
    ){}

    public static final List<String> SEVERITY_ORDER = List.of(
            "RED", "ORANGE", "YELLOW", "GREEN", "BLUE"
    );
}
