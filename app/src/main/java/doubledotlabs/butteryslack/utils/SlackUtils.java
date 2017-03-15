package doubledotlabs.butteryslack.utils;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.SlackUser;
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
                if (profile != null)
                    return (String) profile.get("image_" + resolution);
            }
        }

        return null;
    }

    public static String getChannelTopic(SlackChannel channel) {
        String topic = channel.getTopic();
        if (topic == null || topic.length() < 1)
            topic = channel.getPurpose();
        return topic;
    }

    public static String getHtmlFromMessage(ButterySlack butterySlack, String content) {
        while (true) {
            int index = content.indexOf("<@");
            if (index < 0)
                index = content.indexOf("<#");
            if (index < 0) break;

            int endIndex = content.indexOf("|");
            int realEndIndex = content.indexOf(">");
            if (endIndex < 1)
                endIndex = realEndIndex;

            String id = content.substring(index + 1, endIndex);
            String name = id;

            if (id.startsWith("@")) {
                SlackUser user = butterySlack.session.findUserById(id.substring(1, id.length()));
                if (user != null) name = "@" + user.getUserName();
            } else if (id.startsWith("#")) {
                SlackChannel channel = butterySlack.session.findChannelById(id.substring(1, id.length()));
                if (channel != null) name = "#" + channel.getName();
            }

            try {
                content = content.replace(content.substring(index, realEndIndex + 1), "<a href=\"" + id + "\">" + name + "</a>");
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
                break;
            }
        }

        return content;
    }
}
