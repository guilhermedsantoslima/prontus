package br.com.fiap.prontus.queue.repository;

import br.com.fiap.prontus.queue.model.QueueEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {
}
