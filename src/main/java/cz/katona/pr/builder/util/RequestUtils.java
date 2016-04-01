package cz.katona.pr.builder.util;

import org.springframework.http.HttpHeaders;

import java.util.Base64;

public final class RequestUtils {

    private RequestUtils() {
    }

    public static HttpHeaders getHttpHeadersWithAuth(String username, String password) {
        String usernamePassword = username + ":" + password;
        String base64Credentials = Base64.getEncoder().encodeToString(usernamePassword.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials);
        return headers;
    }
}
