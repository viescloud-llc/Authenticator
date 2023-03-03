package vincentcorp.vshop.Authenticator.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
}
