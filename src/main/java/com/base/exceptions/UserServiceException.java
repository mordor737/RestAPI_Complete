package com.base.exceptions;

public class UserServiceException extends RuntimeException {
    private static final long serialVersionID = 1348771109171435607L;

    public UserServiceException(String message) {
        super(message);
    }
}
