package vincentcorp.vshop.Authenticator.util.Http;

import org.springframework.web.server.ResponseStatusException;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vincentcorp.vshop.Authenticator.util.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpExceptionResponse {
    private String UUID;
    private String time;
    private HttpStatus status;
    private String message;
    private String detailMessageCode;
    private String reason;
    private String localizedMessage;

    private final static String CUT_MESSAGE1 = "\"message\":";
    private final static String CUT_MESSAGE2 = "\\\"";

    public HttpExceptionResponse(ResponseStatusException ex) {
        this.UUID = java.util.UUID.randomUUID().toString();
        this.time = Time.now().toSpring();
        this. message = extractMessage(ex.getMessage());
        this.status = new HttpStatus(ex.getStatusCode());
        this.reason = ex.getReason();
        this.localizedMessage = ex.getLocalizedMessage();
        this.detailMessageCode = ex.getDetailMessageCode();
    }

    public static String extractMessage(String originMessage) {
        var newMessage = originMessage;

        var index = newMessage.indexOf(CUT_MESSAGE1);

        if(index < 0)
            return originMessage;

        newMessage = newMessage.substring(index + CUT_MESSAGE1.length());

        index = newMessage.indexOf(CUT_MESSAGE2);

        if(index < 0)
            return originMessage;

        newMessage = newMessage.substring(index + CUT_MESSAGE2.length());

        index = newMessage.indexOf(CUT_MESSAGE2);
        
        if(index < 0)
            return originMessage;

        newMessage = newMessage.substring(0, index);

        return newMessage;
    }

    public void mask() {
        this.reason = null;
        this.localizedMessage = null;
        this.detailMessageCode = null;
    }
}
