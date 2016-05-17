package cz.katona.pr.builder.filter;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.hamcrest.core.Is.is;

public class SecuringInterceptorTest {

    private static final String TOKEN = "test";
    private static final SecuringInterceptor INTERCEPTOR = new SecuringInterceptor(TOKEN);


    @Test(expected = IllegalArgumentException.class)
    public void emptyAccessToken() throws Exception {
        SecuringInterceptor securingInterceptor = new SecuringInterceptor("");
    }

    @Test
    public void testPreHandleCorrectToken() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "https://sample.com");
        mockRequest.addParameter(SecuringInterceptor.ACCESS_TOKEN_PARAM, TOKEN);
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        boolean shouldContinue = INTERCEPTOR.preHandle(mockRequest, mockResponse, null);

        assertThat(shouldContinue, is(true));
        assertThat(mockResponse.getStatus(), is(not(HttpStatus.UNAUTHORIZED.value())));

    }

    @Test
    public void testPreHandleInvalidToken() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "https://sample.com");
        mockRequest.addParameter(SecuringInterceptor.ACCESS_TOKEN_PARAM, "wrongToken");

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        boolean shouldContinue = INTERCEPTOR.preHandle(mockRequest, mockResponse, null);

        assertThat(shouldContinue, is(false));
        assertThat(mockResponse.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));

    }
}
