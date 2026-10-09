package br.ufla.gcc267.laboratorio.catalogo.api;

import java.util.UUID;

import br.ufla.gcc267.laboratorio.catalogo.domain.EstadoOperacional;
import br.ufla.gcc267.laboratorio.catalogo.domain.Recurso;
import br.ufla.gcc267.laboratorio.catalogo.domain.TipoRecurso;

public record RecursoResponse(
        UUID id, String nome, TipoRecurso tipo, String localizacao, int capacidade, EstadoOperacional estado) {

    public static RecursoResponse de(Recurso recurso) {
        return new RecursoResponse(
                recurso.getId(),
                recurso.getNome(),
                recurso.getTipo(),
                recurso.getLocalizacao(),
                recurso.getCapacidade(),
                recurso.getEstado());
    }
}
