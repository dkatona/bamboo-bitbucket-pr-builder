package cz.katona.pr.builder.bamboo.oauth;

import static cz.katona.pr.builder.TestUtil.MAPPER;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import cz.katona.pr.builder.bamboo.BambooException;
import cz.katona.pr.builder.bamboo.model.Branch;
import cz.katona.pr.builder.bamboo.model.JobQueued;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.util.Collections;

public class BambooOAuthServiceTest {

    private BambooOAuthService serviceSpy;

    private static final Branch BRANCH = new Branch("My branch","dk-abc", "DP-ABC35", false);

    @Before
    public void setUp() throws Exception {
        String privateKey = "classpath:private_key.pem";
        BambooOAuthSettings oAuthSettings = new BambooOAuthSettings(
                "apiKey", privateKey, "token", "tokenSecret", new BambooOAuthApi("https://my.bamboo.com", privateKey));
        BambooOAuthService service = new BambooOAuthService("https://my.bamboo.com", oAuthSettings, MAPPER);
        serviceSpy = spy(service);
    }

    @Test
    public void testGetBranch() throws Exception {
        successHttpResponse(MAPPER.writeValueAsString(BRANCH));

        Branch retrievedBranch = serviceSpy.getBranch("FB-DP", "dk-branch");
        assertThat(retrievedBranch, is(BRANCH));

        verifyOAuthRequestUrl("https://my.bamboo.com/plan/FB-DP/branch/dk-branch.json");
    }

    @Test
    public void testGetBranchNotExisting() throws Exception {
        Response response = new Response(HttpStatus.NO_CONTENT.value(), null, Collections.emptyMap(),
                null, null);
        doReturn(response).when(serviceSpy).signAndSend(any(OAuthRequest.class));
        Branch retrievedBranch = serviceSpy.getBranch("FB-DP", "dk-branch");
        assertThat(retrievedBranch, is(nullValue()));
    }

    @Test(expected = BambooException.class)
    public void testGetBranchException() throws Exception {
        failureHttpResponse();
        serviceSpy.getBranch("FB-DP", "dk-branch");
    }

    @Test
    public void testCreateBranch() throws Exception {
        successHttpResponse(MAPPER.writeValueAsString(BRANCH));

        Branch createdBranch = serviceSpy.createBranch("DP-FBB", "dk-branch");
        assertThat(createdBranch, is(BRANCH));
        verifyOAuthRequestUrl("https://my.bamboo.com/plan/DP-FBB/branch/dk-branch.json?vcsBranch=dk-branch");
    }

    @Test(expected = BambooException.class)
    public void testCreateBranchException() throws Exception {
        failureHttpResponse();
        serviceSpy.createBranch("DP-FBB", "dk-branch");
    }

    @Test
    public void testEnableBranch() throws Exception {
        Response response = new Response(HttpStatus.NO_CONTENT.value(), null, Collections.emptyMap(),
                null, null);
        doReturn(response).when(serviceSpy).signAndSend(any(OAuthRequest.class));
        serviceSpy.enableBranch("DP-FBB34");
        verifyOAuthRequestUrl("https://my.bamboo.com/plan/DP-FBB34/enable.json");
    }

    @Test(expected = BambooException.class)
    public void testEnableBranchFailure() throws Exception {
        failureHttpResponse();
        serviceSpy.enableBranch("DP-FBB34");
    }

    @Test
    public void testQueueJob() throws Exception {
        JobQueued jobQueuedResponse = new JobQueued("DP-FBB34", 20, "DP-FBB34-20", "Manual");
        successHttpResponse(MAPPER.writeValueAsString(jobQueuedResponse));
        JobQueued jobQueued = serviceSpy.queueJob("DP-FBB34");
        assertThat(jobQueued, is(jobQueuedResponse));
        verifyOAuthRequestUrl("https://my.bamboo.com/queue/DP-FBB34.json");
    }

    @Test(expected = BambooException.class)
    public void testQueueJobFailure() throws Exception {
        failureHttpResponse();
        serviceSpy.queueJob("DP-FBB34");
    }

    private void verifyOAuthRequestUrl(String expectedUrl) {
        ArgumentCaptor<OAuthRequest> captor = ArgumentCaptor.forClass(OAuthRequest.class);
        verify(serviceSpy).signAndSend(captor.capture());

        OAuthRequest request = captor.getValue();
        assertThat(request.getUrl(), is(expectedUrl));
    }

    private void failureHttpResponse() {
        Response response = new Response(HttpStatus.UNAUTHORIZED.value(), null, Collections.emptyMap(),
                null, null);
        doReturn(response).when(serviceSpy).signAndSend(any(OAuthRequest.class));
    }

    private void successHttpResponse(String body) {
        Response response = new Response(HttpStatus.OK.value(), null, Collections.emptyMap(),
                body, null);
        doReturn(response).when(serviceSpy).signAndSend(any(OAuthRequest.class));
    }
}
