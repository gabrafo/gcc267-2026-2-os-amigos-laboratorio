package br.ufla.gcc267.laboratorio.reserva.application;

public class ConflitoDeAgendaException extends RuntimeException {

    public ConflitoDeAgendaException() {
        super("O recurso já está reservado em parte desse período.");
    }
}
