package br.com.fiap.prontus.patient.service;

import br.com.fiap.prontus.patient.dto.PatientDTO;
import br.com.fiap.prontus.patient.model.Patient;
import br.com.fiap.prontus.patient.repository.PatientRepository;
import br.com.fiap.prontus.shared.exception.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository repository;

    public PatientService(PatientRepository repository){
        this.repository = repository;
    }

    @Transactional
    public PatientDTO.PatientResponse create(PatientDTO.CreatePatientRequest request){
        Patient patient = new Patient(
                request.fullName(),
                request.birthDate(),
                request.comorbidities()
        );
        return toResponse(repository.save(patient));
    }

    @Transactional(readOnly = true)
    public PatientDTO.PatientResponse findById(Long id) throws NotFoundException {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Patient not found id=" + id));
    }

    @Transactional(readOnly = true)
    public List<PatientDTO.PatientResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream().map(this::toResponse).toList();
    }
    @Transactional
    public PatientDTO.PatientResponse update(Long id, PatientDTO.CreatePatientRequest request) throws NotFoundException {
        Patient patient = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found id=" + id));

        patient.setFullName(request.fullName());
        patient.setBirthDate(request.birthDate());
        patient.setComorbidities(request.comorbidities());
        return toResponse(repository.save(patient));
    }

    @Transactional
    public void delete(Long id) throws NotFoundException {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Patient not found: id=" + id);
        }
        repository.deleteById(id);
    }

    private PatientDTO.PatientResponse toResponse(Patient patient) {
        return new PatientDTO.PatientResponse(
                patient.getId(),
                patient.getFullName(),
                patient.getBirthDate(),
                patient.getComorbidities(),
                patient.getCreatedAt()
        );
    }
}
