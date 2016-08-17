package com.github.boukefalos.jlibloader;

public class NativeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NativeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public NativeException(String message) {
        super(message);
    }
}
