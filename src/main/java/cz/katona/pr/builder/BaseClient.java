package cz.katona.pr.builder;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.katona.pr.builder.bamboo.BambooException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BaseClient {

    private final String baseUri;

    private final Header authenticationHeader;

    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(BaseClient.class);

    public BaseClient(String baseUri, String username, String password, ObjectMapper objectMapper) {
        this.baseUri = baseUri;
        this.objectMapper = objectMapper;
        try {
            this.authenticationHeader = new BasicScheme().authenticate(
                    new UsernamePasswordCredentials(username, password),
                    new HttpGet(), null); //http request here is used just to get charset
        } catch (AuthenticationException e) {
            throw new IllegalStateException("Cannot construct http auth header!");
        }
    }

    public <OUT> OUT get(String resource, Class<OUT> outputType) {
        try {
            Request getRequest = addHeaders(Request.Get(baseUri + resource));
            return executeRequestAndHandleResponse(getRequest, outputType);
        } catch (IOException e) {
            throw new BambooException("Error communicating with the API", e);
        }
    }

    public <OUT> OUT delete(String resource, Class<OUT> outputType) {
        try {
            Request deleteRequest = addHeaders(Request.Delete(baseUri + resource));
            return executeRequestAndHandleResponse(deleteRequest, outputType);
        } catch (IOException e) {
            throw new BambooException("Error communicating with the API", e);
        }
    }

    public <IN,OUT> OUT post(String resource, IN objectToPost, Class<OUT> outputType) {
        try{
            Request postRequest = addHeaders(Request.Post(baseUri + resource));
            postRequest = addBody(postRequest, objectToPost);
            return executeRequestAndHandleResponse(postRequest, outputType);
        } catch (IOException e) {
            throw new BambooException("Error communicating with the API", e);
        }

    }

    public <IN,OUT> OUT put(String resource, IN objectToPut, Class<OUT> outputType) {
        try{
            Request putRequest = addHeaders(Request.Put(baseUri + resource));
            putRequest = addBody(putRequest, objectToPut);
            return executeRequestAndHandleResponse(putRequest, outputType);
        } catch (IOException e) {
            throw new BambooException("Error communicating with the API", e);
        }
    }

    private <IN> Request addBody(Request request, IN bodyObject) throws IOException {
        if (bodyObject != null) {
            String postBody = objectMapper.writeValueAsString(bodyObject);
            return request.bodyString(postBody, ContentType.APPLICATION_JSON);
        }
        return request;
    }

    private <OUT> OUT executeRequestAndHandleResponse(Request request, Class<OUT> outputType) throws IOException {
        Response response = request.execute();
        HttpResponse httpResponse = response.returnResponse();

        int status = httpResponse.getStatusLine().getStatusCode();

        logger.info("Http status code: {}", status);
        HttpEntity httpEntity = httpResponse.getEntity();

        if (status == HttpStatus.SC_OK) {
            String content = EntityUtils.toString(httpEntity);
            return outputType != null ? objectMapper.readValue(content, outputType) : null;
        } else {
            logger.info("Response body {}", httpEntity != null ? EntityUtils.toString(httpEntity) : null);
        }
        return null;
    }

    private Request addHeaders(Request request) {
        return request.addHeader(authenticationHeader).addHeader(
                ACCEPT, APPLICATION_JSON.getMimeType());
    }
}
