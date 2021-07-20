package WebService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface GET extends WEBSERVICE {
    default JSONArray getJSONArray(String extension){
        JSONArray JSONOutput = new JSONArray();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(WEBSITE + extension))
                .setHeader("Authorization", APIKEY)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONOutput = new JSONArray(response.body().toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return JSONOutput;
    }

    default JSONObject getJSONObject(String extension){
        JSONObject JSONOutput = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(WEBSITE + extension))
                .setHeader("Authorization", APIKEY)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());
            JSONOutput = new JSONObject(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return JSONOutput;
    }
}
