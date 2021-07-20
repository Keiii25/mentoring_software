package WebService.PostInterfaces;

import WebService.POST;

import java.io.IOException;
import java.time.LocalDateTime;

public interface SignContract extends POST {
    default void signContract(String contractId, LocalDateTime dateSigned) {
        String extension = "/contract/" + contractId + "/sign";
        String requestBody = "{\"dateSigned\": \"" + dateSigned.toString() +"\"}";

        try {
            post(requestBody, extension);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
