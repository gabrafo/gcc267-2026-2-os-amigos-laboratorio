package br.ufla.gcc267.laboratorio.catalogo.application;

import java.util.UUID;

public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(UUID id) {
        super("Recurso " + id + " não encontrado.");
    }
}
