package br.ufla.gcc267.laboratorio.reserva.infrastructure;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.springframework.stereotype.Repository;

import br.ufla.gcc267.laboratorio.reserva.domain.Reserva;
import br.ufla.gcc267.laboratorio.reserva.domain.ReservaRepository;
import br.ufla.gcc267.laboratorio.reserva.domain.Solicitante;

/** Guarda as reservas em memoria ate o banco de dados ser definido em ADR. */
@Repository
public class ReservaRepositoryEmMemoria implements ReservaRepository {

    private static final Comparator<Reserva> POR_INICIO = Comparator.comparing(r -> r.getPeriodo().inicio());

    private final Map<UUID, Reserva> reservas = new ConcurrentHashMap<>();

    @Override
    public void salvar(Reserva reserva) {
        reservas.put(reserva.getId(), reserva);
    }

    @Override
    public Optional<Reserva> buscarPorId(UUID id) {
        return Optional.ofNullable(reservas.get(id));
    }

    @Override
    public List<Reserva> listarPorRecurso(UUID recursoId) {
        return filtrar(r -> r.getRecursoId().equals(recursoId));
    }

    @Override
    public List<Reserva> listarPorSolicitante(Solicitante solicitante) {
        return filtrar(r -> r.getSolicitante().equals(solicitante));
    }

    private List<Reserva> filtrar(Predicate<Reserva> filtro) {
        return reservas.values().stream().filter(filtro).sorted(POR_INICIO).toList();
    }
}
