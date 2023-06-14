package vincentcorp.vshop.Authenticator.util.Http;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import feign.FeignException;

public class HttpResponseThrowers 
{
    public static Object throwBadRequest(String message)
    {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }    

    public static Object throwUnauthorized(String message)
    {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
    }    

    public static Object throwForbidden(String message)
    {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    } 

    public static Object throwServerError(String message)
    {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    } 

    public static Object throwNotFound(String message)
    {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    public static Object throwFeignException(FeignException ex)
    {
        int status = ex.status();

        if(status <= 0)
            status = 500;

        HttpStatusCode statusCode = HttpStatusCode.valueOf(status);

        throw new ResponseStatusException(statusCode, ex.getMessage());
    }
}
