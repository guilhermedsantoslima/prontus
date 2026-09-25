package br.com.fiap.prontus.queue.controller;

import br.com.fiap.prontus.queue.model.QueueEntry;
import br.com.fiap.prontus.queue.repository.QueueEntryRepository;
import br.com.fiap.prontus.queue.service.QueueService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
public class QueueController {
    private final QueueEntryRepository repository;
    private final QueueService queueService;

    public QueueController(QueueEntryRepository repository, QueueService queueService) {
        this.repository = repository;
        this.queueService = queueService;
    }

    @GetMapping
    public List<QueueEntry> waiting() {
        return repository.findByStatusOrderByEffectiveScoreDescEnqueuedAtAsc("WAITING");
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
