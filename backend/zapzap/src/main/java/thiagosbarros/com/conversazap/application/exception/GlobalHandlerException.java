package thiagosbarros.com.conversazap.application.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import thiagosbarros.com.conversazap.interfaces.dto.ErrorResposta;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResposta handleBusinessException(BusinessException ex) {
        log.warn("⚠️ Regra de negócio violada: {}", ex.getMessage());
        return new ErrorResposta(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage());
    }

    @ExceptionHandler(EmpresaNaoEncontradaException.class)
    public ErrorResposta handleEmpresaNaoEncontradaException(EmpresaNaoEncontradaException ex) {
        log.warn("Empresa não encontrada: {}", ex.getMessage());
        return new ErrorResposta(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResposta handleGeneralException(Exception ex) {
        log.error("Erro inesperado: {}",  ex.getMessage());
        return new ErrorResposta(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ocorreu um erro interno no servidor, entre em contato com o suporte.");
    }
}

