package br.ufla.gcc267.laboratorio.reserva.infrastructure;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.ufla.gcc267.laboratorio.catalogo.application.CatalogoService;
import br.ufla.gcc267.laboratorio.catalogo.application.RecursoNaoEncontradoException;
import br.ufla.gcc267.laboratorio.reserva.application.ConsultaCatalogo;
import br.ufla.gcc267.laboratorio.reserva.domain.Perfil;

/**
 * Adaptador que chama o Catalogo no mesmo processo. E o unico ponto da Reserva que conhece o
 * Catalogo: traduz o Perfil de um contexto para o do outro. Quando o Catalogo virar servico,
 * esta classe da lugar a um cliente HTTP, sem mudar o dominio nem a aplicacao.
 */
@Component
public class ConsultaCatalogoLocal implements ConsultaCatalogo {

    private final CatalogoService catalogo;

    public ConsultaCatalogoLocal(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @Override
    public Situacao consultar(UUID recursoId, Perfil perfil) {
        var perfilNoCatalogo = br.ufla.gcc267.laboratorio.catalogo.domain.Perfil.valueOf(perfil.name());
        try {
            return catalogo.podeSerReservadoPor(recursoId, perfilNoCatalogo)
                    ? Situacao.RESERVAVEL
                    : Situacao.NAO_RESERVAVEL;
        } catch (RecursoNaoEncontradoException e) {
            return Situacao.INEXISTENTE;
        }
    }
}
