package cz.katona.pr.builder.bamboo;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.katona.pr.builder.BaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BambooClient extends BaseClient {

    @Autowired
    public BambooClient(@Value("${bamboo.base.uri}") String bambooBaseUri,
                        @Value("${bamboo.user}") String username,
                        @Value("${bamboo.password}") String password,
                        ObjectMapper objectMapper) {
        super(bambooBaseUri, username, password, objectMapper);
    }
}
