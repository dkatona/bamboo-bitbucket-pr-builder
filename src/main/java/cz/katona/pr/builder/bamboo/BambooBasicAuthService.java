package cz.katona.pr.builder.bamboo;

import static cz.katona.pr.builder.bamboo.BambooResources.*;
import static cz.katona.pr.builder.bamboo.BambooResources.BRANCH_CREATE_RESOURCE;

import cz.katona.pr.builder.bamboo.model.Branch;
import cz.katona.pr.builder.bamboo.model.JobQueued;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
@ConditionalOnProperty(name = {"user", "password"}, prefix = "bamboo")
public class BambooBasicAuthService implements BambooService {

    private final String bambooRestEndpoint;
    //headers which contain basic auth
    private final HttpHeaders headers;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public BambooBasicAuthService(@Value("${bamboo.rest.uri}") String bambooRestEndpoint,
                                  @Value("${bamboo.basic.user}") String username,
                                  @Value("${bamboo.basic.password}") String password) {
        this.bambooRestEndpoint = bambooRestEndpoint;
        this.headers = getHttpHeadersWithAuth(username, password);
    }

    @Override
    public Branch getBranch(String planId, String branchName) {
        HttpEntity<Branch> request = new HttpEntity<>(headers);

        ResponseEntity<Branch> exchange = restTemplate.exchange(BRANCH_RESOURCE.expand(bambooRestEndpoint, planId, branchName).toString(),
                HttpMethod.GET, request, Branch.class);

        if (exchange.getStatusCode() != HttpStatus.OK) {
            throw new BambooException("Unable to fetch branch '" + branchName + "', full response " + exchange);
        }
        return exchange.getBody();
    }

    @Override
    public Branch createBranch(String planId, String branchName) {
        HttpEntity<Branch> request = new HttpEntity<>(headers);
        ResponseEntity<Branch> exchange = restTemplate.exchange(BRANCH_CREATE_RESOURCE.expand(
                bambooRestEndpoint, planId, branchName, branchName).toString(),
                HttpMethod.PUT, request, Branch.class);

        if (exchange.getStatusCode() != HttpStatus.OK) {
            throw new BambooException("Unable to create branch '" + branchName + "', full response " + exchange);
        }
        return exchange.getBody();
    }

    @Override
    public void enableBranch(String planIdWithBranch) {
        HttpEntity<Branch> request = new HttpEntity<>(headers);
        ResponseEntity<Void> exchange = restTemplate.exchange(BRANCH_CREATE_RESOURCE.expand(
                bambooRestEndpoint, planIdWithBranch).toString(),
                HttpMethod.POST, request, Void.class);

        if (exchange.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new BambooException("Unable to enable plan '" + planIdWithBranch + "', full response " + exchange);
        }
    }

    @Override
    public JobQueued queueJob(String planIdWithBranch) {
        HttpEntity<JobQueued> request = new HttpEntity<>(headers);
        ResponseEntity<JobQueued> exchange = restTemplate.exchange(QUEUE_BRANCH_JOB.expand(
                bambooRestEndpoint, planIdWithBranch).toString(),
                HttpMethod.POST, request, JobQueued.class);

        if (exchange.getStatusCode() != HttpStatus.OK) {
            throw new BambooException("Unable to queue job '" + planIdWithBranch + "', full response " + exchange);
        }
        return exchange.getBody();
    }

    private HttpHeaders getHttpHeadersWithAuth(String username, String password) {
        String usernamePassword = username + ":" + password;
        String base64Credentials = Base64.getEncoder().encodeToString(usernamePassword.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials);
        return headers;
    }

}