package br.com.fiap.prontus.triage.engine;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.Set;

@Component
public class TriageEngine {

    public record TriageResult(String severity, int riskScore){}

    private static final Map<String, Integer> SEVERITY_SCORES = Map.of(
            "RED", 1000, "ORANGE", 500, "YELLOW", 200, "GREEN", 50, "BLUE", 10
    );

    public static final Map<String, Double> TIME_WEIGHTS = Map.of(
         "RED", 0.0, "ORANGE", 0.02,"YELLOW", 0.5, "GREEN", 1.0, "BLUE", 0.01
    );

    private static final Map<String, Set<String>> CRITICAL_RULES = Map.of(
            "RED", Set.of("dor toracica", "parada cardiorrespiratoria", "convulsao", "inconsciencia", "hemorragia grave"),
            "ORANGE", Set.of("dor abdominal intensa", "febre alta com rash", "desmaio", "fratura exposta", "falta de ar intensa"),
            "YELLOW", Set.of("vomito persistente", "febre alta", "desidratacao", "hemorragia moderada"),
            "GREEN", Set.of("febre leve", "dor de cabeca leve", "resfriado", "tosse")
    );

    public TriageResult classify(String symptons, int ageYears, boolean hasComorbidity){
        String symptomText = symptons.toLowerCase();

        String category = "BLUE";
        for (var entry : CRITICAL_RULES.entrySet()){
            for (String keyword : entry.getValue()){
                if (symptomText.contains(keyword)){
                    category = entry.getKey();
                    break;
                }
            }
            if (!"BLUE".equals(category)) break;
        }

        int score = SEVERITY_SCORES.get(category);

        if (("YELLOW".equals(category) || "GREEN".equals(category)) && hasComorbidity) {
            category = promote(category);
            score = SEVERITY_SCORES.get(category);
        }
        if (("GREEN".equals(category) || "ORANGE".equals(category)) && (ageYears <= 3 || ageYears >= 75)) {
            category = promote(category);
            score = SEVERITY_SCORES.get(category);
        }

        return new TriageResult(category, score);
    }

    private String promote(String severity){
        return switch (severity){
            case "BLUE" -> "GREEN";
            case "GREEN" -> "YELLOW";
            case "YELLOW" -> "ORANGE";
            default -> severity;
        };
    }

    public static int ageFrom(LocalDate birthDate){
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
