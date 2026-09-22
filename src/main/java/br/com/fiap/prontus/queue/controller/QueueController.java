package br.com.fiap.prontus.queue.controller;

import br.com.fiap.prontus.queue.model.QueueEntry;
import br.com.fiap.prontus.queue.repository.QueueEntryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueEntryRepository repository;

    public QueueController(QueueEntryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<QueueEntry> waiting(){
        return repository.findAll().stream()
                .filter(e -> "WAITING".equals(e.getStatus()))
                .sorted((a, b) -> b.getEffectiveScore().compareTo(a.getEffectiveScore()))
                .toList();
    }
}
