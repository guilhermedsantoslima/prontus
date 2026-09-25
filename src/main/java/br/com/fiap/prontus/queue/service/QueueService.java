package br.com.fiap.prontus.queue.service;

import br.com.fiap.prontus.queue.model.QueueEntry;
import br.com.fiap.prontus.queue.repository.QueueEntryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class QueueService {

    private static final Logger log = LoggerFactory.getLogger(QueueService.class);
    private static final String STATUS_WAITING = "WAITING";
    private static final String STATUS_CALLED = "CALLED";

    private final QueueEntryRepository repository;

    public QueueService(QueueEntryRepository repository) {
        this.repository = repository;
    }

    /** Calls the next patient in line (highest effective score first). */
    @Transactional
    public QueueEntry callNext() {
        QueueEntry next = repository
                .findFirstByStatusOrderByEffectiveScoreDescEnqueuedAtAsc(STATUS_WAITING)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No patients waiting in the queue"));

        next.setStatus(STATUS_CALLED);
        next.setCalledAt(LocalDateTime.now());
        repository.save(next);

        log.info("Patient called: entryId={}, triageId={}, effectiveScore={}",
                next.getId(), next.getTriageId(), next.getEffectiveScore());
        return next;
    }

    /** Calls a specific patient by queue entry id (override for priority cases). */
    @Transactional
    public QueueEntry callById(Long entryId) {
        QueueEntry entry = repository.findById(entryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Queue entry not found: " + entryId));

        if (!STATUS_WAITING.equals(entry.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Entry is not in WAITING status: " + entryId);
        }

        entry.setStatus(STATUS_CALLED);
        entry.setCalledAt(LocalDateTime.now());
        repository.save(entry);

        log.info("Patient called by id: entryId={}, triageId={}",
                entry.getId(), entry.getTriageId());
        return entry;
    }
}
