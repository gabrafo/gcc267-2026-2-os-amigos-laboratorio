package br.ufla.gcc267.laboratorio.reserva.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import tools.jackson.core.JacksonException;

import br.ufla.gcc267.laboratorio.reserva.application.ConflitoDeAgendaException;
import br.ufla.gcc267.laboratorio.reserva.application.RecursoNaoEncontradoException;
import br.ufla.gcc267.laboratorio.reserva.application.ReservaNaoEncontradaException;
import br.ufla.gcc267.laboratorio.reserva.application.SolicitacaoRecusadaException;
import br.ufla.gcc267.laboratorio.reserva.domain.CancelamentoNaoAutorizadoException;
import br.ufla.gcc267.laboratorio.reserva.domain.DadosInvalidosException;
import br.ufla.gcc267.laboratorio.reserva.domain.TransicaoInvalidaException;

/** Converte os erros da Reserva nas respostas HTTP definidas na SPEC 0002. */
@RestControllerAdvice(assignableTypes = ReservaController.class)
public class ReservaExceptionHandler {

    @ExceptionHandler(DadosInvalidosException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse dadosInvalidos(DadosInvalidosException erro) {
        return new ErroResponse(erro.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse corpoInvalido(HttpMessageNotReadableException erro) {
        if (erro.getCause() instanceof JacksonException causa && !causa.getPath().isEmpty()) {
            String campo = causa.getPath().getLast().getPropertyName();
            return new ErroResponse("Valor inválido ou ausente no campo \"" + campo + "\".");
        }
        return new ErroResponse("O corpo da requisição não é um JSON válido.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse parametroInvalido(MethodArgumentTypeMismatchException erro) {
        return new ErroResponse("Valor inválido no parâmetro \"" + erro.getName() + "\".");
    }

    @ExceptionHandler({RecursoNaoEncontradoException.class, ReservaNaoEncontradaException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResponse naoEncontrado(RuntimeException erro) {
        return new ErroResponse(erro.getMessage());
    }

    @ExceptionHandler(CancelamentoNaoAutorizadoException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErroResponse naoAutorizado(CancelamentoNaoAutorizadoException erro) {
        return new ErroResponse(erro.getMessage());
    }

    @ExceptionHandler({ConflitoDeAgendaException.class, TransicaoInvalidaException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResponse conflito(RuntimeException erro) {
        return new ErroResponse(erro.getMessage());
    }

    @ExceptionHandler(SolicitacaoRecusadaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ErroResponse recusada(SolicitacaoRecusadaException erro) {
        return new ErroResponse(erro.getMessage());
    }
}
