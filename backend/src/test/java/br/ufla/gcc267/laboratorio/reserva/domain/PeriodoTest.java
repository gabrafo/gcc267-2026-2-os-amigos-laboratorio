package br.ufla.gcc267.laboratorio.reserva.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class PeriodoTest {

    private static Periodo periodo(String inicio, String fim) {
        return new Periodo(LocalDateTime.parse("2026-10-20T" + inicio), LocalDateTime.parse("2026-10-20T" + fim));
    }

    @Test
    void rejeitaFimAntesOuIgualAoInicio() {
        assertThatThrownBy(() -> periodo("16:00", "14:00")).isInstanceOf(DadosInvalidosException.class);
        assertThatThrownBy(() -> periodo("14:00", "14:00")).isInstanceOf(DadosInvalidosException.class);
    }

    @Test
    void respeitaHorarioDeFuncionamento() {
        assertThatThrownBy(() -> periodo("21:00", "23:00")).isInstanceOf(DadosInvalidosException.class);
        assertThatThrownBy(() -> periodo("06:30", "08:00")).isInstanceOf(DadosInvalidosException.class);
        assertThat(periodo("20:00", "22:00")).isNotNull();
        assertThat(periodo("07:00", "08:00")).isNotNull();
    }

    @Test
    void rejeitaDiasDiferentes() {
        assertThatThrownBy(() -> new Periodo(
                LocalDateTime.parse("2026-10-20T21:00"), LocalDateTime.parse("2026-10-21T08:00")))
                .isInstanceOf(DadosInvalidosException.class);
    }

    @Test
    void respeitaDuracaoDe30MinutosA4Horas() {
        assertThatThrownBy(() -> periodo("14:00", "14:29")).isInstanceOf(DadosInvalidosException.class);
        assertThatThrownBy(() -> periodo("08:00", "13:00")).isInstanceOf(DadosInvalidosException.class);
        assertThat(periodo("14:00", "14:30")).isNotNull();
        assertThat(periodo("08:00", "12:00")).isNotNull();
    }

    @Test
    void periodosEncostadosNaoConflitam() {
        assertThat(periodo("14:00", "16:00").conflitaCom(periodo("16:00", "18:00"))).isFalse();
        assertThat(periodo("14:00", "16:00").conflitaCom(periodo("15:00", "17:00"))).isTrue();
        assertThat(periodo("14:00", "16:00").conflitaCom(periodo("14:30", "15:00"))).isTrue();
    }
}
