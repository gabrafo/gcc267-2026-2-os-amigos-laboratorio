package br.ufla.gcc267.laboratorio.reserva.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import br.ufla.gcc267.laboratorio.catalogo.application.CatalogoService;
import br.ufla.gcc267.laboratorio.catalogo.domain.TipoRecurso;
import br.ufla.gcc267.laboratorio.catalogo.infrastructure.RecursoRepositoryEmMemoria;
import br.ufla.gcc267.laboratorio.reserva.application.ReservaService;
import br.ufla.gcc267.laboratorio.reserva.domain.Perfil;
import br.ufla.gcc267.laboratorio.reserva.infrastructure.ConsultaCatalogoLocal;
import br.ufla.gcc267.laboratorio.reserva.infrastructure.ReservaRepositoryEmMemoria;

// Usa o Catalogo de verdade (em memoria) e um relogio fixo: segunda, 19/10/2026, 10:00 no campus.
// O repositorio e compartilhado entre os testes, por isso cada teste cadastra o proprio recurso.
@WebMvcTest(ReservaController.class)
@Import({ReservaService.class, ReservaRepositoryEmMemoria.class, ConsultaCatalogoLocal.class,
        CatalogoService.class, RecursoRepositoryEmMemoria.class, ReservaControllerTest.RelogioFixo.class})
class ReservaControllerTest {

    private static final String URL = "/api/reservas";

    @TestConfiguration
    static class RelogioFixo {
        @Bean
        Clock relogio() {
            return Clock.fixed(Instant.parse("2026-10-19T13:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private CatalogoService catalogo;

    @Autowired
    private ReservaService reservas;

    private UUID recurso(String nome, TipoRecurso tipo) {
        int capacidade = tipo == TipoRecurso.EQUIPAMENTO ? 1 : 10;
        return catalogo.cadastrar(nome, tipo, "DCC", capacidade).getId();
    }

    private MockMvcTester.MockMvcRequestBuilder solicitar(UUID recurso, String perfil, String inicio, String fim) {
        String json = """
                {"recursoId": "%s", "solicitante": "prof@ufla.br", "perfil": "%s",
                 "inicio": "2026-10-20T%s", "fim": "2026-10-20T%s"}
                """.formatted(recurso, perfil, inicio, fim);
        return mvc.post().uri(URL).contentType(MediaType.APPLICATION_JSON).content(json);
    }

    @Test
    void docenteReservaLaboratorio() {
        UUID lab = recurso("Lab Reserva 1", TipoRecurso.LABORATORIO);

        assertThat(solicitar(lab, "DOCENTE", "14:00", "16:00"))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().extractingPath("$.estado").isEqualTo("CONFIRMADA");
        assertThat(solicitar(lab, "DOCENTE", "15:00", "17:00")).hasStatus(HttpStatus.CONFLICT);
    }

    @Test
    void discenteNaoReservaLaboratorio() {
        UUID lab = recurso("Lab Reserva 2", TipoRecurso.LABORATORIO);

        assertThat(solicitar(lab, "DISCENTE", "14:00", "16:00")).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void recursoEmManutencaoRetorna422() {
        UUID bancada = recurso("Bancada Reserva 3", TipoRecurso.BANCADA);
        catalogo.enviarParaManutencao(bancada);

        assertThat(solicitar(bancada, "DOCENTE", "14:00", "16:00")).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void recursoInexistenteRetorna404() {
        assertThat(solicitar(UUID.randomUUID(), "DOCENTE", "14:00", "16:00")).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void periodoInvalidoOuPerfilDesconhecidoRetorna400() {
        UUID lab = recurso("Lab Reserva 4", TipoRecurso.LABORATORIO);

        assertThat(solicitar(lab, "DOCENTE", "21:00", "23:00")).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(solicitar(lab, "DOCENTE", "08:00", "13:00")).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(solicitar(lab, "ALUNO", "14:00", "16:00"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().extractingPath("$.mensagem").asString().contains("perfil");
    }

    @Test
    void consultaExigeExatamenteUmFiltro() {
        assertThat(mvc.get().uri(URL)).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(mvc.get().uri(URL + "?recursoId={id}&solicitante=a", UUID.randomUUID()))
                .hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(mvc.get().uri(URL + "?recursoId={id}", UUID.randomUUID()))
                .hasStatusOk()
                .bodyJson().isLenientlyEqualTo("[]");
    }

    @Test
    void soQuemSolicitouCancela() {
        UUID lab = recurso("Lab Reserva 5", TipoRecurso.LABORATORIO);
        UUID id = reservas.solicitar(lab, "prof@ufla.br", Perfil.DOCENTE,
                LocalDateTime.parse("2026-10-20T14:00"), LocalDateTime.parse("2026-10-20T16:00")).getId();
        String cancelamento = URL + "/" + id + "/cancelamento";

        assertThat(mvc.post().uri(cancelamento).contentType(MediaType.APPLICATION_JSON)
                .content("{\"solicitante\": \"outra@ufla.br\"}")).hasStatus(HttpStatus.FORBIDDEN);
        assertThat(mvc.post().uri(cancelamento).contentType(MediaType.APPLICATION_JSON)
                .content("{\"solicitante\": \" PROF@ufla.br\"}"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.estado").isEqualTo("CANCELADA");
        assertThat(mvc.post().uri(cancelamento).contentType(MediaType.APPLICATION_JSON)
                .content("{\"solicitante\": \"prof@ufla.br\"}")).hasStatus(HttpStatus.CONFLICT);
    }
}
