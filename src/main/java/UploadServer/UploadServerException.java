package UploadServer;

public class UploadServerException extends Exception {
    public UploadServerException(String message) {
        super(message);
    }

    public UploadServerException(String message, Throwable cause) {
        super(message, cause);
    }
}