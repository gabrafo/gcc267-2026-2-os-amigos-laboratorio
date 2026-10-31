package br.ufla.gcc267.laboratorio.reserva.domain;

/**
 * Perfil de quem solicita a reserva, com o limite de reservas ativas (SPEC 0002).
 * E um modelo proprio da Reserva: o Catalogo tem o seu, e a traducao fica no adaptador.
 */
public enum Perfil {
    DOCENTE(10),
    DISCENTE(3),
    TECNICO(0),
    COORDENACAO(10);

    private final int limiteDeReservasAtivas;

    Perfil(int limiteDeReservasAtivas) {
        this.limiteDeReservasAtivas = limiteDeReservasAtivas;
    }

    public int getLimiteDeReservasAtivas() {
        return limiteDeReservasAtivas;
    }
}
