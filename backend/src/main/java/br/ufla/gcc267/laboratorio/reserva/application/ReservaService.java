package br.ufla.gcc267.laboratorio.reserva.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.ufla.gcc267.laboratorio.reserva.domain.Perfil;
import br.ufla.gcc267.laboratorio.reserva.domain.Periodo;
import br.ufla.gcc267.laboratorio.reserva.domain.Reserva;
import br.ufla.gcc267.laboratorio.reserva.domain.ReservaRepository;
import br.ufla.gcc267.laboratorio.reserva.domain.Solicitante;

/** Casos de uso da Reserva (SPEC 0002). */
@Service
public class ReservaService {

    /** Fuso do campus. O "agora" e calculado nele, qualquer que seja o fuso do servidor. */
    public static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private final ReservaRepository repositorio;
    private final ConsultaCatalogo catalogo;
    private final Clock relogio;

    public ReservaService(ReservaRepository repositorio, ConsultaCatalogo catalogo, Clock relogio) {
        this.repositorio = repositorio;
        this.catalogo = catalogo;
        this.relogio = relogio.withZone(FUSO);
    }

    public Reserva solicitar(UUID recursoId, String solicitante, Perfil perfil, LocalDateTime inicio,
            LocalDateTime fim) {
        Reserva reserva = Reserva.solicitar(
                recursoId, new Solicitante(solicitante), perfil, new Periodo(inicio, fim), agora());

        // Passo 3: le outro contexto, por isso fica fora do bloco atomico.
        switch (catalogo.consultar(recursoId, perfil)) {
            case INEXISTENTE -> throw new RecursoNaoEncontradoException(recursoId);
            case NAO_RESERVAVEL -> throw new SolicitacaoRecusadaException(
                    "O recurso não está disponível ou não pode ser reservado pelo perfil " + perfil + ".");
            case RESERVAVEL -> { }
            default -> throw new IllegalStateException();
        }

        confirmar(reserva);
        return reserva;
    }

    /**
     * Passos 4 a 6. synchronized para que duas solicitacoes simultaneas nao passem juntas pelo
     * limite ou pelo conflito (repositorio em memoria).
     */
    private synchronized void confirmar(Reserva reserva) {
        LocalDateTime agora = agora();
        long ativas = repositorio.listarPorSolicitante(reserva.getSolicitante()).stream()
                .filter(r -> r.estaAtiva(agora))
                .count();
        if (ativas >= reserva.getPerfil().getLimiteDeReservasAtivas()) {
            throw new SolicitacaoRecusadaException("Limite de "
                    + reserva.getPerfil().getLimiteDeReservasAtivas() + " reservas ativas atingido.");
        }
        boolean conflita = repositorio.listarPorRecurso(reserva.getRecursoId()).stream()
                .anyMatch(r -> r.ocupa(reserva.getPeriodo()));
        if (conflita) {
            throw new ConflitoDeAgendaException();
        }
        repositorio.salvar(reserva);
    }

    public synchronized Reserva cancelar(UUID id, String solicitante) {
        Reserva reserva = repositorio.buscarPorId(id).orElseThrow(() -> new ReservaNaoEncontradaException(id));
        reserva.cancelar(new Solicitante(solicitante), agora());
        repositorio.salvar(reserva);
        return reserva;
    }

    public List<Reserva> agendaDoRecurso(UUID recursoId) {
        return repositorio.listarPorRecurso(recursoId);
    }

    public List<Reserva> reservasDoSolicitante(String solicitante) {
        return repositorio.listarPorSolicitante(new Solicitante(solicitante));
    }

    private LocalDateTime agora() {
        return LocalDateTime.now(relogio);
    }
}
