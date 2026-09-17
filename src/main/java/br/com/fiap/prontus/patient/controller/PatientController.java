package br.com.fiap.prontus.patient.controller;

import br.com.fiap.prontus.patient.dto.PatientDTO;
import br.com.fiap.prontus.patient.service.PatientService;
import br.com.fiap.prontus.shared.exception.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PatientDTO.PatientResponse> create(@Valid @RequestBody PatientDTO.CreatePatientRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public List<PatientDTO.PatientResponse> findAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PatientDTO.PatientResponse findById(@PathVariable Long id) throws NotFoundException {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public PatientDTO.PatientResponse update(@PathVariable Long id, @Valid @RequestBody PatientDTO.CreatePatientRequest request) throws NotFoundException {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws NotFoundException {
        service.delete(id);
    }
}
