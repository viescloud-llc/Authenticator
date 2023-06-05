package vincentcorp.vshop.Authenticator.util.Http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ExceptionAdvice {

    @Value("${spring.profiles.active}")
    private String env = "?";

    private final String PROD = "prod";
    
    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<HttpExceptionResponse> handleResponseStatus(ResponseStatusException ex) {
        var response = new HttpExceptionResponse(ex);

        if(env.equals(PROD))
            response.mask();
        
        return new ResponseEntity<HttpExceptionResponse>(response, null, response.getStatus().getValue());
    }
}
