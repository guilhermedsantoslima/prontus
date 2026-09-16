package br.com.fiap.prontus.queue.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_entries")
public class QueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "triage_id", nullable = false)
    private Long triageId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "base_score", nullable = false)
    private Integer baseScore;

    @Column(name = "time_weight", nullable = false, precision = 6, scale = 3)
    private BigDecimal timeWeight;

    @Column(name = "effective_score", nullable = false, precision = 10, scale = 3)
    private BigDecimal effectiveScore;

    @Column(nullable = false, length = 15)
    private String status = "WAITING";

    @Column(name = "enqueued_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime enqueuedAt;

    @Column(name = "called_at")
    private LocalDateTime calledAt;

    protected QueueEntry(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTriageId() {
        return triageId;
    }

    public void setTriageId(Long triageId) {
        this.triageId = triageId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Integer getBaseScore() {
        return baseScore;
    }

    public void setBaseScore(Integer baseScore) {
        this.baseScore = baseScore;
    }

    public BigDecimal getTimeWeight() {
        return timeWeight;
    }

    public void setTimeWeight(BigDecimal timeWeight) {
        this.timeWeight = timeWeight;
    }

    public BigDecimal getEffectiveScore() {
        return effectiveScore;
    }

    public void setEffectiveScore(BigDecimal effectiveScore) {
        this.effectiveScore = effectiveScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEnqueuedAt() {
        return enqueuedAt;
    }

    public void setEnqueuedAt(LocalDateTime enqueuedAt) {
        this.enqueuedAt = enqueuedAt;
    }

    public LocalDateTime getCalledAt() {
        return calledAt;
    }

    public void setCalledAt(LocalDateTime calledAt) {
        this.calledAt = calledAt;
    }
}
