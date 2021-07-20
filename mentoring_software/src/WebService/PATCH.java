package WebService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface PATCH extends WEBSERVICE {
    default void patch(String data, String extension) throws IOException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WEBSITE+extension))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(data))
                .header("Content-Type", "application/json")
                .setHeader("Authorization", APIKEY)
                .header("accept", "application/json").build();
        try {
            HttpResponse<String> response = client.send(request,  HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


