package com.kirgor.enklib.rest;

import com.kirgor.enklib.rest.exception.RESTException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for RESTful clients, which should be extended in order to support necessary content type.
 *
 * Uses <a href="http://hc.apache.org/httpcomponents-client-4.3.x/">Apache HttpClient</a>.
 */
public abstract class RESTClient {
    private String baseUrl;
    private String contentType;
    private Map<String, String> cookies = new HashMap<String, String>();
    private HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

    /**
     * Creates {@link RESTClient} instance.
     *
     * @param baseUrl     Base URL, which all request paths will be appended to.
     * @param contentType Content-Type value, which will be passed as request header to all requests.
     */
    public RESTClient(String baseUrl, String contentType) {
        this.baseUrl = baseUrl;
        this.contentType = contentType;
    }

    /**
     * Performs GET request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param params      Map of URL query params.
     * @param headers     Map of HTTP request headers.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> get(Class<T> entityClass, String path, Map<String, String> params, Map<String, String> headers) throws IOException, RESTException {
        HttpGet httpGet = buildHttpGet(path, params);
        return parseEntityResponse(entityClass, getHttpResponse(httpGet, headers));
    }

    /**
     * Performs GET request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param params      Map of URL query params.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> get(Class<T> entityClass, String path, Map<String, String> params) throws IOException, RESTException {
        return get(entityClass, path, params, null);
    }

    /**
     * Performs GET request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> get(Class<T> entityClass, String path) throws IOException, RESTException {
        return get(entityClass, path, null, null);
    }

    /**
     * Performs GET request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> get(Class<T> entityClass) throws IOException, RESTException {
        return get(entityClass, "", null, null);
    }

    /**
     * Performs GET request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param params      Map of URL query params.
     * @param headers     Map of HTTP request headers.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> getWithListResult(Class<T> entityClass, String path, Map<String, String> params, Map<String, String> headers) throws IOException, RESTException {
        HttpGet httpGet = buildHttpGet(path, params);
        return parseListEntityResponse(entityClass, getHttpResponse(httpGet, headers));
    }

    /**
     * Performs GET request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param params      Map of URL query params.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> getWithListResult(Class<T> entityClass, String path, Map<String, String> params) throws IOException, RESTException {
        return getWithListResult(entityClass, path, params, null);
    }

    /**
     * Performs GET request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> getWithListResult(Class<T> entityClass, String path) throws IOException, RESTException {
        return getWithListResult(entityClass, path, null, null);
    }

    /**
     * Performs GET request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> getWithListResult(Class<T> entityClass) throws IOException, RESTException {
        return getWithListResult(entityClass, "", null, null);
    }

    /**
     * Performs GET request.
     *
     * @param path    Request path.
     * @param params  Map of URL query params.
     * @param headers Map of HTTP request headers.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response get(String path, Map<String, String> params, Map<String, String> headers) throws IOException, RESTException {
        HttpGet httpGet = buildHttpGet(path, params);
        return parseResponse(getHttpResponse(httpGet, headers));
    }

    /**
     * Performs GET request.
     *
     * @param path   Request path.
     * @param params Map of URL query params.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response get(String path, Map<String, String> params) throws IOException, RESTException {
        return get(path, params, null);
    }

    /**
     * Performs GET request.
     *
     * @param path Request path.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response get(String path) throws IOException, RESTException {
        return get(path, null, null);
    }

    /**
     * Performs GET request.
     *
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response get() throws IOException, RESTException {
        return get("", null, null);
    }

    /**
     * Performs POST request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param payload     Entity, which will be used as request payload.
     * @param headers     Map of HTTP request headers.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> post(Class<T> entityClass, String path, Object payload, Map<String, String> headers) throws IOException, RESTException {
        return postInternal(entityClass, payload, new HttpPost(baseUrl + path), headers);
    }

    /**
     * Performs POST request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param payload     Entity, which will be used as request payload.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> post(Class<T> entityClass, String path, Object payload) throws IOException, RESTException {
        return post(entityClass, path, payload, null);
    }

    /**
     * Performs POST request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> post(Class<T> entityClass, String path) throws IOException, RESTException {
        return post(entityClass, path, null, null);
    }

    /**
     * Performs POST request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> post(Class<T> entityClass) throws IOException, RESTException {
        return post(entityClass, null, null, null);
    }

    /**
     * Performs POST request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param payload     Entity, which will be used as request payload.
     * @param headers     Map of HTTP request headers.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> postWithListResult(Class<T> entityClass, String path, Object payload, Map<String, String> headers) throws IOException, RESTException {
        return postWithListResultInternal(entityClass, payload, new HttpPost(baseUrl + path), headers);
    }

    /**
     * Performs POST request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param payload     Entity, which will be used as request payload.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> postWithListResult(Class<T> entityClass, String path, Object payload) throws IOException, RESTException {
        return postWithListResult(entityClass, path, payload, null);
    }

    /**
     * Performs POST request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> postWithListResult(Class<T> entityClass, String path) throws IOException, RESTException {
        return postWithListResult(entityClass, path, null, null);
    }

    /**
     * Performs POST request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> postWithListResult(Class<T> entityClass) throws IOException, RESTException {
        return postWithListResult(entityClass, null, null, null);
    }

    /**
     * Performs POST request.
     *
     * @param path    Request path.
     * @param payload Entity, which will be used as request payload.
     * @param headers Map of HTTP request headers.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response post(String path, Object payload, Map<String, String> headers) throws IOException, RESTException {
        return postInternal(payload, new HttpPost(baseUrl + path), headers);
    }

    /**
     * Performs POST request.
     *
     * @param path    Request path.
     * @param payload Entity, which will be used as request payload.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response post(String path, Object payload) throws IOException, RESTException {
        return post(path, payload, null);
    }

    /**
     * Performs POST request.
     *
     * @param path Request path.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response post(String path) throws IOException, RESTException {
        return post(path, null, null);
    }

    /**
     * Performs POST request.
     *
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response post() throws IOException, RESTException {
        return post("", null, null);
    }

    /**
     * Performs PUT request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param payload     Entity, which will be used as request payload.
     * @param headers     Map of HTTP request headers.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> put(Class<T> entityClass, String path, Object payload, Map<String, String> headers) throws IOException, RESTException {
        return postInternal(entityClass, payload, new HttpPut(baseUrl + path), headers);
    }

    /**
     * Performs PUT request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param payload     Entity, which will be used as request payload.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> put(Class<T> entityClass, String path, Object payload) throws IOException, RESTException {
        return put(entityClass, path, payload, null);
    }

    /**
     * Performs PUT request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> put(Class<T> entityClass, String path) throws IOException, RESTException {
        return put(entityClass, path, null, null);
    }

    /**
     * Performs PUT request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> put(Class<T> entityClass) throws IOException, RESTException {
        return put(entityClass, null, null, null);
    }

    /**
     * Performs PUT request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param payload     Entity, which will be used as request payload.
     * @param headers     Map of HTTP request headers.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> putWithListResult(Class<T> entityClass, String path, Object payload, Map<String, String> headers) throws IOException, RESTException {
        return postWithListResultInternal(entityClass, payload, new HttpPut(baseUrl + path), headers);
    }

    /**
     * Performs PUT request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param payload     Entity, which will be used as request payload.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> putWithListResult(Class<T> entityClass, String path, Object payload) throws IOException, RESTException {
        return putWithListResult(entityClass, path, payload, null);
    }

    /**
     * Performs PUT request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> putWithListResult(Class<T> entityClass, String path) throws IOException, RESTException {
        return putWithListResult(entityClass, path, null, null);
    }

    /**
     * Performs PUT request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> putWithListResult(Class<T> entityClass) throws IOException, RESTException {
        return putWithListResult(entityClass, null, null, null);
    }

    /**
     * Performs PUT request.
     *
     * @param path    Request path.
     * @param payload Entity, which will be used as request payload.
     * @param headers Map of HTTP request headers.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response put(String path, Object payload, Map<String, String> headers) throws IOException, RESTException {
        return postInternal(payload, new HttpPut(baseUrl + path), headers);
    }

    /**
     * Performs PUT request.
     *
     * @param path    Request path.
     * @param payload Entity, which will be used as request payload.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response put(String path, Object payload) throws IOException, RESTException {
        return put(path, payload, null);
    }

    /**
     * Performs PUT request.
     *
     * @param path Request path.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response put(String path) throws IOException, RESTException {
        return put(path, null, null);
    }

    /**
     * Performs PUT request.
     *
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response put() throws IOException, RESTException {
        return put("", null, null);
    }

    /**
     * Performs DELETE request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param headers     Map of HTTP request headers.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> delete(Class<T> entityClass, String path, Map<String, String> headers) throws IOException, RESTException {
        return parseEntityResponse(entityClass, getHttpResponse(new HttpDelete(baseUrl + path), headers));
    }

    /**
     * Performs DELETE request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> delete(Class<T> entityClass, String path) throws IOException, RESTException {
        return delete(entityClass, path, null);
    }

    /**
     * Performs DELETE request.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<T> delete(Class<T> entityClass) throws IOException, RESTException {
        return delete(entityClass, "", null);
    }

    /**
     * Performs DELETE request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param headers     Map of HTTP request headers.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> deleteWithListResult(Class<T> entityClass, String path, Map<String, String> headers) throws IOException, RESTException {
        return parseListEntityResponse(entityClass, getHttpResponse(new HttpDelete(baseUrl + path), headers));
    }

    /**
     * Performs DELETE request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param path        Request path.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> deleteWithListResult(Class<T> entityClass, String path) throws IOException, RESTException {
        return deleteWithListResult(entityClass, path, null);
    }

    /**
     * Performs DELETE request, while expected response entity is a list of specified type.
     *
     * @param entityClass Class, which contains expected response entity fields.
     * @param <T>         Type of class, which contains expected response entity fields.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public <T> EntityResponse<List<T>> deleteWithListResult(Class<T> entityClass) throws IOException, RESTException {
        return deleteWithListResult(entityClass, "", null);
    }

    /**
     * Performs DELETE request.
     *
     * @param path    Request path.
     * @param headers Map of HTTP request headers.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response delete(String path, Map<String, String> headers) throws IOException, RESTException {
        return parseResponse(getHttpResponse(new HttpDelete(baseUrl + path), headers));
    }

    /**
     * Performs DELETE request.
     *
     * @param path Request path.
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response delete(String path) throws IOException, RESTException {
        return delete(path, null);
    }

    /**
     * Performs DELETE request.
     *
     * @throws IOException   If error during HTTP connection or entity parsing occurs.
     * @throws RESTException If HTTP response code is non OK.
     */
    public Response delete() throws IOException, RESTException {
        return delete("", null);
    }

