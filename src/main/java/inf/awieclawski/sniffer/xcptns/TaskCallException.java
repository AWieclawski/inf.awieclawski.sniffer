package inf.awieclawski.sniffer.xcptns;

public class TaskCallException extends RuntimeException {
    private static final long serialVersionUID = -7415303318396233752L;

    public TaskCallException(String message) {
        super(message);
    }

    public TaskCallException() {
    }

    public TaskCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskCallException(Throwable cause) {
        super(cause);
    }

    public TaskCallException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
