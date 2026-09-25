package br.com.fiap.prontus.queue.scheduler;

import br.com.fiap.prontus.queue.model.QueueEntry;
import br.com.fiap.prontus.queue.repository.QueueEntryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueueReorderingScheduler {

    private static final Logger log = LoggerFactory.getLogger(QueueReorderingScheduler.class);
    private static final String STATUS_WAITING = "WAITING";

    private final QueueEntryRepository repository;

    public QueueReorderingScheduler(QueueEntryRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void reevaluateWaitingQueue() {
        List<QueueEntry> waiting = repository
                .findByStatusOrderByEffectiveScoreDescEnqueuedAtAsc(STATUS_WAITING);

        if (waiting.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        boolean changed = false;

        for (QueueEntry entry : waiting) {
            long waitingMinutes = Duration.between(entry.getEnqueuedAt(), now).toMinutes();

            BigDecimal boost = entry.getTimeWeight()
                    .multiply(BigDecimal.valueOf(waitingMinutes))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal newScore = BigDecimal.valueOf(entry.getBaseScore()).add(boost);

            if (newScore.compareTo(entry.getEffectiveScore()) != 0) {
                entry.setEffectiveScore(newScore);
                changed = true;
                log.debug("Rescored entry: triageId={}, base={}, waited={}min, effective={}",
                        entry.getTriageId(), entry.getBaseScore(), waitingMinutes, newScore);
            }
        }

        if (changed) {
            repository.saveAll(waiting);
            log.info("Queue reordered: {} waiting entries re-evaluated", waiting.size());
            // Phase 3 (next step): publish QueueReorderedEvent here for SSE notifications
        }
    }
}
