package doubledotlabs.butteryslack.utils;

import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.replies.GenericSlackReply;

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

        SlackMessageHandle<GenericSlackReply> handle = butterySlack.session.postGenericSlackCommand(params, "users.profile.get");
        if (handle != null) {
            GenericSlackReply reply = handle.getReply();
            if (reply != null) {
                JSONObject object = reply.getPlainAnswer();
                JSONObject profile = (JSONObject) object.get("profile");
                return (String) profile.get("image_" + resolution);
            }
        }

        return null;
    }
}
