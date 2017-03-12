package doubledotlabs.butteryslack.utils;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

import doubledotlabs.butteryslack.ButterySlack;

public class SlackUtils {

    public static String getProfilePicture(ButterySlack butterySlack, String userId) {
        return getProfilePicture(butterySlack, userId, "512");
    }

    public static String getProfilePicture(final ButterySlack butterySlack, final String userId, final String resolution) {
        Map<String, String> params = new HashMap<>();
        params.put("user", userId);

        JSONObject object = butterySlack.session.postGenericSlackCommand(params, "users.profile.get").getReply().getPlainAnswer();
        JSONObject profile = (JSONObject) object.get("profile");
        return (String) profile.get("image_" + resolution);
    }
}
