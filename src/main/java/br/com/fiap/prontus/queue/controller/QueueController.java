package br.com.fiap.prontus.queue.controller;

import br.com.fiap.prontus.queue.model.QueueEntry;
import br.com.fiap.prontus.queue.repository.QueueEntryRepository;
import br.com.fiap.prontus.queue.service.QueueService;
import br.com.fiap.prontus.queue.stream.QueueEventsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
public class QueueController {
    private final QueueEntryRepository repository;
    private final QueueService queueService;
    private final QueueEventsService queueEventsService;

    public QueueController(QueueEntryRepository repository, QueueService queueService, QueueEventsService queueEventsService) {
        this.repository = repository;
        this.queueService = queueService;
        this.queueEventsService = queueEventsService;
    }

    @GetMapping
    public List<QueueEntry> waiting() {
        return repository.findByStatusOrderByEffectiveScoreDescEnqueuedAtAsc("WAITING");
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return queueEventsService.register();
    }

    @PostMapping("/call-next")
    public QueueEntry callNext() {
        return queueService.callNext();
    }

    @PostMapping("/{id}/call")
    public QueueEntry callById(@PathVariable Long id) {
        return queueService.callById(id);
    }
}
