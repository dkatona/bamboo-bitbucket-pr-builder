package cz.katona.pr.builder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cz.katona.pr.builder.HealthCheckController.HealthCheckResult;
import cz.katona.pr.builder.bamboo.BambooException;
import cz.katona.pr.builder.bamboo.BambooService;
import cz.katona.pr.builder.bamboo.model.Branch;
import cz.katona.pr.builder.bitbucket.BitbucketException;
import cz.katona.pr.builder.bitbucket.BitbucketService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HealthCheckControllerTest {

    private static final String BRANCH = "dk-abc";
    private static final String REPO = "bbox/data-platform";
    private static final String PLAN_ID = "DP-234";

    private HealthCheckController healthCheckController;
    private BambooService bambooService;
    private BitbucketService bitbucketService;

    @Before
    public void setUp() throws Exception {
        bambooService = mock(BambooService.class);
        bitbucketService = mock(BitbucketService.class);
        healthCheckController = new HealthCheckController(bambooService, bitbucketService, REPO, PLAN_ID, BRANCH);

    }

    @Test
    public void testHealthCheckOk() throws Exception {
        when(bambooService.getBranch(PLAN_ID, BRANCH)).thenReturn(new Branch(BRANCH, BRANCH, BRANCH, true));
        when(bitbucketService.getMainBranch(REPO)).thenReturn("master");
        ResponseEntity<HealthCheckResult> result = healthCheckController.healthCheck();
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().passed(), is(true));
    }

    @Test
    public void testHealthCheckBitbucketFail() throws Exception {
        when(bambooService.getBranch(PLAN_ID, BRANCH)).thenReturn(new Branch(BRANCH, BRANCH, BRANCH, true));
        when(bitbucketService.getMainBranch(REPO)).thenThrow(new BitbucketException("Error"));

        ResponseEntity<HealthCheckResult> result = healthCheckController.healthCheck();
        assertThat(result.getStatusCode(), is(HttpStatus.SERVICE_UNAVAILABLE));
        assertThat(result.getBody().passed(), is(false));
        assertThat(result.getBody().getBitbucketCheck(), is("Error"));
        assertThat(result.getBody().getBambooCheck(), is(HealthCheckController.OK_RESULT));
    }

    @Test
    public void testHealthCheckBambooFail() throws Exception {
        when(bambooService.getBranch(PLAN_ID, BRANCH)).thenThrow(new BambooException("Error"));
        when(bitbucketService.getMainBranch(REPO)).thenReturn("master");

        ResponseEntity<HealthCheckResult> result = healthCheckController.healthCheck();
        assertThat(result.getStatusCode(), is(HttpStatus.SERVICE_UNAVAILABLE));
        assertThat(result.getBody().passed(), is(false));
        assertThat(result.getBody().getBambooCheck(), is("Error"));
        assertThat(result.getBody().getBitbucketCheck(), is(HealthCheckController.OK_RESULT));
    }

    @Test
    public void testHealthCheckBothFail() throws Exception {
        when(bambooService.getBranch(PLAN_ID, BRANCH)).thenThrow(new BambooException("Error"));
        when(bitbucketService.getMainBranch(REPO)).thenThrow(new BitbucketException("Error2"));

        ResponseEntity<HealthCheckResult> result = healthCheckController.healthCheck();
        assertThat(result.getStatusCode(), is(HttpStatus.SERVICE_UNAVAILABLE));
        assertThat(result.getBody().passed(), is(false));
        assertThat(result.getBody().getBambooCheck(), is("Error"));
        assertThat(result.getBody().getBitbucketCheck(), is("Error2"));

    }
}
