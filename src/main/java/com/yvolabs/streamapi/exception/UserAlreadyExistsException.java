package com.yvolabs.streamapi.exception;

/**
 * @author Yvonne N
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        super("User with email " + email + " already exists");
    }
}
