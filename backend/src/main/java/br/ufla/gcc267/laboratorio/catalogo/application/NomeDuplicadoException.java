package br.ufla.gcc267.laboratorio.catalogo.application;

public class NomeDuplicadoException extends RuntimeException {

    public NomeDuplicadoException(String nome) {
        super("Já existe um recurso com o nome \"" + nome + "\".");
    }
}
