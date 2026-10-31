package br.ufla.gcc267.laboratorio.reserva.domain;

import java.time.LocalDateTime;
import java.util.UUID;

/** Agregado da Reserva: o uso de um recurso do Catalogo, referenciado so pelo id, em um periodo. */
public class Reserva {

    private static final int ANTECEDENCIA_MIN_HORAS = 1;
    private static final int ANTECEDENCIA_MAX_DIAS = 30;

    private final UUID id;
    private final UUID recursoId;
    private final Solicitante solicitante;
    private final Perfil perfil;
    private final Periodo periodo;
    private EstadoReserva estado;

    private Reserva(UUID recursoId, Solicitante solicitante, Perfil perfil, Periodo periodo) {
        this.id = UUID.randomUUID();
        this.recursoId = recursoId;
        this.solicitante = solicitante;
        this.perfil = perfil;
        this.periodo = periodo;
        this.estado = EstadoReserva.CONFIRMADA;
    }

    /**
     * Cria a reserva ja Confirmada. As regras que dependem de outras reservas (conflito e limite)
     * sao verificadas pelo servico de aplicacao, que enxerga o repositorio.
     */
    public static Reserva solicitar(
            UUID recursoId, Solicitante solicitante, Perfil perfil, Periodo periodo, LocalDateTime agora) {
        if (recursoId == null) {
            throw new DadosInvalidosException("O recurso é obrigatório.");
        }
        if (solicitante == null) {
            throw new DadosInvalidosException("O solicitante é obrigatório.");
        }
        if (perfil == null) {
            throw new DadosInvalidosException("O perfil é obrigatório.");
        }
        if (periodo == null) {
            throw new DadosInvalidosException("O período é obrigatório.");
        }
        if (periodo.inicio().isBefore(agora.plusHours(ANTECEDENCIA_MIN_HORAS))) {
            throw new DadosInvalidosException("A reserva deve ser feita com pelo menos 1 hora de antecedência.");
        }
        if (periodo.inicio().isAfter(agora.plusDays(ANTECEDENCIA_MAX_DIAS))) {
            throw new DadosInvalidosException("A reserva pode ser feita com no máximo 30 dias de antecedência.");
        }
        return new Reserva(recursoId, solicitante, perfil, periodo);
    }

    public void cancelar(Solicitante quemCancela, LocalDateTime agora) {
        if (!solicitante.equals(quemCancela)) {
            throw new CancelamentoNaoAutorizadoException();
        }
        if (estado == EstadoReserva.CANCELADA) {
            throw new TransicaoInvalidaException("A reserva já foi cancelada.");
        }
        if (!agora.isBefore(periodo.inicio())) {
            throw new TransicaoInvalidaException("Uma reserva já iniciada não pode ser cancelada.");
        }
        estado = EstadoReserva.CANCELADA;
    }

    /** Reserva ativa: Confirmada e com fim ainda nao passado. */
    public boolean estaAtiva(LocalDateTime agora) {
        return estado == EstadoReserva.CONFIRMADA && periodo.fim().isAfter(agora);
    }

    /** So reservas Confirmadas ocupam a agenda do recurso. */
    public boolean ocupa(Periodo outro) {
        return estado == EstadoReserva.CONFIRMADA && periodo.conflitaCom(outro);
    }

    public UUID getId() {
        return id;
    }

    public UUID getRecursoId() {
        return recursoId;
    }

    public Solicitante getSolicitante() {
        return solicitante;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public EstadoReserva getEstado() {
        return estado;
    }
}
