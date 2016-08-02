package cz.katona.pr.builder;

import static org.apache.commons.lang3.Validate.notNull;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.katona.pr.builder.bamboo.BambooService;
import cz.katona.pr.builder.bamboo.model.Branch;
import cz.katona.pr.builder.bitbucket.BitbucketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller with healthcheck checking that evey
 */
@RestController
public class HealthCheckController {

    static final String OK_RESULT = "ok";

    private final BambooService bambooService;
    private final BitbucketService bitbucketService;
    private final String bitbucketCheckRepository;
    private final String bambooCheckPlanId;
    private final String bambooCheckBranch;

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

    @Autowired
    public HealthCheckController(BambooService bambooService,
                                 BitbucketService bitbucketService,
                                 @Value("${bitbucket.check.repository}") String bitbucketCheckRepository,
                                 @Value("${bamboo.check.planId}") String bambooCheckPlanId,
                                 @Value("${bamboo.check.branch}") String bambooCheckBranch) {
        notNull(bambooService, "Bamboo service can't be null!");
        notNull(bitbucketService, "Bitbucket service can't be null!");

        this.bambooService = bambooService;
        this.bitbucketService = bitbucketService;
        this.bitbucketCheckRepository = bitbucketCheckRepository;
        this.bambooCheckPlanId = bambooCheckPlanId;
        this.bambooCheckBranch = bambooCheckBranch;

    }

    /**
     * Healthcheck endpoint - it checks that credentials are set correctly
     * @return result of healthcheck for bamboo and bitbucket with respective status code (200 or 503)
     */
    @RequestMapping(value = "/healthcheck", method = GET)
    public ResponseEntity<HealthCheckResult> healthCheck() {
        logger.info("action=health_check status=START");
        String bitbucketResult = OK_RESULT;
        try {
            logger.debug("action=bitbucket_check status=START");
            String mainBranch = bitbucketService.getMainBranch(bitbucketCheckRepository);
            logger.debug("action=bitbucket_check mainBranch={} status=FINISH", mainBranch);

        } catch (Exception ex) {
            logger.debug("action=bitbucket_check status=ERROR", ex);
            bitbucketResult = ex.getMessage();
        }

        String bambooResult = OK_RESULT;
        try {
            logger.debug("action=bamboo_check status=START");
            Branch branch = bambooService.getBranch(bambooCheckPlanId, bambooCheckBranch);
            logger.debug("action=bamboo_check branch={} status=FINISH", branch);
        } catch (Exception ex) {
            logger.debug("action=bamboo_check status=ERROR", ex);
            bambooResult = ex.getMessage();
        }
        HealthCheckResult result = new HealthCheckResult(bitbucketResult, bambooResult);
        HttpStatus resultStatus = result.passed() ? HttpStatus.OK :
                HttpStatus.SERVICE_UNAVAILABLE;
        logger.info("action=health_check result={} status=FINISH", resultStatus);

        return new ResponseEntity<>(result, resultStatus);

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HealthCheckResult {
        private String bitbucketCheck;
        private String bambooCheck;

        @JsonCreator
        public HealthCheckResult(@JsonProperty("bitbucketCheck") String bitbucketCheck,
                                 @JsonProperty("bambooCheck") String bambooCheck) {
            this.bitbucketCheck = bitbucketCheck;
            this.bambooCheck = bambooCheck;
        }

        public String getBitbucketCheck() {
            return bitbucketCheck;
        }

        public String getBambooCheck() {
            return bambooCheck;
        }

        public boolean passed() {
            return OK_RESULT.equals(bitbucketCheck) && OK_RESULT.equals(bambooCheck);
        }
    }
}
