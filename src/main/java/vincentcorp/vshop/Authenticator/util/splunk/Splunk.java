package vincentcorp.vshop.Authenticator.util.splunk;

import java.net.InetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;


@Component
public class Splunk 
{
    private static RestTemplate restTemplate;

    @Value("${server.port}")
    private String port = "8080";
    private static String PORT;

    @Value("${splunk.host}")
    private String splunkHost = "localhost";
    private static String SPLUNKHOST;

    @Value("${splunk.port}")
    private String splunkPort = "8088";
    private static String SPLUNKPORT;

    @Value("${splunk.scheme}")
    private String splunkScheme = "http";
    private static String SPLUNKSCHEME;

    @Value("${splunk.token}")
    private String splunkToken;
    private static String SPLUNKTOKEN;
    
    private static String splunkPath = "/services/collector/event";

    @Autowired
    public Splunk(RestTemplate restTemplate)
    {
        Splunk.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() 
    {
        PORT = port;
        SPLUNKHOST = splunkHost;
        SPLUNKPORT = splunkPort;
        SPLUNKSCHEME = splunkScheme;
        SPLUNKTOKEN = splunkToken;
    }

    public static SplunkResponse log(SplunkRequest request)
    {
        try
        {
            String url = String.format("%s://%s:%s%s", SPLUNKSCHEME, SPLUNKHOST, SPLUNKPORT, splunkPath);
        
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(HttpHeaders.AUTHORIZATION, String.format("Splunk %s", SPLUNKTOKEN));
    
            HttpEntity<SplunkRequest> entity = new HttpEntity<>(request, headers);
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .build();

            request.setHost(String.format("%s:%s", InetAddress.getLocalHost().getHostAddress(), PORT));
            request.setSourcetype("RestTemplate");

            RestTemplate restTemplate2 = restTemplate;
            
            return restTemplate2.exchange(builder.toUri(), HttpMethod.POST, entity, SplunkResponse.class).getBody();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public static SplunkResponse logInfo(SplunkEvent event)
    {
        event.setEvent(SplunkEvent.INFO);
        SplunkRequest request = SplunkRequest.builder()
                        .event(event)
                        .build();

        return log(request);
    }

    public static SplunkResponse logError(SplunkEvent event)
    {
        event.setEvent(SplunkEvent.ERROR);
        SplunkRequest request = SplunkRequest.builder()
                        .event(event)
                        .build();

        return log(request);
    }

    public static SplunkResponse logError(Exception ex)
    {
        SplunkEvent event = SplunkEvent.builder()
                        .event(SplunkEvent.ERROR)
                        .exception(ex)
                        .message(ex.getMessage())
                        .build();

        SplunkRequest request = SplunkRequest.builder()
                        .event(event)
                        .build();

        return log(request);
    }
}

