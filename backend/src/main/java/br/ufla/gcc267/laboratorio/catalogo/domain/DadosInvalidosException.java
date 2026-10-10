package br.ufla.gcc267.laboratorio.catalogo.domain;

public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
