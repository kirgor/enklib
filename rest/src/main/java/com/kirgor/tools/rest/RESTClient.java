package com.kirgor.tools.rest;

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

@SuppressWarnings("UnusedDeclaration")
public abstract class RESTClient {
    private String baseUrl;
    private String contentType;
    private Map<String, String> cookies = new HashMap<String, String>();
    private HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

    public RESTClient(String baseUrl, String contentType) {
        this.baseUrl = baseUrl;
        this.contentType = contentType;
    }

    public <T> EntityResponse<T> get(Class<T> entityClass, String path, Map<String, String> params, Map<String, String> headers) throws IOException, RESTException {
        HttpGet httpGet = buildHttpGet(path, params);
        return parseEntityResponse(entityClass, getHttpResponse(httpGet, headers));
    }

    public <T> EntityResponse<T> get(Class<T> entityClass, String path, Map<String, String> params) throws IOException, RESTException {
        return get(entityClass, path, params, null);
    }

    public <T> EntityResponse<T> get(Class<T> entityClass, String path) throws IOException, RESTException {
        return get(entityClass, path, null, null);
    }

    public <T> EntityResponse<T> get(Class<T> entityClass) throws IOException, RESTException {
        return get(entityClass, "", null, null);
    }

    public <T> EntityResponse<List<T>> getWithListResult(Class<T> entityClass, String path, Map<String, String> params, Map<String, String> headers) throws IOException, RESTException {
        HttpGet httpGet = buildHttpGet(path, params);
        return parseListEntityResponse(entityClass, getHttpResponse(httpGet, headers));
    }

    public <T> EntityResponse<List<T>> getWithListResult(Class<T> entityClass, String path, Map<String, String> params) throws IOException, RESTException {
        return getWithListResult(entityClass, path, params, null);
    }

    public <T> EntityResponse<List<T>> getWithListResult(Class<T> entityClass, String path) throws IOException, RESTException {
        return getWithListResult(entityClass, path, null, null);
    }

    public <T> EntityResponse<List<T>> getWithListResult(Class<T> entityClass) throws IOException, RESTException {
        return getWithListResult(entityClass, "", null, null);
    }

    public Response get(String path, Map<String, String> params, Map<String, String> headers) throws IOException, RESTException {
        HttpGet httpGet = buildHttpGet(path, params);
        return parseResponse(getHttpResponse(httpGet, headers));
    }

    public Response get(String path, Map<String, String> params) throws IOException, RESTException {
        return get(path, params, null);
    }

    public Response get(String path) throws IOException, RESTException {
        return get(path, null, null);
    }

    public Response get() throws IOException, RESTException {
        return get("", null, null);
    }

    public <T> EntityResponse<T> post(Class<T> entityClass, String path, Object payload, Map<String, String> headers) throws IOException, RESTException {
        return postInternal(entityClass, payload, new HttpPost(baseUrl + path), headers);
    }

    public <T> EntityResponse<T> post(Class<T> entityClass, String path, Object payload) throws IOException, RESTException {
        return post(entityClass, path, payload, null);
    }

    public <T> EntityResponse<T> post(Class<T> entityClass, String path) throws IOException, RESTException {
        return post(entityClass, path, null, null);
    }

    public <T> EntityResponse<T> post(Class<T> entityClass) throws IOException, RESTException {
        return post(entityClass, null, null, null);
    }

    public <T> EntityResponse<List<T>> postWithListResult(Class<T> entityClass, String path, Object payload, Map<String, String> headers) throws IOException, RESTException {
        return postWithListResultInternal(entityClass, payload, new HttpPost(baseUrl + path), headers);
    }

    public <T> EntityResponse<List<T>> postWithListResult(Class<T> entityClass, String path, Object payload) throws IOException, RESTException {
        return postWithListResult(entityClass, path, payload, null);
    }

    public <T> EntityResponse<List<T>> postWithListResult(Class<T> entityClass, String path) throws IOException, RESTException {
        return postWithListResult(entityClass, path, null, null);
    }

    public <T> EntityResponse<List<T>> postWithListResult(Class<T> entityClass) throws IOException, RESTException {
        return postWithListResult(entityClass, null, null, null);
    }

    public Response post(String path, Object payload, Map<String, String> headers) throws IOException, RESTException {
        return postInternal(payload, new HttpPost(baseUrl + path), headers);
    }

