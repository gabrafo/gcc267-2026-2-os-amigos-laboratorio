package br.ufla.gcc267.laboratorio.catalogo.api;

import br.ufla.gcc267.laboratorio.catalogo.domain.TipoRecurso;

public record CadastroRecursoRequest(String nome, TipoRecurso tipo, String localizacao, int capacidade) {
}
