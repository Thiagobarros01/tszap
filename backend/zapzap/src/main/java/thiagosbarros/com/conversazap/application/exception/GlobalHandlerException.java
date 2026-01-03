package thiagosbarros.com.conversazap.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import thiagosbarros.com.conversazap.interfaces.dto.ErrorResposta;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResposta businessExceptionHandler(BusinessException ex) {
        return new ErrorResposta(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage());
    }

}
