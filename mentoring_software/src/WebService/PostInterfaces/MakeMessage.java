package WebService.PostInterfaces;

import WebService.POST;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;

public interface MakeMessage extends POST {
    default JSONObject makeMessage(String bidId, String posterId, String content, JSONObject additionalInfo){
        final String EXTENSION = "/message";
        LocalDateTime time = LocalDateTime.now();
        JSONObject object = new JSONObject();
        String messageJsonString = "{\"bidId\": \"" + bidId + "\"," +
                "\"posterId\": \"" + posterId + "\"," +
                "\"datePosted\": \"" + time.toString() + "\"," +
                "\"content\": \"" + content + "\"," +
                "\"additionalInfo\": "  + additionalInfo.toString() +
                "}";

        JSONObject response = null;

        try {
            response = post(messageJsonString, EXTENSION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
