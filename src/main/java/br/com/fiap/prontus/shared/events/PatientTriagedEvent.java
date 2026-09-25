package br.com.fiap.prontus.shared.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PatientTriagedEvent (
        Long triageId,
        Long patientId,
        String severity,
        Integer riskScore,
        String ticketCode,
        BigDecimal timeWeight,
        LocalDateTime occurredAt
){
}
