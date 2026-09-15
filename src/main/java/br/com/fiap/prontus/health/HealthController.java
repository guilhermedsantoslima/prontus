package br.com.fiap.prontus.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, String>health(){
        return  Map.of(
                "status", "UP",
                "service", "Prontus - Smart Triage for SUS",
                "version", "0.0.1"
        );
    }
}
