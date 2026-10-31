package br.ufla.gcc267.laboratorio.reserva.application;

import java.util.UUID;

public class ReservaNaoEncontradaException extends RuntimeException {

    public ReservaNaoEncontradaException(UUID id) {
        super("Reserva " + id + " não encontrada.");
    }
}
