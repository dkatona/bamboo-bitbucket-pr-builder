package cz.katona.pr.builder.bitbucket;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.katona.pr.builder.BaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BitbucketClient extends BaseClient {

    @Autowired
    public BitbucketClient(@Value("${bitbucket.base.uri}") String baseUri,
                           @Value("${bitbucket.user}") String username,
                           @Value("${bitbucket.password}") String password,
                           ObjectMapper objectMapper) {
        super(baseUri, username, password, objectMapper);
    }
}
