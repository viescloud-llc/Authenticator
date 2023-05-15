package vincentcorp.vshop.Authenticator.util.Http;

import org.springframework.web.server.ResponseStatusException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpExceptionResponse {
    private HttpStatus status;
    private String message;

    public HttpExceptionResponse(ResponseStatusException ex) {
        this. message = ex.getMessage();
        this.status = new HttpStatus(ex.getStatusCode());
    }

}
