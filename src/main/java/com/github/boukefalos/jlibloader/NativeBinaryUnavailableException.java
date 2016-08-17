package com.github.boukefalos.jlibloader;

/**
 * Thrown when a given integration is not available for the current machine.
 */
public class NativeBinaryUnavailableException extends NativeException {
    private static final long serialVersionUID = 1L;

    public NativeBinaryUnavailableException(String message) {
        super(message);
    }
}
