package br.ufla.gcc267.laboratorio.reserva.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Intervalo de uso de um recurso, no fuso do campus. A construcao valida as regras que dependem so
 * do periodo: horario de funcionamento, mesmo dia e duracao (SPEC 0002).
 */
public record Periodo(LocalDateTime inicio, LocalDateTime fim) {

    private static final LocalTime ABERTURA = LocalTime.of(7, 0);
    private static final LocalTime FECHAMENTO = LocalTime.of(22, 0);
    private static final Duration DURACAO_MIN = Duration.ofMinutes(30);
    private static final Duration DURACAO_MAX = Duration.ofHours(4);

    public Periodo {
        if (inicio == null || fim == null) {
            throw new DadosInvalidosException("Início e fim são obrigatórios.");
        }
        if (!fim.isAfter(inicio)) {
            throw new DadosInvalidosException("O fim deve ser depois do início.");
        }
        if (!inicio.toLocalDate().equals(fim.toLocalDate())) {
            throw new DadosInvalidosException("Início e fim devem ser no mesmo dia.");
        }
        if (inicio.toLocalTime().isBefore(ABERTURA) || fim.toLocalTime().isAfter(FECHAMENTO)) {
            throw new DadosInvalidosException("A reserva deve ficar entre 07:00 e 22:00.");
        }
        Duration duracao = Duration.between(inicio, fim);
        if (duracao.compareTo(DURACAO_MIN) < 0 || duracao.compareTo(DURACAO_MAX) > 0) {
            throw new DadosInvalidosException("A duração deve ser de 30 minutos a 4 horas.");
        }
    }

    /** Periodos encostados (um termina quando o outro comeca) nao conflitam. */
    public boolean conflitaCom(Periodo outro) {
        return inicio.isBefore(outro.fim) && outro.inicio.isBefore(fim);
    }
}
