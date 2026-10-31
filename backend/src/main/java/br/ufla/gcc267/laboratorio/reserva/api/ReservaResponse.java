package br.ufla.gcc267.laboratorio.reserva.api;

import java.time.LocalDateTime;
import java.util.UUID;

import br.ufla.gcc267.laboratorio.reserva.domain.EstadoReserva;
import br.ufla.gcc267.laboratorio.reserva.domain.Perfil;
import br.ufla.gcc267.laboratorio.reserva.domain.Reserva;

public record ReservaResponse(
        UUID id,
        UUID recursoId,
        String solicitante,
        Perfil perfil,
        LocalDateTime inicio,
        LocalDateTime fim,
        EstadoReserva estado) {

    public static ReservaResponse de(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getRecursoId(),
                reserva.getSolicitante().identificacao(),
                reserva.getPerfil(),
                reserva.getPeriodo().inicio(),
                reserva.getPeriodo().fim(),
                reserva.getEstado());
    }
}
