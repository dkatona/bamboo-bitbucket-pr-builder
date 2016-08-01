package cz.katona.pr.builder.util;

import cz.katona.pr.builder.bitbucket.model.CommentCreated;
import org.slf4j.MDC;

/**
 * Provides utility methods with regards to logging
 */
public class LoggingUtils {

    private LoggingUtils() {
    }

    /**
     * Initializes logging context with essential information
     * @param commentCreated created comment with all necessary information
     */
    public static void initializeLogContext(CommentCreated commentCreated) {
        MDC.put("pullId", commentCreated.getPullRequest().getId().toString());
        MDC.put("repo", commentCreated.getRepositoryName());
        MDC.put("branch", commentCreated.getSourceBranch());
    }

    /**
     * Clears logging context
     */
    public static void clearLogContext() {
        MDC.clear();
    }
}
