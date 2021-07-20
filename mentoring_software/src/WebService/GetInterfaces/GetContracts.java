package WebService.GetInterfaces;

import WebService.GET;
import org.json.JSONArray;

public interface GetContracts extends GET {
    default JSONArray getContracts(){
        final String EXTENSION = "/contract";

        return getJSONArray(EXTENSION);
    }
}
