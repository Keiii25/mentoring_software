package WebService.GetInterfaces;

import WebService.GET;
import org.json.JSONArray;

public interface GetBids extends GET {
    default JSONArray getBids(Boolean withMessages) {
        String extension = "/bid";

        if (withMessages) {
            extension += "?fields=messages";
        }

        return getJSONArray(extension);
    }
}