    /**
     * Gets map of cookies, which are currently stored in the client.
     */
    public Map<String, String> getCookies() {
        return cookies;
    }

    /**
     * Manually add cookie to the client.
     *
     * @param name  Cookie name.
     * @param value Cookie value.
     */
    public void addCookie(String name, String value) {
        cookies.put(name, value);
    }

    /**
     * Removes cookie from the client.
     *
     * @param name Cookie name.
     */
    public void removeCookie(String name) {
        cookies.remove(name);
    }

    /**
     * Gets cookie value by name or null.
     *
     * @param name Cookie name.
     */
    public String getCookieValue(String name) {
        return cookies.get(name);
    }

    /**
     * Parse entity of specified class from {@link HttpResponse} instance.
     *
     * @param entityClass  Entity class.
     * @param httpResponse {@link HttpResponse} instance, which is ready to read from.
     * @param <T>          Entity type.
     * @throws IOException
     */
    protected abstract <T> T parseEntity(Class<T> entityClass, HttpResponse httpResponse) throws IOException;

    /**
     * Parse entity of specified class from {@link HttpResponse} instance,
     * while expected response entity is a list of specified type.
     *
     * @param entityClass  Entity class.
     * @param httpResponse {@link HttpResponse} instance, which is ready to read from.
     * @param <T>          Entity type.
     * @throws IOException
     */
    protected abstract <T> List<T> parseListEntity(Class<T> entityClass, HttpResponse httpResponse) throws IOException;

