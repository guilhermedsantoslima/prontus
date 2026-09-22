package br.com.fiap.prontus.triage.service;

import br.com.fiap.prontus.patient.model.Patient;
import br.com.fiap.prontus.patient.repository.PatientRepository;
import br.com.fiap.prontus.queue.model.QueueEntry;
import br.com.fiap.prontus.queue.repository.QueueEntryRepository;
import br.com.fiap.prontus.shared.exception.NotFoundException;
import br.com.fiap.prontus.triage.dto.TriageDTO;
import br.com.fiap.prontus.triage.engine.TriageEngine;
import br.com.fiap.prontus.triage.model.Triage;
import br.com.fiap.prontus.triage.repository.TriageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class TriageService {

    private static final Map<String, String> PREFIXES = Map.of(
            "RED", "R", "ORANGE", "O", "YELLOW", "Y", "GREEN", "G", "BLUE", "B"
    );

    private final TriageRepository triageRepository;
    private final PatientRepository patientRepository;
    private final QueueEntryRepository queueEntryRepository;
    private final TriageEngine engine;

    public TriageService(TriageRepository triageRepository, PatientRepository patientRepository,
                         QueueEntryRepository queueEntryRepository, TriageEngine engine) {
        this.triageRepository = triageRepository;
        this.patientRepository = patientRepository;
        this.queueEntryRepository = queueEntryRepository;
        this.engine = engine;
    }

    @Transactional
    public TriageDTO.TriageResponse triage(TriageDTO.CreateTriageRequest request) throws NotFoundException {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found: id=" + request.patientId()));

        boolean hasComorbidity = patient.getComorbidities() != null && !patient.getComorbidities().isBlank();
        int age = TriageEngine.ageFrom(patient.getBirthDate());
        TriageEngine.TriageResult result = engine.classify(request.symptoms(), age, hasComorbidity);

        // 2. Emissão da senha sequencial por categoria
        String ticketCode = nextTicket(result.severity());

        // 3. Persiste a triagem
        Triage triage = new Triage();
        triage.setPatientId(patient.getId());
        triage.setSymptoms(request.symptoms());
        triage.setSeverity(result.severity());
        triage.setRiskScore(result.riskScore());
        triage.setTicketCode(ticketCode);
        triage = triageRepository.save(triage);

        // 4. Enfileira com score efetivo inicial = score base (sem tempo de espera ainda)
        QueueEntry entry = new QueueEntry();
        entry.setTriageId(triage.getId());
        entry.setPatientId(patient.getId());
        entry.setBaseScore(result.riskScore());
        entry.setTimeWeight(BigDecimal.valueOf(TriageEngine.TIME_WEIGHTS.get(result.severity())));
        entry.setEffectiveScore(BigDecimal.valueOf(result.riskScore()));
        entry.setStatus("WAITING");
        queueEntryRepository.save(entry);

        return toResponse(triage);
    }

    private String nextTicket(String severity) {
        return triageRepository.findLastBySeverity(severity)
                .map(t -> increment(t.getTicketCode(), severity))
                .orElse(prefix(severity) + "-001");
    }

    private String increment(String previous, String severity) {
        // Formato base "R-001" -> extrai o sufixo numérico
        String seq = previous.split("-")[1];
        return prefix(severity) + "-" + String.format("%03d", Integer.parseInt(seq) + 1);
    }

    private String prefix(String severity) { return PREFIXES.get(severity); }

    private TriageDTO.TriageResponse toResponse(Triage triage) {
        return new TriageDTO.TriageResponse(
                triage.getId(), triage.getPatientId(), triage.getSymptoms(),
                triage.getSeverity(), triage.getRiskScore(), triage.getTicketCode(),
                triage.getCreatedAt()
        );
    }

}
