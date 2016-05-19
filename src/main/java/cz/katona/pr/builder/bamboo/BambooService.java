package cz.katona.pr.builder.bamboo;

import cz.katona.pr.builder.bamboo.model.Branch;
import cz.katona.pr.builder.bamboo.model.JobQueued;

/**
 * Service that provides various operations on Bamboo API
 *
 */
public interface BambooService {

    /**
     * Retrieves information about branch job
     * @param planId plan id associated with branch job
     * @param branchName name of the branch
     * @return information about branch job, null if the branch doesn't exist
     */
    Branch getBranch(String planId, String branchName);

    /**
     * Creates branch job under specified plan
     * @param planId plan id to create branch job under
     * @param branchName name of the branch job
     * @return information about branch job
     */
    Branch createBranch(String planId, String branchName);

    /**
     * Enable branch job bamboo
     * @param planIdWithBranch identification of the branch job, including plan - e.g. DP-FBB395
     */
    void enableBranch(String planIdWithBranch);

    /**
     * Queue a new job in bamboo
     * @param planIdWithBranch identification of the branch job, including plan - e.g. DP-FBB395
     * @return information about queued job
     */
    JobQueued queueJob(String planIdWithBranch);
}
