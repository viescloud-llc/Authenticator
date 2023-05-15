package vincentcorp.vshop.Authenticator.util.Http;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ExceptionAdvice {
    
    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<HttpExceptionResponse> handleResponseStatus(ResponseStatusException ex) {
        var response = new HttpExceptionResponse(ex);
        return new ResponseEntity<HttpExceptionResponse>(response, null, response.getStatus().getValue());
    }
}
