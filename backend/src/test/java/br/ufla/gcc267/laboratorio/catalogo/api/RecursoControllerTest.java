package br.ufla.gcc267.laboratorio.catalogo.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import br.ufla.gcc267.laboratorio.catalogo.application.CatalogoService;
import br.ufla.gcc267.laboratorio.catalogo.domain.Recurso;
import br.ufla.gcc267.laboratorio.catalogo.domain.TipoRecurso;
import br.ufla.gcc267.laboratorio.catalogo.infrastructure.RecursoRepositoryEmMemoria;

// O repositorio em memoria e compartilhado entre os testes, por isso cada teste usa nomes proprios.
@WebMvcTest(RecursoController.class)
@Import({CatalogoService.class, RecursoRepositoryEmMemoria.class})
class RecursoControllerTest {

    private static final String URL = "/api/catalogo/recursos";

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private CatalogoService catalogo;

    private MockMvcTester.MockMvcRequestBuilder cadastrar(String json) {
        return mvc.post().uri(URL).contentType(MediaType.APPLICATION_JSON).content(json);
    }

    @Test
    void cadastraRecursoValido() {
        String json = """
                {"nome": "Lab Redes", "tipo": "LABORATORIO", "localizacao": "DCC - sala 105", "capacidade": 30}
                """;

        assertThat(cadastrar(json))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().extractingPath("$.estado").isEqualTo("DISPONIVEL");
    }

    @Test
    void rejeitaNomeCurto() {
        String json = """
                {"nome": "La", "tipo": "LABORATORIO", "localizacao": "DCC", "capacidade": 30}
                """;

        assertThat(cadastrar(json)).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void rejeitaTipoDesconhecido() {
        String json = """
                {"nome": "Sala X", "tipo": "SALA", "localizacao": "DCC", "capacidade": 30}
                """;

        assertThat(cadastrar(json))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().extractingPath("$.mensagem").asString().contains("tipo");
    }

    @Test
    void rejeitaNomeDuplicadoComOutraCaixa() {
        catalogo.cadastrar("Lab Software", TipoRecurso.LABORATORIO, "DCC", 30);
        String json = """
                {"nome": "lab software", "tipo": "LABORATORIO", "localizacao": "DCC", "capacidade": 30}
                """;

        assertThat(cadastrar(json)).hasStatus(HttpStatus.CONFLICT);
    }

    @Test
    void rejeitaEquipamentoComCapacidadeCinco() {
        String json = """
                {"nome": "Osciloscopio", "tipo": "EQUIPAMENTO", "localizacao": "DCC", "capacidade": 5}
                """;

        assertThat(cadastrar(json)).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void enviaParaManutencaoELibera() {
        Recurso recurso = catalogo.cadastrar("Lab Hardware", TipoRecurso.LABORATORIO, "DCC", 20);

        assertThat(mvc.post().uri(URL + "/{id}/manutencao", recurso.getId()))
                .hasStatusOk()
                .bodyJson().extractingPath("$.estado").isEqualTo("EM_MANUTENCAO");
        assertThat(mvc.post().uri(URL + "/{id}/liberacao", recurso.getId()))
                .hasStatusOk()
                .bodyJson().extractingPath("$.estado").isEqualTo("DISPONIVEL");
    }

    @Test
    void naoLiberaRecursoDisponivel() {
        Recurso recurso = catalogo.cadastrar("Lab Quimica", TipoRecurso.LABORATORIO, "DQI", 20);

        assertThat(mvc.post().uri(URL + "/{id}/liberacao", recurso.getId())).hasStatus(HttpStatus.CONFLICT);
    }

    @Test
    void recursoInexistenteRetorna404() {
        assertThat(mvc.get().uri(URL + "/{id}", UUID.randomUUID())).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void laboratorioNaoEReservavelPorDiscente() {
        Recurso recurso = catalogo.cadastrar("Lab Fisica", TipoRecurso.LABORATORIO, "DFI", 25);
        String url = URL + "/{id}/elegibilidade?perfil={perfil}";

        assertThat(mvc.get().uri(url, recurso.getId(), "DISCENTE"))
                .bodyJson().extractingPath("$.reservavel").isEqualTo(false);
        assertThat(mvc.get().uri(url, recurso.getId(), "DOCENTE"))
                .bodyJson().extractingPath("$.reservavel").isEqualTo(true);
    }

    @Test
    void perfilDesconhecidoRetorna400() {
        Recurso recurso = catalogo.cadastrar("Bancada 7", TipoRecurso.BANCADA, "DCC", 2);

        assertThat(mvc.get().uri(URL + "/{id}/elegibilidade?perfil=ALUNO", recurso.getId()))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().extractingPath("$.mensagem").asString().contains("perfil");
    }
}
