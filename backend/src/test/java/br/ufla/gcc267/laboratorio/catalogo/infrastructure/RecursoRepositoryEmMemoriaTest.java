package br.ufla.gcc267.laboratorio.catalogo.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import br.ufla.gcc267.laboratorio.catalogo.domain.Recurso;
import br.ufla.gcc267.laboratorio.catalogo.domain.TipoRecurso;

class RecursoRepositoryEmMemoriaTest {

    private final RecursoRepositoryEmMemoria repositorio = new RecursoRepositoryEmMemoria();

    @Test
    void listaEmOrdemAlfabetica() {
        repositorio.salvar(Recurso.cadastrar("Lab Redes", TipoRecurso.LABORATORIO, "DCC", 30));
        repositorio.salvar(Recurso.cadastrar("bancada 1", TipoRecurso.BANCADA, "DCC", 2));
        repositorio.salvar(Recurso.cadastrar("Kit Arduino", TipoRecurso.EQUIPAMENTO, "DCC", 1));

        assertThat(repositorio.listarTodos())
                .extracting(Recurso::getNome)
                .containsExactly("bancada 1", "Kit Arduino", "Lab Redes");
    }

    @Test
    void nomeComAcentoFicaNaOrdemAlfabetica() {
        repositorio.salvar(Recurso.cadastrar("Zoologia", TipoRecurso.LABORATORIO, "DBI", 30));
        repositorio.salvar(Recurso.cadastrar("Óptica", TipoRecurso.LABORATORIO, "DFI", 30));

        assertThat(repositorio.listarTodos())
                .extracting(Recurso::getNome)
                .containsExactly("Óptica", "Zoologia");
    }

    @Test
    void encontraNomeSemDiferenciarMaiusculas() {
        repositorio.salvar(Recurso.cadastrar("Lab Redes", TipoRecurso.LABORATORIO, "DCC", 30));

        assertThat(repositorio.existeComNome("lab redes")).isTrue();
        assertThat(repositorio.existeComNome(" LAB REDES ")).isTrue();
        assertThat(repositorio.existeComNome("Lab Software")).isFalse();
    }

    @Test
    void buscaPorId() {
        Recurso recurso = Recurso.cadastrar("Lab Redes", TipoRecurso.LABORATORIO, "DCC", 30);
        repositorio.salvar(recurso);

        assertThat(repositorio.buscarPorId(recurso.getId())).contains(recurso);
    }
}
