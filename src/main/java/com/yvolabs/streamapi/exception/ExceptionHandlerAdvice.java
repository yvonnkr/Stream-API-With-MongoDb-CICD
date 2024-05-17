package com.yvolabs.streamapi.exception;

import com.yvolabs.streamapi.response.Result;
import com.yvolabs.streamapi.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
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

    // Security Errors

    // AuthenticationEntryPoint: custom-basic-auth
    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result<?> handleAuthenticationException(Exception ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.UNAUTHORIZED)
                .message("username or password is incorrect")
                .data(ex.getMessage())
                .build();
    }

    // AuthenticationEntryPoint: custom-basic-auth
    @ExceptionHandler(AccountStatusException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result<?> handleAccountStatusException(AccountStatusException ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.UNAUTHORIZED)
                .message("User account is abnormal")
                .data(ex.getMessage())
                .build();
    }

    // AuthenticationEntryPoint: custom-bearer-token-auth
    @ExceptionHandler(InvalidBearerTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result<?> handleInvalidBearerTokenException(InvalidBearerTokenException ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.UNAUTHORIZED)
                .message("The access token provided is expired, revoked, malformed or invalid for other reasons.")
                .data(ex.getMessage())
                .build();
    }


    // AccessDeniedHandler: custom-bearer-token
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    Result<?> handleAccessDeniedException(AccessDeniedException ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.FORBIDDEN)
                .message("No permission to access this resource")
                .data(ex.getMessage())
                .build();

    }

    @ExceptionHandler({InsufficientAuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result<?> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.UNAUTHORIZED)
                .message("Login credentials are missing.")
                .data(ex.getMessage())
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
