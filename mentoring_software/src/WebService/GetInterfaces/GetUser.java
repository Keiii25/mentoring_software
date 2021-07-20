package WebService.GetInterfaces;

import WebService.GET;
import org.json.JSONObject;

import java.io.IOException;

public interface GetUser extends GET {
    default JSONObject getUser(String userId, int option) {
        String extension = "/user/" + userId;

        try {
            if (option == 1) {

            }else if (option == 2) {
                extension += "?fields=competencies";
            } else if (option == 3) {
                extension += "?fields=competencies.subject";
            } else if (option == 4) {
                extension += "?fields=qualifications";
            } else if (option == 5) {
                extension += "?fields=initiatedBids";
            } else {
                throw new IOException("option entered is not from 1-5.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getJSONObject(extension);
    }
}
