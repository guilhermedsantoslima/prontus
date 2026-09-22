package br.com.fiap.prontus.triage.controller;

import br.com.fiap.prontus.shared.exception.NotFoundException;
import br.com.fiap.prontus.triage.dto.TriageDTO;
import br.com.fiap.prontus.triage.service.TriageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/triages")
public class TriageController {

    private final TriageService service;

    public TriageController(TriageService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TriageDTO.TriageResponse> triage(@Valid @RequestBody TriageDTO.CreateTriageRequest request) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.triage(request));
    }
}
