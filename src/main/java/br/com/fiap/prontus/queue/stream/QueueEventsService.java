package br.com.fiap.prontus.queue.stream;

import br.com.fiap.prontus.queue.model.QueueEntry;
import br.com.fiap.prontus.queue.repository.QueueEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class QueueEventsService {

    private static final Logger log = LoggerFactory.getLogger(QueueEventsService.class);
    private static final String STATUS_WAITING = "WAITING";

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final QueueEntryRepository repository;

    public QueueEventsService(QueueEntryRepository repository) {
        this.repository = repository;
    }

    public SseEmitter register() {
        SseEmitter emitter = new SseEmitter(0L); // 0 = no timeout
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        log.info("SSE client connected. Active listeners: {}", emitters.size());
        return emitter;
    }

    @Transactional(readOnly = true)
    public void broadcastQueueUpdate() {
        List<QueueEntry> queue = repository
                .findByStatusOrderByEffectiveScoreDescEnqueuedAtAsc(STATUS_WAITING);

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("queue-update")
                        .data(queue, MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                emitters.remove(emitter); // Dead client — clean up
            }
        }
        log.debug("Queue update broadcast to {} listeners", emitters.size());
    }
}
