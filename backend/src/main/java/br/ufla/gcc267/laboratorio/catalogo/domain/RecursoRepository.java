package br.ufla.gcc267.laboratorio.catalogo.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecursoRepository {

    void salvar(Recurso recurso);

    Optional<Recurso> buscarPorId(UUID id);

    List<Recurso> listarTodos();

    boolean existeComNome(String nome);
}
