package br.ufla.gcc267.laboratorio.reserva.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.ufla.gcc267.laboratorio.reserva.application.ConsultaCatalogo.Situacao;
import br.ufla.gcc267.laboratorio.reserva.domain.DadosInvalidosException;
import br.ufla.gcc267.laboratorio.reserva.domain.Perfil;
import br.ufla.gcc267.laboratorio.reserva.domain.Reserva;
import br.ufla.gcc267.laboratorio.reserva.infrastructure.ReservaRepositoryEmMemoria;

class ReservaServiceTest {

    // Segunda, 19/10/2026, 10:00 no campus = 13:00 UTC. O relogio esta em UTC de proposito.
    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-10-19T13:00:00Z"), ZoneOffset.UTC);

    private final Map<UUID, Situacao> catalogo = new HashMap<>();
    private ReservaService servico;
    private UUID bancada;

    @BeforeEach
    void preparar() {
        catalogo.clear();
        bancada = recurso(Situacao.RESERVAVEL);
        servico = new ReservaService(new ReservaRepositoryEmMemoria(),
                (id, perfil) -> catalogo.getOrDefault(id, Situacao.INEXISTENTE), RELOGIO);
    }

    private UUID recurso(Situacao situacao) {
        UUID id = UUID.randomUUID();
        catalogo.put(id, situacao);
        return id;
    }

    private Reserva solicitar(UUID recurso, String quem, Perfil perfil, String inicio, String fim) {
        return servico.solicitar(recurso, quem, perfil,
                LocalDateTime.parse("2026-10-20T" + inicio), LocalDateTime.parse("2026-10-20T" + fim));
    }

    @Test
    void confirmaReservaValida() {
        assertThat(solicitar(bancada, "prof@ufla.br", Perfil.DOCENTE, "14:00", "16:00")).isNotNull();
    }

    @Test
    void recusaPeriodoQueConflita() {
        solicitar(bancada, "prof@ufla.br", Perfil.DOCENTE, "14:00", "16:00");

        assertThatThrownBy(() -> solicitar(bancada, "outro@ufla.br", Perfil.DOCENTE, "15:00", "17:00"))
                .isInstanceOf(ConflitoDeAgendaException.class);
        assertThat(solicitar(bancada, "outro@ufla.br", Perfil.DOCENTE, "16:00", "18:00")).isNotNull();
    }

    @Test
    void reservaCanceladaLiberaOHorario() {
        Reserva primeira = solicitar(bancada, "prof@ufla.br", Perfil.DOCENTE, "14:00", "16:00");
        servico.cancelar(primeira.getId(), "prof@ufla.br");

        assertThat(solicitar(bancada, "outro@ufla.br", Perfil.DOCENTE, "14:00", "16:00")).isNotNull();
    }

    @Test
    void usaOFusoDoCampusEmServidorUtc() {
        // 10:00 no campus: 11:00 e aceito (1 hora exata), 10:30 nao.
        assertThat(solicitar(bancada, "a@ufla.br", Perfil.DOCENTE, "11:00", "12:00").getPeriodo()).isNotNull();
        assertThatThrownBy(() -> servico.solicitar(bancada, "a@ufla.br", Perfil.DOCENTE,
                LocalDateTime.parse("2026-10-19T10:30"), LocalDateTime.parse("2026-10-19T11:30")))
                .isInstanceOf(DadosInvalidosException.class);
    }

    @Test
    void recursoInexistenteOuIndisponivel() {
        assertThatThrownBy(() -> solicitar(UUID.randomUUID(), "a@ufla.br", Perfil.DOCENTE, "14:00", "16:00"))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        UUID emManutencao = recurso(Situacao.NAO_RESERVAVEL);
        assertThatThrownBy(() -> solicitar(emManutencao, "a@ufla.br", Perfil.DOCENTE, "14:00", "16:00"))
                .isInstanceOf(SolicitacaoRecusadaException.class);
    }

    @Test
    void discenteTemLimiteDeTresReservasAtivas() {
        solicitar(recurso(Situacao.RESERVAVEL), "joao@ufla.br", Perfil.DISCENTE, "08:00", "09:00");
        solicitar(recurso(Situacao.RESERVAVEL), "joao@ufla.br", Perfil.DISCENTE, "10:00", "11:00");
        solicitar(recurso(Situacao.RESERVAVEL), "joao@ufla.br", Perfil.DISCENTE, "12:00", "13:00");

        assertThatThrownBy(() -> solicitar(recurso(Situacao.RESERVAVEL), " JOAO@ufla.br", Perfil.DISCENTE,
                "14:00", "15:00"))
                .isInstanceOf(SolicitacaoRecusadaException.class);
    }

    @Test
    void solicitacoesSimultaneasParaOMesmoHorarioConfirmamSoUma() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Callable<Boolean>> tarefas = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            String quem = "pessoa" + i + "@ufla.br";
            tarefas.add(() -> {
                try {
                    solicitar(bancada, quem, Perfil.DOCENTE, "14:00", "16:00");
                    return true;
                } catch (ConflitoDeAgendaException e) {
                    return false;
                }
            });
        }
        long confirmadas = 0;
        for (Future<Boolean> resultado : executor.invokeAll(tarefas)) {
            confirmadas += resultado.get() ? 1 : 0;
        }
        executor.shutdown();

        assertThat(confirmadas).isEqualTo(1);
    }
}
