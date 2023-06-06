package vincentcorp.vshop.Authenticator.util.Http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @Value("${spring.profiles.active}")
    private String env = "?";

    private final String PROD = "prod";
    
    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<HttpExceptionResponse> handleResponseStatus(ResponseStatusException ex) {
        var response = new HttpExceptionResponse(ex);

        log.error(ex.getMessage(), ex);

        if(env.equals(PROD))
            response.mask();
        
        return new ResponseEntity<HttpExceptionResponse>(response, null, response.getStatus().getValue());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<HttpExceptionResponse> handleException(Exception ex) {
        var response = new HttpExceptionResponse();
        response.setMessage(HttpExceptionResponse.extractMessage(ex.getMessage()));
        response.setLocalizedMessage(ex.getLocalizedMessage());
        response.setStatus(HttpStatus.builder().value(500).serverError(true).error(true).build());

        log.error(ex.getMessage(), ex);

        if(env.equals(PROD))
            response.mask();
        
        return new ResponseEntity<HttpExceptionResponse>(response, null, response.getStatus().getValue());
    }
}
