package cz.katona.pr.builder.bamboo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cz.katona.pr.builder.bamboo.model.Branch;
import cz.katona.pr.builder.bamboo.model.JobQueued;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class BambooBasicAuthServiceTest {

    private RestTemplate restTemplate;
    private BambooBasicAuthService basicAuthService;

    private static final Branch BRANCH = new Branch("My branch","dk-abc", "DP-ABC35", false);

    @Before
    public void setUp() throws Exception {
        restTemplate = mock(RestTemplate.class);
        basicAuthService = new BambooBasicAuthService("https://my.bamboo.com", "dusan", "pass", restTemplate);
    }

    @Test
    public void testGetBranch() throws Exception {
        successHttpResponse(BRANCH);

        Branch retrievedBranch = basicAuthService.getBranch("DP-ABC35", "dk-abc");
        assertThat(retrievedBranch, is(BRANCH));

        verifyRequest("https://my.bamboo.com/plan/DP-ABC35/branch/dk-abc.json", HttpMethod.GET, Branch.class);
    }

    @Test
    public void testGetBranchNotExisting() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        when(restTemplate.exchange(anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(responseEntity);
        Branch retrievedBranch = basicAuthService.getBranch("DP-ABC35", "dk-abc");
        assertThat(retrievedBranch, is(nullValue()));

        verifyRequest("https://my.bamboo.com/plan/DP-ABC35/branch/dk-abc.json", HttpMethod.GET, Branch.class);
    }

    @Test(expected = BambooException.class)
    public void testGetBranchException() throws Exception {
        failureHttpResponse();
        basicAuthService.getBranch("FB-DP", "dk-branch");
    }

    @Test
    public void testCreateBranch() throws Exception {
        successHttpResponse(BRANCH);
        Branch createdBranch = basicAuthService.createBranch("DP-FBB", "dk-branch");
        assertThat(createdBranch, is(BRANCH));

        verifyRequest("https://my.bamboo.com/plan/DP-FBB/branch/dk-branch.json?vcsBranch=dk-branch",
                HttpMethod.PUT, Branch.class);
    }

    @Test(expected = BambooException.class)
    public void testCreateBranchException() throws Exception {
        failureHttpResponse();
        basicAuthService.createBranch("DP-FBB", "dk-branch");
    }

    @Test
    public void testEnableBranch() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        when(restTemplate.exchange(anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(responseEntity);
        basicAuthService.enableBranch("DP-FBB34");
        verifyRequest("https://my.bamboo.com/plan/DP-FBB34/enable.json",
                HttpMethod.POST, Void.class);
    }

    @Test(expected = BambooException.class)
    public void testEnableBranchFailure() throws Exception {
        failureHttpResponse();
        basicAuthService.enableBranch("DP-FBB34");
    }

    @Test
    public void testQueueJob() throws Exception {
        JobQueued jobQueuedResponse = new JobQueued("DP-FBB34", 20, "DP-FBB34-20", "Manual");
        successHttpResponse(jobQueuedResponse);

        JobQueued jobQueued = basicAuthService.queueJob("DP-FBB34");
        assertThat(jobQueued, is(jobQueuedResponse));
        verifyRequest("https://my.bamboo.com/queue/DP-FBB34.json",
                HttpMethod.POST, JobQueued.class);    }

    @Test(expected = BambooException.class)
    public void testQueueJobFailure() throws Exception {
        failureHttpResponse();
        basicAuthService.queueJob("DP-FBB34");
    }

    private void verifyRequest(String expectedUrl, HttpMethod expectedMethod, Class expectedResponseClass) {
        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(expectedUrl), eq(expectedMethod),
                captor.capture(), eq(expectedResponseClass));
        verifyBasicAuth(captor.getValue());
    }


    private void verifyBasicAuth(HttpEntity httpEntity) {
        assertThat(httpEntity.getHeaders().containsKey(HttpHeaders.AUTHORIZATION), is(true));
        assertThat(httpEntity.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0), startsWith("Basic"));
    }

    private void successHttpResponse(Object responseObject) {
        ResponseEntity responseEntity = new ResponseEntity<>(responseObject, HttpStatus.OK);
        when(restTemplate.exchange(anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(responseEntity);
    }

    private void failureHttpResponse() {
        ResponseEntity responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        when(restTemplate.exchange(anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(responseEntity);
    }
}
