package WebService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface VerifyToken extends WEBSERVICE {
    public default Boolean verifyToken(String jwt) throws IOException {
        // default valid is considered as code 403: "The provided JSON Web Token is invalid, or has expired."
        Boolean valid = false;
        String token = "{\"jwt\": \"" + jwt + "\"}";
        int code = 0;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(WEBSITE + "/user/verify-token"))
                .setHeader("Authorization", APIKEY)
                .header("Content-Type","application/json") // This header needs to be set when sending a JSON request body.
                .POST(HttpRequest.BodyPublishers.ofString(token))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            code = response.statusCode();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (code == 200) {
            valid = true;
        } else if (code == 400) {
            throw new IOException("Request body could not be parsed or contains invalid fields.");
        } else if (code == 401) {
            throw new IOException("A valid API key was not provided in the request.");
        }

        return valid;
    }
}
