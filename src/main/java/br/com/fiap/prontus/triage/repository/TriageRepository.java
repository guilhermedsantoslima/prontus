package br.com.fiap.prontus.triage.repository;

import br.com.fiap.prontus.triage.model.Triage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TriageRepository extends JpaRepository<Triage, Long> {

    @Query("SELECT t FROM Triage t WHERE t.severity = :severity ORDER BY t.id DESC LIMIT 1")
    Optional<Triage> findLastBySeverity(String severity);
}
