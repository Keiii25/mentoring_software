package WebService.PostInterfaces;

import WebService.POST;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;

public interface MakeContract extends POST {
    default JSONObject makeContract(String tutorId, String studentId, String subjectId, JSONObject paymentInfo, JSONObject lessonInfo, JSONObject additionalInfo, String firstSignerId, LocalDateTime dateCreated) {
        final String EXTENSION = "/contract";
        LocalDateTime time = dateCreated;
        LocalDateTime expire = time.plusMonths(additionalInfo.getInt("contractDuration"));

        additionalInfo.put("firstSigner", firstSignerId);

        String contractString = "{\"firstPartyId\":\"" + tutorId + "\"," +
                "\"secondPartyId\": \"" + studentId + "\"," +
                "\"subjectId\":\"" + subjectId + "\"," +
                "\"dateCreated\": \"" + time.toString() + "\"," +
                "\"expiryDate\": \"" + expire.toString() + "\"," +
                "\"paymentInfo\": " + paymentInfo.toString() + "," +
                "\"lessonInfo\": " + lessonInfo.toString() + "," +
                "\"additionalInfo\": " + additionalInfo.toString() + "}";

        JSONObject response = null;

        try {
            response = post(contractString, EXTENSION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
