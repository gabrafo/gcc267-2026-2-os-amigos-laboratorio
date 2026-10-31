package br.ufla.gcc267.laboratorio.reserva.application;

/** Pedido bem formado que fere uma regra de negocio: recurso nao reservavel ou limite atingido. */
public class SolicitacaoRecusadaException extends RuntimeException {

    public SolicitacaoRecusadaException(String mensagem) {
        super(mensagem);
    }
}
