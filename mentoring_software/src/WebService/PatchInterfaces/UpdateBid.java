package WebService.PatchInterfaces;

import WebService.PATCH;
import org.json.JSONObject;

import java.io.IOException;

public interface UpdateBid extends PATCH {
    default void updateBid(String bidId, JSONObject additionalInfo) {
        String extension = "/bid/" + bidId;
        String data = "{\"additionalInfo\": "  + additionalInfo.toString() + "}";
        try {
            patch(data, extension);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
