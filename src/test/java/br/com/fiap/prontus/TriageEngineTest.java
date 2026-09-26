package br.com.fiap.prontus;

import br.com.fiap.prontus.triage.engine.TriageEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriageEngineTest {

    private final TriageEngine engine = new TriageEngine();

    private int ageOf(int yearsAgo) {
        return TriageEngine.ageFrom(LocalDate.now().minusYears(yearsAgo));
    }

    @Test
    @DisplayName("Chest pain classifies as RED with maximum score")
    void shouldClassifyChestPainAsRed() {
        var result = engine.classify("dor toracica", 45, false);
        assertEquals("RED", result.severity());
        assertEquals(1000, result.riskScore());
    }

    @Test
    @DisplayName("Mild cold classifies as GREEN with low score")
    void shouldClassifyColdAsGreen() {
        var result = engine.classify("resfriado", 30, false);
        assertEquals("GREEN", result.severity());
        assertEquals(50, result.riskScore());
    }

    @Test
    @DisplayName("Unrecognized symptoms default to BLUE")
    void shouldDefaultToBlueWhenNoKeywordMatches() {
        var result = engine.classify("quero tirar Duvida sobre receita", 40, false);
        assertEquals("BLUE", result.severity());
        assertEquals(10, result.riskScore());
    }

    @Test
    @DisplayName("Comorbidity promotes GREEN to YELLOW")
    void shouldPromoteGreenWithComorbidity() {
        var result = engine.classify("resfriado", 30, true);
        assertEquals("YELLOW", result.severity());
        assertEquals(200, result.riskScore());
    }

    @Test
    @DisplayName("Elderly patient promotes ORANGE to RED")
    void shouldPromoteOrangeToRedForElderly() {
        var result = engine.classify("desmaio", ageOf(78), false);
        assertEquals("RED", result.severity());
        assertEquals(1000, result.riskScore());
    }

    @Test
    @DisplayName("Infant promotes GREEN to YELLOW")
    void shouldPromoteGreenForInfant() {
        var result = engine.classify("resfriado", ageOf(2), false);
        assertEquals("YELLOW", result.severity());
        assertEquals(200, result.riskScore());
    }

    @Test
    @DisplayName("Time weights cover all five severities")
    void shouldHaveTimeWeightForAllSeverities() {
        for (String severity : new String[]{"RED", "ORANGE", "YELLOW", "GREEN", "BLUE"}) {
            assertTrue(TriageEngine.TIME_WEIGHTS.containsKey(severity),
                    "Missing time weight for " + severity);
        }
        // RED does not accelerate (already maximum priority)
        assertEquals(0, TriageEngine.TIME_WEIGHTS.get("RED").compareTo(java.math.BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Keyword match is case-insensitive")
    void shouldBeCaseInsensitive() {
        var result = engine.classify("DOR TORACICA intensa", 50, false);
        assertEquals("RED", result.severity());
    }
}