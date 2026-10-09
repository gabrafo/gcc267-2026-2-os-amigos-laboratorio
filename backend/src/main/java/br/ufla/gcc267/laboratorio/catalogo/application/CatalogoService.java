package br.ufla.gcc267.laboratorio.catalogo.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.ufla.gcc267.laboratorio.catalogo.domain.Perfil;
import br.ufla.gcc267.laboratorio.catalogo.domain.Recurso;
import br.ufla.gcc267.laboratorio.catalogo.domain.RecursoRepository;
import br.ufla.gcc267.laboratorio.catalogo.domain.TipoRecurso;

/**
 * Casos de uso do Catalogo.
 * Os metodos que alteram dados sao synchronized para que dois pedidos simultaneos nao cadastrem o
 * mesmo nome nem mudem o estado do mesmo recurso ao mesmo tempo (repositorio em memoria).
 */
@Service
public class CatalogoService {

    private final RecursoRepository repositorio;

    public CatalogoService(RecursoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public synchronized Recurso cadastrar(String nome, TipoRecurso tipo, String localizacao, int capacidade) {
        Recurso recurso = Recurso.cadastrar(nome, tipo, localizacao, capacidade);
        if (repositorio.existeComNome(recurso.getNome())) {
            throw new NomeDuplicadoException(recurso.getNome());
        }
        repositorio.salvar(recurso);
        return recurso;
    }

    public List<Recurso> listar() {
        return repositorio.listarTodos();
    }

    public Recurso buscar(UUID id) {
        return repositorio.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException(id));
    }

    public synchronized Recurso enviarParaManutencao(UUID id) {
        Recurso recurso = buscar(id);
        recurso.enviarParaManutencao();
        repositorio.salvar(recurso);
        return recurso;
    }

    public synchronized Recurso liberar(UUID id) {
        Recurso recurso = buscar(id);
        recurso.liberar();
        repositorio.salvar(recurso);
        return recurso;
    }

    public synchronized Recurso darBaixa(UUID id) {
        Recurso recurso = buscar(id);
        recurso.darBaixa();
        repositorio.salvar(recurso);
        return recurso;
    }

    public boolean podeSerReservadoPor(UUID id, Perfil perfil) {
        return buscar(id).podeSerReservadoPor(perfil);
    }
}
