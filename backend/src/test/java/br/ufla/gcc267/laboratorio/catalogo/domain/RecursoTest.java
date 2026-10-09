package br.ufla.gcc267.laboratorio.catalogo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RecursoTest {

    private Recurso laboratorio() {
        return Recurso.cadastrar("Lab Redes", TipoRecurso.LABORATORIO, "DCC - sala 105", 30);
    }

    @Test
    void cadastroValidoComecaDisponivel() {
        Recurso recurso = laboratorio();

        assertThat(recurso.getId()).isNotNull();
        assertThat(recurso.getEstado()).isEqualTo(EstadoOperacional.DISPONIVEL);
    }

    @Test
    void nomeERemovidoDosEspacosDasPontas() {
        Recurso recurso = Recurso.cadastrar("  Lab Redes  ", TipoRecurso.LABORATORIO, "DCC", 30);

        assertThat(recurso.getNome()).isEqualTo("Lab Redes");
    }

    @Test
    void rejeitaNomeCurto() {
        assertThatThrownBy(() -> Recurso.cadastrar("La", TipoRecurso.LABORATORIO, "DCC", 30))
                .isInstanceOf(DadosInvalidosException.class);
    }

    @Test
    void rejeitaLocalizacaoVazia() {
        assertThatThrownBy(() -> Recurso.cadastrar("Lab Redes", TipoRecurso.LABORATORIO, " ", 30))
                .isInstanceOf(DadosInvalidosException.class);
    }

    @Test
    void rejeitaCapacidadeForaDosLimites() {
        assertThatThrownBy(() -> Recurso.cadastrar("Lab Redes", TipoRecurso.LABORATORIO, "DCC", 0))
                .isInstanceOf(DadosInvalidosException.class);
        assertThatThrownBy(() -> Recurso.cadastrar("Lab Redes", TipoRecurso.LABORATORIO, "DCC", 101))
                .isInstanceOf(DadosInvalidosException.class);
    }

    @Test
    void rejeitaEquipamentoComCapacidadeDiferenteDeUm() {
        assertThatThrownBy(() -> Recurso.cadastrar("Osciloscopio", TipoRecurso.EQUIPAMENTO, "DCC", 5))
                .isInstanceOf(DadosInvalidosException.class);
    }

    @Test
    void vaiParaManutencaoEVolta() {
        Recurso recurso = laboratorio();

        recurso.enviarParaManutencao();
        assertThat(recurso.getEstado()).isEqualTo(EstadoOperacional.EM_MANUTENCAO);

        recurso.liberar();
        assertThat(recurso.getEstado()).isEqualTo(EstadoOperacional.DISPONIVEL);
    }

    @Test
    void naoLiberaRecursoDisponivel() {
        Recurso recurso = laboratorio();

        assertThatThrownBy(recurso::liberar).isInstanceOf(TransicaoInvalidaException.class);
        assertThat(recurso.getEstado()).isEqualTo(EstadoOperacional.DISPONIVEL);
    }

    @Test
    void recursoBaixadoNaoMudaMaisDeEstado() {
        Recurso recurso = laboratorio();
        recurso.darBaixa();

        assertThatThrownBy(recurso::enviarParaManutencao).isInstanceOf(TransicaoInvalidaException.class);
        assertThatThrownBy(recurso::liberar).isInstanceOf(TransicaoInvalidaException.class);
        assertThatThrownBy(recurso::darBaixa).isInstanceOf(TransicaoInvalidaException.class);
        assertThat(recurso.getEstado()).isEqualTo(EstadoOperacional.BAIXADO);
    }

    @Test
    void laboratorioSoPodeSerReservadoPorDocenteOuCoordenacao() {
        Recurso recurso = laboratorio();

        assertThat(recurso.podeSerReservadoPor(Perfil.DOCENTE)).isTrue();
        assertThat(recurso.podeSerReservadoPor(Perfil.COORDENACAO)).isTrue();
        assertThat(recurso.podeSerReservadoPor(Perfil.DISCENTE)).isFalse();
    }

    @Test
    void recursoEmManutencaoNaoPodeSerReservado() {
        Recurso bancada = Recurso.cadastrar("Bancada 1", TipoRecurso.BANCADA, "DCC", 2);
        bancada.enviarParaManutencao();

        assertThat(bancada.podeSerReservadoPor(Perfil.DOCENTE)).isFalse();
    }
}
