package br.ufla.gcc267.laboratorio.catalogo.api;

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

import br.ufla.gcc267.laboratorio.catalogo.application.CatalogoService;
import br.ufla.gcc267.laboratorio.catalogo.domain.Perfil;

@RestController
@RequestMapping("/api/catalogo/recursos")
public class RecursoController {

    private final CatalogoService catalogo;

    public RecursoController(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecursoResponse cadastrar(@RequestBody CadastroRecursoRequest pedido) {
        return RecursoResponse.de(
                catalogo.cadastrar(pedido.nome(), pedido.tipo(), pedido.localizacao(), pedido.capacidade()));
    }

    @GetMapping
    public List<RecursoResponse> listar() {
        return catalogo.listar().stream().map(RecursoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public RecursoResponse buscar(@PathVariable UUID id) {
        return RecursoResponse.de(catalogo.buscar(id));
    }

    @PostMapping("/{id}/manutencao")
    public RecursoResponse enviarParaManutencao(@PathVariable UUID id) {
        return RecursoResponse.de(catalogo.enviarParaManutencao(id));
    }

    @PostMapping("/{id}/liberacao")
    public RecursoResponse liberar(@PathVariable UUID id) {
        return RecursoResponse.de(catalogo.liberar(id));
    }

    @PostMapping("/{id}/baixa")
    public RecursoResponse darBaixa(@PathVariable UUID id) {
        return RecursoResponse.de(catalogo.darBaixa(id));
    }

    @GetMapping("/{id}/elegibilidade")
    public ElegibilidadeResponse elegibilidade(@PathVariable UUID id, @RequestParam Perfil perfil) {
        return new ElegibilidadeResponse(catalogo.podeSerReservadoPor(id, perfil));
    }
}
