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

    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result<Object> handleMovieNotFoundException(MovieNotFoundException e) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.NOT_FOUND)
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
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Provided arguments are not valid, see data for details")
                .data(map)
                .build();
    }

}
