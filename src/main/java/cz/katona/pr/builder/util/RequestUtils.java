package cz.katona.pr.builder.util;

import static org.apache.commons.lang3.Validate.*;

import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpHeaders;

import java.util.Base64;

/**
 * Utility class for various request-related functions
 */
public final class RequestUtils {

    private RequestUtils() {
    }

    /**
     * Returns prepared HttpHeaders with basic authentication
     *
     * @param username username to add to basic auth, cannot be empty
     * @param password password to add to basic auth, cannot be null (can be empty)
     * @return HttpHeaders with basic auth header included
     */
    public static HttpHeaders getHttpHeadersWithAuth(String username, String password) {
        notEmpty(username, "Username can't be empty!");
        notNull(password, "Password can't be null!");

        String usernamePassword = username + ":" + password;
        String base64Credentials = Base64.getEncoder().encodeToString(usernamePassword.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials);
        return headers;
    }
}
