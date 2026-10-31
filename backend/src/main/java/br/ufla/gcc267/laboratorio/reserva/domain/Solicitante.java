package br.ufla.gcc267.laboratorio.reserva.domain;

import java.util.Locale;

/**
 * Identificacao de quem solicita (e-mail ou matricula), sem espacos nas pontas e em minusculas,
 * para que " Joao@ufla.br" e "joao@ufla.br" sejam a mesma pessoa (SPEC 0002).
 */
public record Solicitante(String identificacao) {

    private static final int MAX = 100;

    public Solicitante {
        identificacao = identificacao == null ? "" : identificacao.strip().toLowerCase(Locale.ROOT);
        if (identificacao.isEmpty() || identificacao.length() > MAX) {
            throw new DadosInvalidosException("A identificação do solicitante é obrigatória (até 100 caracteres).");
        }
    }
}
