package br.ufla.gcc267.laboratorio.catalogo.domain;

public class TransicaoInvalidaException extends RuntimeException {

    public TransicaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
