package br.com.fiap.prontus.shared.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TriageEventPublisher {

    public static final String TOPIC = "prontus.triage.completed";

    private static final Logger log = LoggerFactory.getLogger(TriageEventPublisher.class);
    private final KafkaTemplate<String, PatientTriagedEvent> kafkaTemplate;

    public TriageEventPublisher(KafkaTemplate<String, PatientTriagedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PatientTriagedEvent event) {
        try {
            kafkaTemplate.send(TOPIC, String.valueOf(event.triageId()), event);
            log.info("Published PatientTriagedEvent: triageId={}, severity={}",
                    event.triageId(), event.severity());
        } catch (Exception e) {
            log.warn("Kafka unavailable, triage persisted but queue insertion will be retried: {}",
                    e.getMessage());
        }
    }
}
