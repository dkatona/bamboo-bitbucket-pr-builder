package cz.katona.pr.builder.bamboo;

import static cz.katona.pr.builder.bamboo.BambooResources.*;
import static cz.katona.pr.builder.bamboo.BambooResources.BRANCH_CREATE_RESOURCE;

import cz.katona.pr.builder.bamboo.model.Branch;
import cz.katona.pr.builder.bamboo.model.JobQueued;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BambooService {

    private BambooClient bambooClient;

    @Autowired
    public BambooService(BambooClient bambooClient) {
        this.bambooClient = bambooClient;
    }

    public Branch getBranch(String planId, String branchName) {
        return bambooClient.get(BRANCH_RESOURCE.expand(planId, branchName).toString(), Branch.class);
    }

    public Branch createBranch(String planId, String branchName) {
        return bambooClient.put(BRANCH_CREATE_RESOURCE.expand(planId, branchName, branchName).toString(),
                null, Branch.class);
    }

    public JobQueued queueJob(String planIdWithBranch) {
        return bambooClient.post(QUEUE_BRANCH_JOB.expand(planIdWithBranch).toString(),
                null, JobQueued.class);
    }

}
