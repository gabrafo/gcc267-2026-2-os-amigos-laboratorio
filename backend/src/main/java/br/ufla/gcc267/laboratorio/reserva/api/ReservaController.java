package br.ufla.gcc267.laboratorio.reserva.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.ufla.gcc267.laboratorio.reserva.application.ReservaService;
import br.ufla.gcc267.laboratorio.reserva.domain.DadosInvalidosException;
import br.ufla.gcc267.laboratorio.reserva.domain.Reserva;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservas;

    public ReservaController(ReservaService reservas) {
        this.reservas = reservas;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse solicitar(@RequestBody SolicitacaoReservaRequest pedido) {
        return ReservaResponse.de(reservas.solicitar(
                pedido.recursoId(), pedido.solicitante(), pedido.perfil(), pedido.inicio(), pedido.fim()));
    }

    /** Exige exatamente um filtro, para nao expor as reservas de todos os usuarios. */
    @GetMapping
    public List<ReservaResponse> consultar(
            @RequestParam(required = false) UUID recursoId,
            @RequestParam(required = false) String solicitante) {
        if ((recursoId == null) == (solicitante == null)) {
            throw new DadosInvalidosException("Informe exatamente um filtro: recursoId ou solicitante.");
        }
        List<Reserva> encontradas = recursoId != null
                ? reservas.agendaDoRecurso(recursoId)
                : reservas.reservasDoSolicitante(solicitante);
        return encontradas.stream().map(ReservaResponse::de).toList();
    }

    @PostMapping("/{id}/cancelamento")
    public ReservaResponse cancelar(@PathVariable UUID id, @RequestBody CancelamentoRequest pedido) {
        return ReservaResponse.de(reservas.cancelar(id, pedido.solicitante()));
    }
}
