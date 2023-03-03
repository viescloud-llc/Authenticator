package vincentcorp.vshop.Authenticator.util.splunk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SplunkRequest 
{
    private String host;
    private String sourcetype;
    private SplunkEvent event;
}
