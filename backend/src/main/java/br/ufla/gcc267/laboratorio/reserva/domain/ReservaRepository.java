package br.ufla.gcc267.laboratorio.reserva.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservaRepository {

    void salvar(Reserva reserva);

    Optional<Reserva> buscarPorId(UUID id);

    /** Reservas do recurso, ordenadas pelo inicio. */
    List<Reserva> listarPorRecurso(UUID recursoId);

    /** Reservas do solicitante, ordenadas pelo inicio. */
    List<Reserva> listarPorSolicitante(Solicitante solicitante);
}
