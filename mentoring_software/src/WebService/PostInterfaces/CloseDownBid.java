package WebService.PostInterfaces;

import WebService.POST;

import java.io.IOException;

public interface CloseDownBid extends POST {
    default void closeDownBid(String dateClosedDown, String bidId) {
        String extension = "/bid/" + bidId + "/close-down";
        String requestBody = "{\"dateClosedDown\": \"" + dateClosedDown + "\"}";

        try {
            post(requestBody, extension);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
