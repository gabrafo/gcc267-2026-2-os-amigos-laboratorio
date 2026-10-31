package br.ufla.gcc267.laboratorio.reserva.application;

import java.util.UUID;

import br.ufla.gcc267.laboratorio.reserva.domain.Perfil;

/**
 * Porta da Reserva para o Catalogo (passo 3 da SPEC 0002). Hoje a implementacao chama o Catalogo
 * no mesmo processo; quando ele virar servico, so o adaptador muda.
 */
public interface ConsultaCatalogo {

    enum Situacao {
        INEXISTENTE,
        RESERVAVEL,
        NAO_RESERVAVEL
    }

    Situacao consultar(UUID recursoId, Perfil perfil);
}
