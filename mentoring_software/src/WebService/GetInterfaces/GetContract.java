package WebService.GetInterfaces;

import WebService.GET;
import org.json.JSONObject;

public interface GetContract extends GET {
    default JSONObject getContract(String contractId){
        String extension = "/contract/" + contractId;

        return getJSONObject(extension);
    }
}
