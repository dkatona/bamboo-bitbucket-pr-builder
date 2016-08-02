package cz.katona.pr.builder.bitbucket.oauth;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import cz.katona.pr.builder.TestUtil;
import cz.katona.pr.builder.bitbucket.BitbucketException;
import cz.katona.pr.builder.bitbucket.BitbucketResources;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import java.util.Collections;

public class BitbucketOAuthServiceTest {


    private BitbucketOAuthService bitbucketServiceSpy;

    @Before
    public void setUp() throws Exception {
        BitbucketOAuthSettings oAuthSettings = mock(BitbucketOAuthSettings.class);
        when(oAuthSettings.getAccessToken()).thenReturn(new OAuth2AccessToken("abcdefg"));
        BitbucketOAuthService bitbucketService = new BitbucketOAuthService("https://my.bitbucket.com", oAuthSettings, TestUtil.MAPPER);
        bitbucketServiceSpy = spy(bitbucketService);
    }

    @Test
    public void testCreateComment() throws Exception {
        Response response = new Response(HttpStatus.OK.value(), null, Collections.emptyMap(),
                null, null);
        doReturn(response).when(bitbucketServiceSpy).sendRequest(any(OAuthRequest.class));

        bitbucketServiceSpy.createComment("bbox/data-platform", 12L, "New comment", 13L);

        ArgumentCaptor<OAuthRequest> captor = ArgumentCaptor.forClass(OAuthRequest.class);
        verify(bitbucketServiceSpy).sendRequest(captor.capture());

        verifyRequest(captor.getValue());
    }

    @Test(expected = BitbucketException.class)
    public void testCreateCommentFailure() throws Exception {
        Response response = new Response(HttpStatus.FORBIDDEN.value(), null, Collections.emptyMap(),
                null, null);
        doReturn(response).when(bitbucketServiceSpy).sendRequest(any(OAuthRequest.class));
        bitbucketServiceSpy.createComment("bbox/data-platform", 12L, "New comment", 13L);
    }

    @Test
    public void testGetMainBranch() throws Exception {
        Response response = new Response(HttpStatus.OK.value(), null, Collections.emptyMap(),
                "{\"name\":\"master\"}", null);
        doReturn(response).when(bitbucketServiceSpy).sendRequest(any(OAuthRequest.class));
        String mainBranch = bitbucketServiceSpy.getMainBranch("bbox/data-platform");
        assertThat(mainBranch, is("master"));
    }

    @Test
    public void testMainBranchNotSet() throws Exception {
        Response response = new Response(HttpStatus.NOT_FOUND.value(), null, Collections.emptyMap(),
                BitbucketResources.NO_BRANCH_SET_RESPONSE, null);
        doReturn(response).when(bitbucketServiceSpy).sendRequest(any(OAuthRequest.class));

        String mainBranch = bitbucketServiceSpy.getMainBranch("bbox/data-platform");
        assertThat(mainBranch, nullValue());
    }

    @Test(expected = BitbucketException.class)
    public void testMainBranchInvalid() throws Exception {
        Response response = new Response(HttpStatus.NOT_FOUND.value(), null, Collections.emptyMap(),
                "", null);
        doReturn(response).when(bitbucketServiceSpy).sendRequest(any(OAuthRequest.class));

        bitbucketServiceSpy.getMainBranch("bbox/data-platform");
    }

    private void verifyRequest(OAuthRequest oAuthRequest) throws Exception {
        assertThat(oAuthRequest.getUrl(), is("https://my.bitbucket.com/repositories/bbox/data-platform/pullrequests/12/comments"));
        assertThat(oAuthRequest.getHeaders().get(AUTHORIZATION), is("Bearer abcdefg"));
        String body = oAuthRequest.getBodyContents();
        assertThatJson(body).node("content").isStringEqualTo("New comment");
        assertThatJson(body).node("parent_id").isEqualTo(13L);
    }

}
