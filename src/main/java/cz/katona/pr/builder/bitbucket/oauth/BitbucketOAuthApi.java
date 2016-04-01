package cz.katona.pr.builder.bitbucket.oauth;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthConfig;
import org.springframework.stereotype.Component;

@Component
public class BitbucketOAuthApi extends DefaultApi20 {

    private static final String BITBUCKET_OAUTH_ENDPOINT = "https://bitbucket.org/site/oauth2/access_token";
    private static final String BITBUCKET_AUTHORIZE_ENDPOINT = "https://bitbucket.org/site/oauth2/authorize";

    @Override
    public String getAccessTokenEndpoint() {
        return BITBUCKET_OAUTH_ENDPOINT;
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        //for implicit grant
        StringBuilder sb = new StringBuilder(BITBUCKET_AUTHORIZE_ENDPOINT);
        sb.append("?").append("client_id=").append(config.getApiKey()).append("&response_type=token");
        return sb.toString();
    }
}
