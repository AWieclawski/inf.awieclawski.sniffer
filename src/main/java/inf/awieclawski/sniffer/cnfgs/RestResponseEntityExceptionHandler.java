package inf.awieclawski.sniffer.cnfgs;


import inf.awieclawski.sniffer.xcptns.ControllerException;
import inf.awieclawski.sniffer.xcptns.TaskSchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${base.error.msg}")
    public String baseErrorMsg;

    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = baseErrorMsg + getCause(ex);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = baseErrorMsg + getCause(ex);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = baseErrorMsg + getCause(ex);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ControllerException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(ControllerException ex, WebRequest request) {
        String bodyOfResponse = baseErrorMsg + getCause(ex);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(TaskSchedulerException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = baseErrorMsg + getCause(ex);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }


    private String getCause(Throwable ex) {
        if (ex != null) {
            return ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        }
        return "Unrecognized issue!";
    }
}