    public Response post(String path, Object payload) throws IOException, RESTException {
        return post(path, payload, null);
    }

    public Response post(String path) throws IOException, RESTException {
        return post(path, null, null);
    }

    public Response post() throws IOException, RESTException {
        return post("", null, null);
    }

    public <T> EntityResponse<T> put(Class<T> entityClass, String path, Object payload, Map<String, String> headers) throws IOException, RESTException {
        return postInternal(entityClass, payload, new HttpPut(baseUrl + path), headers);
    }

    public <T> EntityResponse<T> put(Class<T> entityClass, String path, Object payload) throws IOException, RESTException {
        return put(entityClass, path, payload, null);
    }

    public <T> EntityResponse<T> put(Class<T> entityClass, String path) throws IOException, RESTException {
        return put(entityClass, path, null, null);
    }

    public <T> EntityResponse<T> put(Class<T> entityClass) throws IOException, RESTException {
        return put(entityClass, null, null, null);
    }

    public <T> EntityResponse<List<T>> putWithListResult(Class<T> entityClass, String path, Object payload, Map<String, String> headers) throws IOException, RESTException {
        return postWithListResultInternal(entityClass, payload, new HttpPut(baseUrl + path), headers);
    }

    public <T> EntityResponse<List<T>> putWithListResult(Class<T> entityClass, String path, Object payload) throws IOException, RESTException {
        return putWithListResult(entityClass, path, payload, null);
    }

    public <T> EntityResponse<List<T>> putWithListResult(Class<T> entityClass, String path) throws IOException, RESTException {
        return putWithListResult(entityClass, path, null, null);
    }

    public <T> EntityResponse<List<T>> putWithListResult(Class<T> entityClass) throws IOException, RESTException {
        return putWithListResult(entityClass, null, null, null);
    }

    public Response put(String path, Object payload, Map<String, String> headers) throws IOException, RESTException {
        return postInternal(payload, new HttpPut(baseUrl + path), headers);
    }

    public Response put(String path, Object payload) throws IOException, RESTException {
        return put(path, payload, null);
    }

    public Response put(String path) throws IOException, RESTException {
        return put(path, null, null);
    }

    public Response put() throws IOException, RESTException {
        return put("", null, null);
    }

    public <T> EntityResponse<T> delete(Class<T> entityClass, String path, Map<String, String> headers) throws IOException, RESTException {
        return parseEntityResponse(entityClass, getHttpResponse(new HttpDelete(baseUrl + path), headers));
    }

    public <T> EntityResponse<T> delete(Class<T> entityClass, String path) throws IOException, RESTException {
        return delete(entityClass, path, null);
    }

    public <T> EntityResponse<T> delete(Class<T> entityClass) throws IOException, RESTException {
        return delete(entityClass, "", null);
    }

    public <T> EntityResponse<List<T>> deleteWithListResult(Class<T> entityClass, String path, Map<String, String> headers) throws IOException, RESTException {
        return parseListEntityResponse(entityClass, getHttpResponse(new HttpDelete(baseUrl + path), headers));
    }

    public <T> EntityResponse<List<T>> deleteWithListResult(Class<T> entityClass, String path) throws IOException, RESTException {
        return deleteWithListResult(entityClass, path, null);
    }

    public <T> EntityResponse<List<T>> deleteWithListResult(Class<T> entityClass) throws IOException, RESTException {
        return deleteWithListResult(entityClass, "", null);
    }

    public Response delete(String path, Map<String, String> headers) throws IOException, RESTException {
        return parseResponse(getHttpResponse(new HttpDelete(baseUrl + path), headers));
    }

    public Response delete(String path) throws IOException, RESTException {
        return delete(path, null);
    }

    public Response delete() throws IOException, RESTException {
        return delete("", null);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void addCookie(String name, String value) {
        cookies.put(name, value);
    }

    public void removeCookie(String name) {
        cookies.remove(name);
    }

    public String getCookieValue(String name) {
        return cookies.get(name);
    }

    protected abstract <T> T parseEntity(Class<T> entityClass, HttpResponse httpResponse) throws IOException;

    protected abstract <T> List<T> parseListEntity(Class<T> entityClass, HttpResponse httpResponse) throws IOException;

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
