package cz.katona.pr.builder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.PropertySourceUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CommentPlanLookup {

    private Map<String, String> planLookup = new HashMap<>();

    private static final String COMMENT_PREFIX = "comment.";
    private static final String PLAN_SUFFIX = ".planId";

    @Autowired
    public CommentPlanLookup(@Value("${bitbucket.comment.prefix}") String allowedCommentPrefix,
                             ConfigurableEnvironment configurableEnvironment) {
        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
        Map<String, Object> subProperties = PropertySourceUtils.getSubProperties(propertySources, COMMENT_PREFIX);
        this.planLookup = subProperties.entrySet().stream().collect(Collectors.toMap(
                entry -> allowedCommentPrefix + StringUtils.substringBefore(entry.getKey(), PLAN_SUFFIX),
                entry -> entry.getValue().toString()));
    }

    public String getPlanId(String comment) {
        return planLookup.get(comment);
    }
}
