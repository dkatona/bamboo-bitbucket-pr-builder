package cz.katona.pr.builder.filter;

import static org.apache.commons.lang3.Validate.notEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SecuringInterceptor extends HandlerInterceptorAdapter {

    private final String accessToken;

    private static final String ACCESS_TOKEN_PARAM = "accessToken";

    @Autowired
    public SecuringInterceptor(@Value("${app.accessToken}") String accessToken) {
        notEmpty(accessToken, "Access token can't be empty!");
        this.accessToken = accessToken;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestAccessToken = request.getParameter(ACCESS_TOKEN_PARAM);
        if(!accessToken.equals(requestAccessToken)){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }
}
