package WebService.GetInterfaces;

import WebService.GET;
import org.json.JSONObject;

public interface GetBid extends GET {
    default JSONObject getBid(String bidId, Boolean withMessages){
        String extension = "/bid/" + bidId;
        
        if (withMessages) {
            extension += "?fields=messages";
        }

        return getJSONObject(extension);
    }
}
