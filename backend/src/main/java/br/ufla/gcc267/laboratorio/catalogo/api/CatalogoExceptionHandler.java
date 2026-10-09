package br.ufla.gcc267.laboratorio.catalogo.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import tools.jackson.core.JacksonException;

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse corpoInvalido(HttpMessageNotReadableException erro) {
        // O Jackson guarda em getPath() o caminho ate o campo que nao pode ser lido.
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

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse parametroAusente(MissingServletRequestParameterException erro) {
        return new ErroResponse("O parâmetro \"" + erro.getParameterName() + "\" é obrigatório.");
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
