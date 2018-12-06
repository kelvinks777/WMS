package com.bosnet.ngemart.libgen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luis on 2/26/2016.
 * Purpose : access direct to API, don't add specific Query url here.
 */
public class RestClient {
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    private static final String PRINCIPAL_ID = "principalId";

    private GsonMapper jsonMapper = new GsonMapper();
    private String authentication = "";
    private String appId = "";
    private String baseUrl = "";
    private String principalId = "";
    private boolean isNeedAuth = false;
    private RestConfig restConfig;

    public RestClient(RestConfig restConfig) throws Exception {
        this.restConfig = restConfig;
        this.appId = restConfig.GetAppId();
        this.principalId = restConfig.GetPrincipalId();
    }

    public RestClient() {
    }

    public void setAuthorization(String auth, String baseUrl) throws Exception {
        authentication = auth;
        if (restConfig != null) {
            this.baseUrl = restConfig.GetApiHost() + baseUrl + "/";
        } else {
            this.baseUrl = baseUrl;
        }

        isNeedAuth = (!authentication.equals(""));
    }

    public void SetAppId(String injectedAppId){
        this.appId = injectedAppId;
    }

    public <T> T get(Class<T> theClass, String functionName) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnection(Method.GET, baseUrl + functionName, "");
            String textResult = readResultData(urlConnection);

            return jsonMapper.read(textResult, theClass);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    private void setHeaderContentType(HttpURLConnection urlConnection) {
        urlConnection.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON);
    }

    private void setHeaderPrincipalId(HttpURLConnection urlConnection) {
        urlConnection.setRequestProperty(PRINCIPAL_ID, this.principalId);
    }

    private String readResultData(HttpURLConnection urlConnection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                (urlConnection.getInputStream())));

        String output;
        StringBuilder textResult = new StringBuilder();
        while ((output = reader.readLine()) != null) {
            textResult.append(output);
        }
        return textResult.toString();
    }

    private void setTokenHeader(HttpURLConnection urlConnection) {
        urlConnection.setRequestProperty("token", authentication);
    }

    private void setAppIdHeader(HttpURLConnection urlConnection) {
        urlConnection.setRequestProperty("appid", appId);
    }

    public <T> List<T> getList(Class<T[]> theClass, String functionName) throws Exception {
        HttpURLConnection urlConnection = null;
        try {

            urlConnection = getHttpURLConnection(Method.GET, baseUrl + functionName, "");
            String textResult = readResultData(urlConnection);

            if (textResult.equals("[]"))
                return new ArrayList<>();

            return jsonMapper.readList(textResult, theClass);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    private void throwIfErrorCode(HttpURLConnection urlConnection) throws Exception {
        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK && urlConnection.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
            throw new APIFailedException(urlConnection.getResponseMessage());
        }
    }

    public <T> void post(String functionName, T data) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            String inputValue = jsonMapper.write(data);
            urlConnection = getHttpURLConnection(Method.POST, baseUrl + functionName, inputValue);

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public <T> T post(Class<T> theClass, String functionName) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnection(Method.POST, baseUrl + functionName, "");
            String textResult = readResultData(urlConnection);
            urlConnection.disconnect();

            return jsonMapper.read(textResult, theClass);

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public <T> void post(String functionName, List<T> listData) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            String inputValue = jsonMapper.write(listData);
            urlConnection = getHttpURLConnection(Method.POST, baseUrl + functionName, inputValue);
            readResultData(urlConnection);
            urlConnection.disconnect();

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public <T> T post(Class<T> theClass, String functionName, Data data) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            String inputValue = jsonMapper.write(data);
            urlConnection = getHttpURLConnection(Method.POST, baseUrl + functionName, inputValue);
            String textResult = readResultData(urlConnection);

            return jsonMapper.read(textResult, theClass);

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public <T> T post(Class<T> theClass, String functionName, List<T> listData) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            String inputValue = jsonMapper.write(listData);
            urlConnection = getHttpURLConnection(Method.POST, baseUrl + functionName, inputValue);
            String textResult = readResultData(urlConnection);

            return jsonMapper.read(textResult, theClass);

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public <T> List<T> postl(Class<T[]> theClass, String functionName, Data data) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            String inputValue = jsonMapper.write(data);
            urlConnection = getHttpURLConnection(Method.POST, baseUrl + functionName, inputValue);
            String textResult = readResultData(urlConnection);

            if (textResult.equals("[]"))
                return new ArrayList<>();

            return jsonMapper.readList(textResult, theClass);

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public <T> List<T> postList(Class<T[]> theClass, String functionName, Object object) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            String inputValue = jsonMapper.write(object);
            urlConnection = getHttpURLConnection(Method.POST, baseUrl + functionName, inputValue);
            String textResult = readResultData(urlConnection);

            if (textResult.equals("[]"))
                return new ArrayList<>();

            return jsonMapper.readList(textResult, theClass);

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public void post(String functionName) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnection(Method.POST, baseUrl + functionName, "");

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public void delete(String functionName) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnection(Method.DELETE, baseUrl + functionName, "");

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public void put(String functionName) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnection(Method.PUT, baseUrl + functionName, "");

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public <T> void put(String functionName, T data) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            String inputValue = jsonMapper.write(data);
            urlConnection = getHttpURLConnection(Method.PUT, baseUrl + functionName, inputValue);

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    private HttpURLConnection getHttpURLConnection(String method, String urlString, String inputValue) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (!inputValue.equals(""))
            urlConnection.setDoOutput(true);

        urlConnection.setRequestMethod(method);
        setHeaderContentType(urlConnection);

        setHeaderPrincipalId(urlConnection);

        if (isNeedAuth)
            setTokenHeader(urlConnection);

        if (!appId.equals("")) {
            setAppIdHeader(urlConnection);
        }

        if (!inputValue.equals("")) {
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(inputValue.getBytes());
            outputStream.flush();
        }

        throwIfErrorCode(urlConnection);

        return urlConnection;
    }

    class Method {
        static final String POST = "POST";
        static final String PUT = "PUT";
        static final String DELETE = "DELETE";
        static final String GET = "GET";
    }

}
