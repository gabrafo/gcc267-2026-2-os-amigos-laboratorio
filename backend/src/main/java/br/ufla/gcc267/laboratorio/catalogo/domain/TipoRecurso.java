package br.ufla.gcc267.laboratorio.catalogo.domain;

import java.util.Set;

/** Tipos de recurso e os perfis que podem reservar cada um (SPEC 0001). */
public enum TipoRecurso {
    LABORATORIO(Set.of(Perfil.DOCENTE, Perfil.COORDENACAO)),
    BANCADA(Set.of(Perfil.DOCENTE, Perfil.DISCENTE, Perfil.COORDENACAO)),
    EQUIPAMENTO(Set.of(Perfil.DOCENTE, Perfil.DISCENTE, Perfil.COORDENACAO));

    private final Set<Perfil> perfisElegiveis;

    TipoRecurso(Set<Perfil> perfisElegiveis) {
        this.perfisElegiveis = perfisElegiveis;
    }

    public boolean aceitaPerfil(Perfil perfil) {
        return perfisElegiveis.contains(perfil);
    }
}
