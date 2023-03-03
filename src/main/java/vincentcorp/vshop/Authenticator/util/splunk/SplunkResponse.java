package vincentcorp.vshop.Authenticator.util.splunk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SplunkResponse 
{
    private String text;
    private int code;
}
