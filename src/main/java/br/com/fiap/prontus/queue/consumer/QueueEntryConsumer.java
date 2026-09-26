package br.com.fiap.prontus.queue.consumer;

import br.com.fiap.prontus.queue.model.QueueEntry;
import br.com.fiap.prontus.queue.repository.QueueEntryRepository;
import br.com.fiap.prontus.queue.stream.QueueEventsService;
import br.com.fiap.prontus.shared.events.PatientTriagedEvent;
import br.com.fiap.prontus.shared.events.TriageEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class QueueEntryConsumer {

    private static final Logger log = LoggerFactory.getLogger(QueueEntryConsumer.class);

    private final QueueEntryRepository repository;
    private final QueueEventsService queueEventsService;

    public QueueEntryConsumer(QueueEntryRepository repository,
                              QueueEventsService queueEventsService) {
        this.repository = repository;
        this.queueEventsService = queueEventsService;
    }

    @KafkaListener(topics = TriageEventPublisher.TOPIC, groupId = "prontus-queue")
    @Transactional
    public void onPatientTriaged(PatientTriagedEvent event) {
        log.info("Received PatientTriagedEvent: triageId={}, severity={}",
                event.triageId(), event.severity());

        // Idempotency: skip if already enqueued (protects against duplicates/retries)
        if (repository.findByTriageId(event.triageId()).isPresent()) {
            log.info("Triage already enqueued, skipping duplicate: triageId={}",
                    event.triageId());
            return;
        }

        QueueEntry entry = new QueueEntry();
        entry.setTriageId(event.triageId());
        entry.setPatientId(event.patientId());
        entry.setTicketCode(event.ticketCode());
        entry.setBaseScore(event.riskScore());
        entry.setTimeWeight(event.timeWeight());
        entry.setEffectiveScore(java.math.BigDecimal.valueOf(event.riskScore()));
        entry.setStatus("WAITING");
        entry.setEnqueuedAt(LocalDateTime.now());
        repository.save(entry);

        log.info("QueueEntry created via Kafka: triageId={}, score={}",
                event.triageId(), event.riskScore());

        // Real-time: push the updated queue to all SSE clients
        queueEventsService.broadcastQueueUpdate();
    }
}
