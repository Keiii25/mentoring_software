package WebService.PostInterfaces;

import WebService.POST;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;

public interface MakeBid extends POST {
    default JSONObject makeBid(String id, String subjectId, int minComp, int hoursPerLesson, int sessionsPerWeek, int rate, String rateType, Boolean isOpen, int contractDuration) {
        final String EXTENSION = "/bid";
        LocalDateTime time = LocalDateTime.now();
        JSONObject additionalInformation = new JSONObject();
        additionalInformation.put("minimumCompetency", minComp);
        additionalInformation.put("hoursPerLesson", hoursPerLesson);
        additionalInformation.put("sessionsPerWeek", sessionsPerWeek);
        additionalInformation.put("rate", rate);
        additionalInformation.put("rateType", rateType);
        additionalInformation.put("havePrivateOffers", false);
        additionalInformation.put("contractDuration", contractDuration);

        String bidString = "";
        if (isOpen) {
            bidString = "{\"type\": \"open\",";
        } else {
            bidString = "{\"type\": \"close\",";
        }

        bidString += "\"initiatorId\": \"" + id + "\"," +
                "\"dateCreated\": \"" + time.toString() + "\"," +
                "\"subjectId\": \"" + subjectId + "\"," +
                "\"additionalInfo\": " + additionalInformation.toString() + "}";

        JSONObject response = null;

        try {
            response = post(bidString, EXTENSION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
