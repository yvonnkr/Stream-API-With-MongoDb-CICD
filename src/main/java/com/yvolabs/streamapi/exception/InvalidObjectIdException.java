package com.yvolabs.streamapi.exception;

/**
 * @author Yvonne N
 */
public class InvalidObjectIdException extends RuntimeException {

    public InvalidObjectIdException(String objectName, String objectId) {
        super(objectName + " id: " + objectId + " is invalid, should be 24 characters long");
    }
}
