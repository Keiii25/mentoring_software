package WebService.GetInterfaces;

import WebService.GET;
import org.json.JSONArray;

public interface GetMessages extends GET {
    default JSONArray getMessages(){
        final String EXTENSION = "/message";
        return getJSONArray(EXTENSION);
    }
}
