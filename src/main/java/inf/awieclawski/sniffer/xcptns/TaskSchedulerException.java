package inf.awieclawski.sniffer.xcptns;

public class TaskSchedulerException extends RuntimeException {
    private static final long serialVersionUID = 7315303318396233531L;

    public TaskSchedulerException(String message) {
        super(message);
    }

    public TaskSchedulerException() {
    }

    public TaskSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskSchedulerException(Throwable cause) {
        super(cause);
    }

    public TaskSchedulerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
