package com.yvolabs.streamapi.exception;

/**
 * @author Yvonne N
 */
public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(String movieId) {
        super("Could not find movie with Id " + movieId);
    }
}
