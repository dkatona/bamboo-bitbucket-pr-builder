package cz.katona.pr.builder.bitbucket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cz.katona.pr.builder.bitbucket.model.CommentAdd;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.hamcrest.core.Is.is;


public class BitbucketBasicAuthServiceTest {

    private RestTemplate restTemplate;
    private BitbucketBasicAuthService basicAuthService;

    @Before
    public void setUp() throws Exception {
        restTemplate = mock(RestTemplate.class);
        basicAuthService = new BitbucketBasicAuthService("https://my.bitbucket.com", "dusan", "pass", restTemplate);
    }

    @Test
    public void testCreateComment() throws Exception {
        when(restTemplate.exchange(anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(new ResponseEntity<Object>(HttpStatus.OK));

        basicAuthService.createComment("bbox/data-platform", 12L, "My comment", 13L);
        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("https://my.bitbucket.com/repositories/bbox/data-platform/pullrequests/12/comments"),
                eq(HttpMethod.POST), captor.capture(), eq(Void.class));
        HttpEntity addedComment = captor.getValue();
        assertThat(addedComment.getHeaders().containsKey(HttpHeaders.AUTHORIZATION), is(true));
        assertThat(addedComment.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0), startsWith("Basic"));

        CommentAdd commentAdd = (CommentAdd) addedComment.getBody();
        assertThat(commentAdd.getContent(), is("My comment"));
        assertThat(commentAdd.getParentId(), is(13L));
    }

    @Test(expected = BitbucketException.class)
    public void testCreateCommentFailure() throws Exception {
        when(restTemplate.exchange(anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED));
        basicAuthService.createComment("bbox/data-platform", 12L, "My comment", 13L);

    }
}
