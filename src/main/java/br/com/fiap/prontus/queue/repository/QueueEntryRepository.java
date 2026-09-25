package br.com.fiap.prontus.queue.repository;

import br.com.fiap.prontus.queue.model.QueueEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {
    Optional<QueueEntry> findByTriageId(Long triageId);
    Optional<QueueEntry> findFirstByTriageIdOrderByEnqueuedAtDesc(Long triageId);
    List<QueueEntry> findByStatusOrderByEffectiveScoreDescEnqueuedAtAsc(String status);
    Optional<QueueEntry> findFirstByStatusOrderByEffectiveScoreDescEnqueuedAtAsc(String status);
}
