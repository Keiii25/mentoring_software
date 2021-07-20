package WebService.GetInterfaces;

import WebService.GET;
import org.json.JSONObject;

public interface GetMessage extends GET {
    default JSONObject getMessage(String messageId) {
        String extension = "/message/" + messageId;

        return getJSONObject(extension);
    }
}
