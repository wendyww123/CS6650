package com.album;

public class AlbumException extends RuntimeException{

    public AlbumException() {
    }

    public AlbumException(String message) {
        super(message);
    }

    public AlbumException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlbumException(Throwable cause) {
        super(cause);
    }

    public AlbumException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
