package cz.katona.pr.builder.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.util.Base64;
import java.util.List;

public class RequestUtilsTest {

    @Test
    public void httpHeadersWithBasicAuth() throws Exception {
        HttpHeaders httpHeaders = RequestUtils.getHttpHeadersWithAuth("peter", "pan");
        assertAuthHeader(httpHeaders, "peter", "pan");
    }

    @Test(expected = NullPointerException.class)
    public void httpHeadersNoPassword() throws Exception {
        RequestUtils.getHttpHeadersWithAuth("peter", null);
    }

    @Test(expected = NullPointerException.class)
    public void httpHeadersNoUsername() throws Exception {
        RequestUtils.getHttpHeadersWithAuth(null, "pass");
    }

    @Test
    public void httpHeadersEmptyPassword() throws Exception {
        HttpHeaders httpHeaders = RequestUtils.getHttpHeadersWithAuth("peter", "");
        assertAuthHeader(httpHeaders, "peter", "");
    }

    private void assertAuthHeader(HttpHeaders httpHeaders, String expectedUser, String expectedPass) {
        List<String> authHeader = httpHeaders.get(HttpHeaders.AUTHORIZATION);
        assertThat(authHeader, notNullValue());

        String base64header = authHeader.get(0);
        assertThat(base64header, is("Basic " + Base64.getEncoder().encodeToString((
                expectedUser + ":" + expectedPass).getBytes())));
    }
}
