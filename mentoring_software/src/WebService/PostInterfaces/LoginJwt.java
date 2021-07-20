package WebService.PostInterfaces;

import WebService.POST;
import org.json.JSONObject;

import java.io.IOException;

public interface LoginJwt extends POST {
    default JSONObject loginJwt(String userName, String password) throws IOException {
        final String EXTENSION = "/user/login?jwt=true";
        String loginString = "{\"userName\": \"" + userName + "\", \"password\": \"" + password + "\" }";

        return post(loginString, EXTENSION);
    }
}
