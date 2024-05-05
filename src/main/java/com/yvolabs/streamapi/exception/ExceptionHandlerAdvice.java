package com.yvolabs.streamapi.exception;

import com.yvolabs.streamapi.response.Result;
import com.yvolabs.streamapi.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;

/**
 * @author Yvonne N
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result<Object> handleObjectNotFoundException(ObjectNotFoundException e) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.NOT_FOUND)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Result<Object> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.INVALID_ARGUMENT)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        HashMap<Object, Object> map = new HashMap<>(errors.size());
        errors.forEach(error -> {
            String key = ((FieldError) error).getField();
            String value = error.getDefaultMessage();
            map.put(key, value);
        });
        return Result.builder()
                .flag(false)
                .code(StatusCode.INVALID_ARGUMENT)
                .message("Provided arguments are not valid, see data for details")
                .data(map)
                .build();
    }

    @ExceptionHandler(InvalidObjectIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Result<Object> handleInvalidObjectIdException(InvalidObjectIdException e) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.INVALID_ARGUMENT)
                .message(e.getMessage())
                .build();
    }

    // Fallback Handler
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Result<Object> handleAllOtherUnhandledException(Exception e) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
    }

}