    /**
     * Converts payload to string according to content type.
     *
     * @param entity Entity to convert.
     */
    protected abstract String payloadToString(Object entity);

    private <T> EntityResponse<T> postInternal(Class<T> entityClass, Object payload, HttpEntityEnclosingRequestBase request, Map<String, String> headers) throws IOException, RESTException {
        if (payload != null) {
            request.setEntity(new StringEntity(payloadToString(payload)));
        }
        return parseEntityResponse(entityClass, getHttpResponse(request, headers));
    }

    private <T> EntityResponse<List<T>> postWithListResultInternal(Class<T> entityClass, Object payload, HttpEntityEnclosingRequestBase request, Map<String, String> headers) throws IOException, RESTException {
        if (payload != null) {
            request.setEntity(new StringEntity(payloadToString(payload)));
        }
        return parseListEntityResponse(entityClass, getHttpResponse(request, headers));
    }

    private Response postInternal(Object payload, HttpEntityEnclosingRequestBase request, Map<String, String> headers) throws IOException, RESTException {
        if (payload != null) {
            request.setEntity(new StringEntity(payloadToString(payload)));
        }
        return parseResponse(getHttpResponse(request, headers));
    }

    private CloseableHttpResponse getHttpResponse(HttpUriRequest httpUriRequest, Map<String, String> headers) throws IOException {
        httpUriRequest.addHeader("Content-Type", contentType);
        if (cookies.size() > 0) {
            StringBuilder cookiesStringBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                cookiesStringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
            }
            cookiesStringBuilder.setLength(cookiesStringBuilder.length() - 1);
            httpUriRequest.addHeader("Cookie", cookiesStringBuilder.toString());
        }

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpUriRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }

        CloseableHttpClient httpClient = httpClientBuilder.build();

        return httpClient.execute(httpUriRequest);
    }

    private int getHttpResponseStatusCode(HttpResponse httpResponse) {
        return httpResponse.getStatusLine().getStatusCode();
    }

    private Map<String, String> getHttpResponseHeaders(HttpResponse httpResponse) {
        Map<String, String> headers = new HashMap<String, String>();
        for (Header header : httpResponse.getAllHeaders()) {
            headers.put(header.getName(), header.getValue());
            if (header.getName().toLowerCase().equals("set-cookie")) {
                int equalsIndex = header.getValue().indexOf('=');
                int semicolonIndex = header.getValue().indexOf(';');
                String cookieName = header.getValue().substring(0, equalsIndex);
                String cookieValue = header.getValue().substring(equalsIndex + 1, semicolonIndex);
                cookies.put(cookieName, cookieValue);
            }
        }
        return headers;
    }

    private Response parseResponse(CloseableHttpResponse httpResponse) throws IOException, RESTException {
        int code = getHttpResponseStatusCode(httpResponse);
        Map<String, String> headers = getHttpResponseHeaders(httpResponse);
        httpResponse.close();
        if (code < 400) {
            return new Response(code, headers);
        } else {
            throw new RESTException(code, headers, null);
        }
    }

    private <T> EntityResponse<T> parseEntityResponse(Class<T> entityClass, CloseableHttpResponse httpResponse) throws IOException, RESTException {
        int code = getHttpResponseStatusCode(httpResponse);
        Map<String, String> headers = getHttpResponseHeaders(httpResponse);
        T entity = parseEntity(entityClass, httpResponse);
        httpResponse.close();
        if (code < 400) {
            return new EntityResponse<T>(code, headers, entity);
        } else {
            throw new RESTException(code, headers, entity);
        }
    }

    private <T> EntityResponse<List<T>> parseListEntityResponse(Class<T> entityClass, CloseableHttpResponse httpResponse) throws IOException, RESTException {
        int code = getHttpResponseStatusCode(httpResponse);
        Map<String, String> headers = getHttpResponseHeaders(httpResponse);
        List<T> entity = parseListEntity(entityClass, httpResponse);
        httpResponse.close();
        if (code < 400) {
            return new EntityResponse<List<T>>(code, headers, entity);
        } else {
            throw new RESTException(code, headers, entity);
        }
    }

    private HttpGet buildHttpGet(String path, Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(baseUrl).append(path);
        if (params != null && params.size() > 0) {
            stringBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
        }

        return new HttpGet(stringBuilder.toString());
    }
}
