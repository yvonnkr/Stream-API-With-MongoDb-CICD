package com.yvolabs.streamapi.exception;

/**
 * @author Yvonne N
 */
public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String objectName, String objectId) {
        super("Could not find " + objectName + " with id " + objectId);

    }
}
