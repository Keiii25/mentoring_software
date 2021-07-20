package WebService;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface POST extends WEBSERVICE {
    public default JSONObject post(String requestBody, String extension) throws IOException {
        JSONObject responseObject = new JSONObject();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(WEBSITE + extension))
                .setHeader("Authorization", APIKEY)
                .header("Content-Type","application/json") // This header needs to be set when sending a JSON request body.
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body().length() > 0) {
                responseObject = new JSONObject(response.body());
            }

            if (response.statusCode() == 403) {
                throw new IOException();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return responseObject;
    }
}
