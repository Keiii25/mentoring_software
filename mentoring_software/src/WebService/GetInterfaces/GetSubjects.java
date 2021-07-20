package WebService.GetInterfaces;

import WebService.GET;
import org.json.JSONArray;

import java.io.IOException;

public interface GetSubjects extends GET {
    default JSONArray getSubjects(int option) {
        String extension = "/subject";

        try {
            if (option == 1) {

            }else if (option == 2) {
                extension += "?fields=competencies";
            } else if (option == 3) {
                extension += "?fields=competencies.subject";
            } else if (option == 4) {
                extension += "?fields=bids";
            } else {
                throw new IOException("option entered is not from 1-4.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getJSONArray(extension);
    }
}
