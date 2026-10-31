package br.ufla.gcc267.laboratorio.reserva.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ReservaTest {

    private static final LocalDateTime AGORA = LocalDateTime.parse("2026-10-19T10:00");
    private static final Solicitante JOAO = new Solicitante("joao@ufla.br");

    private static Reserva reservaDas(String inicio, String fim) {
        Periodo periodo = new Periodo(LocalDateTime.parse(inicio), LocalDateTime.parse(fim));
        return Reserva.solicitar(UUID.randomUUID(), JOAO, Perfil.DOCENTE, periodo, AGORA);
    }

    @Test
    void solicitanteIgnoraEspacosECaixa() {
        assertThat(new Solicitante(" JOAO@ufla.br ")).isEqualTo(JOAO);
    }

    @Test
    void novaReservaEstaConfirmada() {
        assertThat(reservaDas("2026-10-20T14:00", "2026-10-20T16:00").getEstado())
                .isEqualTo(EstadoReserva.CONFIRMADA);
    }

    @Test
    void antecedenciaMinimaDeUmaHora() {
        assertThatThrownBy(() -> reservaDas("2026-10-19T10:30", "2026-10-19T11:30"))
                .isInstanceOf(DadosInvalidosException.class);
        assertThat(reservaDas("2026-10-19T11:00", "2026-10-19T12:00")).isNotNull();
    }

    @Test
    void antecedenciaMaximaDe30Dias() {
        assertThat(reservaDas("2026-11-18T10:00", "2026-11-18T11:00")).isNotNull();
        assertThatThrownBy(() -> reservaDas("2026-11-18T10:30", "2026-11-18T11:30"))
                .isInstanceOf(DadosInvalidosException.class);
    }

    @Test
    void soQuemSolicitouCancela() {
        Reserva reserva = reservaDas("2026-10-20T14:00", "2026-10-20T16:00");

        assertThatThrownBy(() -> reserva.cancelar(new Solicitante("maria@ufla.br"), AGORA))
                .isInstanceOf(CancelamentoNaoAutorizadoException.class);

        reserva.cancelar(new Solicitante(" Joao@UFLA.br"), AGORA);
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
    }

    @Test
    void naoCancelaDuasVezesNemDepoisDoInicio() {
        Reserva reserva = reservaDas("2026-10-20T14:00", "2026-10-20T16:00");

        assertThatThrownBy(() -> reserva.cancelar(JOAO, LocalDateTime.parse("2026-10-20T14:00")))
                .isInstanceOf(TransicaoInvalidaException.class);

        reserva.cancelar(JOAO, AGORA);
        assertThatThrownBy(() -> reserva.cancelar(JOAO, AGORA)).isInstanceOf(TransicaoInvalidaException.class);
    }

    @Test
    void reservaCanceladaNaoOcupaAgenda() {
        Reserva reserva = reservaDas("2026-10-20T14:00", "2026-10-20T16:00");
        Periodo mesmoHorario = reserva.getPeriodo();

        assertThat(reserva.ocupa(mesmoHorario)).isTrue();
        reserva.cancelar(JOAO, AGORA);
        assertThat(reserva.ocupa(mesmoHorario)).isFalse();
        assertThat(reserva.estaAtiva(AGORA)).isFalse();
    }
}
