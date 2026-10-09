package br.ufla.gcc267.laboratorio.status;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint simples para o front-end saber se a API esta no ar. */
@RestController
public class StatusController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
