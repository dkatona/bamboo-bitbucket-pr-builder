package cz.katona.pr.builder.bitbucket.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class TestUtil {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.registerModule(new JavaTimeModule());
    }

    private TestUtil() {
    }
}
