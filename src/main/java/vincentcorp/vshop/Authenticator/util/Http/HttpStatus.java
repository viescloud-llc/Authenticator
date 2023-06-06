package vincentcorp.vshop.Authenticator.util.Http;

import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HttpStatus {
    private int value;
    private boolean informational;
    private boolean successful;
    private boolean redirection;
    private boolean clientError;
    private boolean serverError;
    private boolean error;

    public HttpStatus(HttpStatusCode httpStatusCode) {
        this.value = httpStatusCode.value();
        informational = httpStatusCode.is1xxInformational();
        successful = httpStatusCode.is2xxSuccessful();
        redirection = httpStatusCode.is3xxRedirection();
        clientError = httpStatusCode.is4xxClientError();
        serverError = httpStatusCode.is5xxServerError();
        error = httpStatusCode.isError();
    }
}
