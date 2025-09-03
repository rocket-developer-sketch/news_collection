package com.ddi.assessment.news.batch.collector.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import java.util.Map;

public class HttpUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String get(String apiUrl, Map<String, String> requestHeaders) {

        HttpURLConnection con = connect(apiUrl);

        try {
            con.setRequestMethod("GET");

            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();

            InputStream stream = (responseCode == HttpURLConnection.HTTP_OK)
                    ? con.getInputStream() : con.getErrorStream();

            return readBody(stream);

        } catch (IOException e) {
            throw new RuntimeException("API Request And Response Failed", e);
        } finally {
            con.disconnect();
        }
    }

    // todo post 는 test 필요
    public static String post(String apiUrl, Map<String, String> headers, Object requestObject) {
        try {
            String requestBody = objectMapper.writeValueAsString(requestObject);
            return post(apiUrl, headers, requestBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert request object to JSON", e);
        }
    }

    private static String post(String apiUrl, Map<String, String> headers, String requestBody) {
        HttpURLConnection con = connect(apiUrl);

        try {
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            for (Map.Entry<String, String> header : headers.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            InputStream stream = (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED)
                    ? con.getInputStream() : con.getErrorStream();

            return readBody(stream);
        } catch (IOException e) {
            throw new RuntimeException("Failed To POST Request", e);
        } finally {
            con.disconnect();
        }
    }


    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException("Failed To Connection: " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(body))) {
            return reader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            throw new RuntimeException("Failed To Read Response", e);
        }
    }

    private HttpUtils() {
        // 인스턴스 생성 방지
    }

}
