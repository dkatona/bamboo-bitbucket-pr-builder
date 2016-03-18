package cz.katona.pr.builder.bamboo;

import cz.katona.pr.builder.bamboo.model.Branch;
import cz.katona.pr.builder.bamboo.model.JobQueued;

public interface BambooService {
    Branch getBranch(String planId, String branchName);

    Branch createBranch(String planId, String branchName);

    void enableBranch(String planIdWithBranch);

    JobQueued queueJob(String planIdWithBranch);
}
