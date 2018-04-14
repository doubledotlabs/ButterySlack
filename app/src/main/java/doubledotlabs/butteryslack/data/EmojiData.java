package doubledotlabs.butteryslack.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class EmojiData {

    private static final String FILE_PREFIX = "https://raw.githubusercontent.com/iamcal/emoji-data/master/img-google-136/";

    private String name;
    private String file;
    private String unicode;
    private String[] unicodes;

    private EmojiData() {
    }

    public String getName() {
        return name;
    }

    public String getSlackName() {
        return ":" + name + ":";
    }

    public String getUrl() {
        return FILE_PREFIX + file;
    }

    public String getUnicode() {
        for (String unicode : unicodes) {
            try {
                return String.valueOf(Character.toChars(Integer.parseInt(unicode, 16)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return getSlackName();
    }

    public String getUnicode(int i) {
        try {
            return String.valueOf(Character.toChars(Integer.parseInt(unicodes[i], 16)));
        } catch (Exception e) {
            e.printStackTrace();
            return getSlackName();
        }
    }

    public int getUnicodeSize() {
        return unicodes.length;
    }

    public static EmojiData from(JsonObject object) {
        EmojiData emoji = new EmojiData();
        emoji.name = object.get("short_name").getAsString();
        emoji.file = object.get("image").getAsString();
        emoji.unicode = object.get("unified").getAsString();
        if (emoji.unicode != null)
            emoji.unicodes = emoji.unicode.split("-");

        return emoji;
    }

}
