package doubledotlabs.butteryslack.utils;

import android.content.Context;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.replies.GenericSlackReply;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.data.EmojiData;

public class SlackUtils {

    public static String getProfilePicture(ButterySlack butterySlack, String userId) {
        return getProfilePicture(butterySlack, userId, "512");
    }

    public static String getProfilePicture(final ButterySlack butterySlack, final String userId, final String resolution) {
        Map<String, String> params = new HashMap<>();
        params.put("user", userId);

        try {
            SlackMessageHandle<GenericSlackReply> handle = butterySlack.session.postGenericSlackCommand(params, "users.profile.get");
            GenericSlackReply reply = handle.getReply();
            JSONObject object = reply.getPlainAnswer();
            JSONObject profile = (JSONObject) object.get("profile");
            return (String) profile.get("image_" + resolution);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static String getChannelTopic(SlackChannel channel) {
        String topic = channel.getTopic();
        if (topic == null || topic.length() < 1)
            topic = channel.getPurpose();
        return topic;
    }

    public static String getEmojiMessage(ButterySlack butterySlack, String content) {
        if (butterySlack.getEmojis() != null) {
            for (EmojiData emoji : butterySlack.getEmojis()) {
                if (emoji.getUnicodeSize() > 0)
                    content = content.replaceAll(emoji.getSlackName(), emoji.getUnicode());
            }
        }

        return content;
    }

    public static String getHtmlMessage(ButterySlack butterySlack, String content) {
        while (true) {
            try {
                int index = content.indexOf("<@");
                if (index < 0)
                    index = content.indexOf("<#");
                if (index < 0)
                    index = content.indexOf("<http://");
                if (index < 0)
                    index = content.indexOf("<https://");
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
                } else if (id.startsWith("http") && endIndex != realEndIndex) {
                    name = content.substring(endIndex + 1, realEndIndex);
                }

                content = content.replace(content.substring(index, realEndIndex + 1), getHtmlLink(id, name));
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
                break;
            }
        }

        return getEmojiMessage(butterySlack, content);
    }

    public static String getHtmlLink(String href, String name) {
        return "<a href=\"" + href + "\">" + name + "</a>";
    }

    public static GlideUrl getUrl(Context context, String url) {
        return new GlideUrl(url, new LazyHeaders.Builder().addHeader("Authorization", "Bearer " + context.getString(R.string.token)).build());
    }
}
