package br.ufla.gcc267.laboratorio.reserva.domain;

public class CancelamentoNaoAutorizadoException extends RuntimeException {

    public CancelamentoNaoAutorizadoException() {
        super("Só quem solicitou a reserva pode cancelá-la.");
    }
}
