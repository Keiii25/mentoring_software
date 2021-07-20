package WebService;

import org.json.JSONObject;

import java.util.Base64;

public interface DecodeJwt extends WEBSERVICE {
    default JSONObject decodeJwt(String jwt) {
        JSONObject decodedJwt = new JSONObject();

        if (jwt.length() > 0) {
            String res = jwt.substring(1, (jwt.length() - 1));
            String[] userInfo = res.split("\\.");
            Base64.Decoder decoder = Base64.getDecoder();

            String header = new String(decoder.decode(userInfo[0]));
            String payload = new String(decoder.decode(userInfo[1]));

            decodedJwt = new JSONObject(payload);
        }

        return decodedJwt;
    }
}
