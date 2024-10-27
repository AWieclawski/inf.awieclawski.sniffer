package inf.awieclawski.sniffer.xcptns;

public class ControllerException extends RuntimeException {
    private static final long serialVersionUID = 8315303318396233370L;

    public ControllerException(String message) {
        super(message);
    }

    public ControllerException() {
    }

    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ControllerException(Throwable cause) {
        super(cause);
    }

    public ControllerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
