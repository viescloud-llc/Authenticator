package vincentcorp.vshop.Authenticator.util.splunk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SplunkEvent 
{
    public static final String INFO = "info";
    public static final String ERROR = "error";

    @Builder.Default
    private String event = INFO;

    private Exception exception;

    private String message;
}
