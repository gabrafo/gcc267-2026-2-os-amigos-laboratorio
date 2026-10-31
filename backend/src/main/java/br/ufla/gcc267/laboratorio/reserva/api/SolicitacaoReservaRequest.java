package br.ufla.gcc267.laboratorio.reserva.api;

import java.time.LocalDateTime;
import java.util.UUID;

import br.ufla.gcc267.laboratorio.reserva.domain.Perfil;

public record SolicitacaoReservaRequest(
        UUID recursoId, String solicitante, Perfil perfil, LocalDateTime inicio, LocalDateTime fim) {
}
