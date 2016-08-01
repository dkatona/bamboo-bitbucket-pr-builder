package cz.katona.pr.builder;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.PropertySourceUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Lookup for binding of comment configurations (repository-comment pairs) and plan ids which should be fired.
 */
@Component
public class CommentPlanLookup {

    private Map<CommentConfig, String> planLookup = new HashMap<>();

    private static final String COMMENT_PREFIX = "comment.";

    static final Pattern COMMENT_CONFIG_PATTERN = Pattern.compile("(.*)\\.(.*)\\.planId");

    /**
     * Creates a new lookup from specified environment
     * @param allowedCommentPrefix prefix which is present on all user comments for this PR builder, e.g. bamboo:, can
     *                             be empty
     * @param configurableEnvironment environment where comments are configured (e.g. from properties file, command line)
     */
    @Autowired
    public CommentPlanLookup(@Value("${bitbucket.comment.prefix}") String allowedCommentPrefix,
                             ConfigurableEnvironment configurableEnvironment) {
        notNull(configurableEnvironment, "Environment can't be null!");

        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
        Map<String, Object> subProperties = PropertySourceUtils.getSubProperties(propertySources, COMMENT_PREFIX);

        this.planLookup = subProperties.entrySet().stream().collect(Collectors.toMap(
                entry -> getCommentConfig(allowedCommentPrefix, entry.getKey()),
                entry -> entry.getValue().toString()));
    }

    /**
     * Retrieves plan for given repository-comment pair
     * @param repositoryName name of the repository
     * @param comment added comment, which includes the standard prefix (e.g. bamboo:)
     * @return
     */
    public String getPlanId(String repositoryName, String comment) {
        notEmpty(repositoryName, "Repository name can't be empty!");
        notEmpty(comment, "Comment can't be empty!");

        return planLookup.get(new CommentConfig(repositoryName, comment));
    }

    private CommentConfig getCommentConfig(String allowedCommentPrefix, String commentConfigString) {
        Matcher matcher = COMMENT_CONFIG_PATTERN.matcher(commentConfigString);
        if (matcher.matches()) {
            return new CommentConfig(matcher.group(1), allowedCommentPrefix != null ?
                    allowedCommentPrefix + matcher.group(2) : matcher.group(2));
        } else {
            throw new IllegalArgumentException("There is a property starting with 'comment', but in improper format, " +
                    "please change it to: 'comment.<repository>.<comment>.planId'");
        }
    }

    private static class CommentConfig {

        private String repositoryName;
        private String comment;

        public CommentConfig(String repositoryName, String comment) {
            this.repositoryName = repositoryName;
            this.comment = comment;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CommentConfig that = (CommentConfig) o;

            if (!repositoryName.equals(that.repositoryName)) return false;
            return comment.equals(that.comment);

        }

        @Override
        public int hashCode() {
            int result = repositoryName.hashCode();
            result = 31 * result + comment.hashCode();
            return result;
        }
    }
}
