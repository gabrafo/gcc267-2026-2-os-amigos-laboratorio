package br.ufla.gcc267.laboratorio.catalogo.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import br.ufla.gcc267.laboratorio.catalogo.application.NomeDuplicadoException;
import br.ufla.gcc267.laboratorio.catalogo.application.RecursoNaoEncontradoException;
import br.ufla.gcc267.laboratorio.catalogo.domain.DadosInvalidosException;
import br.ufla.gcc267.laboratorio.catalogo.domain.TransicaoInvalidaException;

/** Converte os erros do Catalogo nas respostas HTTP definidas na SPEC 0001. */
@RestControllerAdvice(assignableTypes = RecursoController.class)
public class CatalogoExceptionHandler {

    @ExceptionHandler(DadosInvalidosException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse dadosInvalidos(DadosInvalidosException erro) {
        return new ErroResponse(erro.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse requisicaoInvalida() {
        return new ErroResponse("Requisição inválida: verifique os campos e os valores enviados.");
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResponse naoEncontrado(RecursoNaoEncontradoException erro) {
        return new ErroResponse(erro.getMessage());
    }

    @ExceptionHandler({NomeDuplicadoException.class, TransicaoInvalidaException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResponse conflito(RuntimeException erro) {
        return new ErroResponse(erro.getMessage());
    }
}
