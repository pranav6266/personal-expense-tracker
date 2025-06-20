package com.pranav.expensetrackerui.utils;

import com.google.gson.Gson;
import com.pranav.expensetrackerui.exceptions.AuthenticationException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientUtil {
    private static final HttpClient client = HttpClient.newBuilder().build();
    private static final Gson gson = new Gson();

    private static final String baseUrl = "https://personal-expense-tracker-backend-production-a9ef.up.railway.app";

//    Method to send POST request with authorization.
    public static HttpResponse<String> sendPostRequest(String path, String jsonBody) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Send the request and return the response
        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    // Method to send GET request with Authorization token
    public static String sendGetRequestWithToken(String path, String token)
            throws IOException, InterruptedException, AuthenticationException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)  // Add the token in the header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // If the response status code is 403, throw the custom AuthenticationException
        if (response.statusCode() == 403) {
            throw new AuthenticationException("Session has expired. Please log in again.");
        }

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Failed to fetch data: " + response.statusCode());
        }
    }

    // Method to send POST request with Authorization token
    public static void sendPostRequestWithToken(String path, String token, String jsonBody)
            throws IOException, InterruptedException, AuthenticationException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)  // Add Bearer token
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))  // Send the JSON body
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // If the response status code is 403, throw the custom AuthenticationException
        if (response.statusCode() == 403) {
            throw new AuthenticationException("Session has expired. Please log in again.");
        }

        if (response.statusCode() == 200) {
            response.body();
        } else {
            throw new IOException("Failed to fetch data: " + response.statusCode());
        }

    }

    // Method to send PUT request with Authorization token
    public static void sendPutRequestWithToken(String path, String token, String jsonBody) throws IOException, InterruptedException, AuthenticationException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)  // Add Bearer token
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))  // Send the JSON body with PUT
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // If the response status code is 403, throw the custom AuthenticationException
        if (response.statusCode() == 403) {
            throw new AuthenticationException("Session has expired. Please log in again.");
        }

        if (response.statusCode() == 200) {
            System.out.println("Expense updated successfully.");
        } else {
            throw new IOException("Failed to update data: " + response.statusCode());
        }
    }

    // Method to send DELETE request with Authorization token
    public static void sendDeleteRequestWithToken(String path, String token) throws IOException, InterruptedException, AuthenticationException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)  // Add Bearer token
                .DELETE()  // DELETE request method
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // If the response status code is 403, throw the custom AuthenticationException
        if (response.statusCode() == 403) {
            throw new AuthenticationException("Session has expired. Please log in again.");
        }

        if (response.statusCode() != 204) {
            throw new IOException("Failed to delete data: " + response.statusCode());
        }
    }

    public static HttpResponse<String> sendGetRequest(String url, String token) {
        // This method will be used for sending GET requests like fetching expenses.
        return null;
    }
}
