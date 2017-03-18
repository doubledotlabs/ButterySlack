package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackFile;

import org.json.simple.JSONObject;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.utils.SlackMovementMethod;
import doubledotlabs.butteryslack.utils.SlackUtils;

public class AttachmentData extends ItemData<AttachmentData.ViewHolder> {

    @Nullable
    private String title, titleLink;
    @Nullable
    private String authorName, authorLink, authorIcon;
    @Nullable
    private String pretext, text, textType;
    @Nullable
    private String imageUrl, thumbUrl;
    @Nullable
    private String footer, footerIcon;

    private AttachmentData(Context context, Identifier identifier) {
        super(context, identifier);
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_attachment, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.v.setOnClickListener(this);

        if (!(holder.title.getMovementMethod() instanceof SlackMovementMethod) && getContext() instanceof AppCompatActivity)
            holder.title.setMovementMethod(new SlackMovementMethod((AppCompatActivity) getContext()));

        if (title != null) {
            holder.title.setVisibility(View.VISIBLE);

            if (titleLink != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    holder.title.setText(Html.fromHtml(SlackUtils.getHtmlLink(titleLink, title), 0));
                else holder.title.setText(Html.fromHtml(SlackUtils.getHtmlLink(titleLink, title)));
            } else holder.title.setText(title);
        } else holder.title.setVisibility(View.GONE);

        if (!(holder.subtitle.getMovementMethod() instanceof SlackMovementMethod) && getContext() instanceof AppCompatActivity)
            holder.subtitle.setMovementMethod(new SlackMovementMethod((AppCompatActivity) getContext()));

        if (text != null) {
            holder.subtitle.setVisibility(View.VISIBLE);

            if (textType != null) {
                //TODO: syntax highlighting
                holder.subtitle.setText(text);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                holder.subtitle.setText(Html.fromHtml(SlackUtils.getHtmlMessage(getButterySlack(), text), 0));
            else
                holder.subtitle.setText(Html.fromHtml(SlackUtils.getHtmlMessage(getButterySlack(), text)));
        } else if (pretext != null) {
            holder.subtitle.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                holder.subtitle.setText(Html.fromHtml(SlackUtils.getHtmlMessage(getButterySlack(), pretext), 0));
            else
                holder.subtitle.setText(Html.fromHtml(SlackUtils.getHtmlMessage(getButterySlack(), pretext)));
        } else holder.subtitle.setVisibility(View.GONE);

        if (authorIcon != null) {
            holder.authorIconContainer.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(authorIcon).thumbnail(0.2f).into(holder.authorIcon);
        } else holder.authorIconContainer.setVisibility(View.GONE);

        if (authorName != null) {
            holder.authorName.setVisibility(View.VISIBLE);

            if (authorLink != null) {
                if (!(holder.authorName.getMovementMethod() instanceof SlackMovementMethod) && getContext() instanceof AppCompatActivity)
                    holder.authorName.setMovementMethod(new SlackMovementMethod((AppCompatActivity) getContext()));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    holder.title.setText(Html.fromHtml(SlackUtils.getHtmlLink(authorLink, authorName), 0));
                else
                    holder.title.setText(Html.fromHtml(SlackUtils.getHtmlLink(authorLink, authorName)));
            } else holder.authorName.setText(authorName);
        } else holder.authorName.setVisibility(View.GONE);

        if (thumbUrl != null) {
            holder.thumbIconContainer.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(thumbUrl).into(holder.thumbIcon);
        } else holder.thumbIconContainer.setVisibility(View.GONE);

        if (imageUrl != null) {
            holder.imageContainer.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(SlackUtils.getUrl(getContext(), imageUrl)).into(holder.image);
        } else holder.imageContainer.setVisibility(View.GONE);

        if (footerIcon != null) {
            holder.footerIconContainer.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(footerIcon).into(holder.footerIcon);
        } else holder.footerIconContainer.setVisibility(View.GONE);

        if (!(holder.title.getMovementMethod() instanceof SlackMovementMethod) && getContext() instanceof AppCompatActivity)
            holder.title.setMovementMethod(new SlackMovementMethod((AppCompatActivity) getContext()));

        if (footer != null) {
            holder.footerName.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                holder.footerName.setText(Html.fromHtml(SlackUtils.getHtmlMessage(getButterySlack(), footer), 0));
            else
                holder.footerName.setText(Html.fromHtml(SlackUtils.getHtmlMessage(getButterySlack(), footer)));
        } else holder.footerName.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (titleLink != null)
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(titleLink)));
        else if (authorLink != null)
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authorLink)));
    }

    public static class ViewHolder extends ItemData.ViewHolder {

        View authorIconContainer, thumbIconContainer, imageContainer, footerIconContainer;
        ImageView authorIcon, thumbIcon, image, footerIcon;
        TextView authorName, footerName;

        public ViewHolder(View v) {
            super(v);
            authorIconContainer = v.findViewById(R.id.authorIconContainer);
            thumbIconContainer = v.findViewById(R.id.thumbIconContainer);
            imageContainer = v.findViewById(R.id.imageContainer);
            footerIconContainer = v.findViewById(R.id.footerIconContainer);
            authorIcon = (ImageView) v.findViewById(R.id.authorIcon);
            thumbIcon = (ImageView) v.findViewById(R.id.thumbIcon);
            image = (ImageView) v.findViewById(R.id.image);
            footerIcon = (ImageView) v.findViewById(R.id.footerIcon);
            authorName = (TextView) v.findViewById(R.id.authorName);
            footerName = (TextView) v.findViewById(R.id.footerName);
        }
    }

    public static AttachmentData from(Context context, SlackAttachment attachment) {
        AttachmentData data = new AttachmentData(context, new Identifier(attachment.getTitle(), attachment.getPretext()));

        data.title = attachment.getTitle();
        data.titleLink = attachment.getTitleLink();
        data.authorName = attachment.getAuthorName();
        data.authorLink = attachment.getAuthorLink();
        data.authorIcon = attachment.getAuthorIcon();
        data.pretext = attachment.getPretext();
        data.text = attachment.getText();
        data.imageUrl = attachment.getImageUrl();
        data.thumbUrl = attachment.getThumbUrl();
        data.footer = attachment.getFooter();
        data.footerIcon = attachment.getFooterIcon();

        return data;
    }

    public static AttachmentData from(Context context, JSONObject object) {
        AttachmentData data = new AttachmentData(context, new Identifier((String) object.get("title"), (String) object.get("pretext")));

        data.title = (String) object.get("title");
        data.titleLink = (String) object.get("title_link");
        data.authorName = (String) object.get("author_name");
        data.authorLink = (String) object.get("author_link");
        data.authorIcon = (String) object.get("author_icon");
        data.pretext = (String) object.get("pretext");
        data.text = (String) object.get("text");
        data.imageUrl = (String) object.get("image_url");
        data.thumbUrl = (String) object.get("thumb_url");
        data.footer = (String) object.get("footer");
        data.footerIcon = (String) object.get("footer_icon");

        return data;
    }

    public static AttachmentData fromFile(Context context, JSONObject object) {
        JSONObject comment = (JSONObject) object.get("initial_comment");

        String title = (String) object.get("title");
        String pretext = comment != null ? (String) comment.get("comment") : null;

        AttachmentData data = new AttachmentData(context, new Identifier(title, pretext));
        data.title = title;
        data.titleLink = (String) object.get("permalink");
        data.pretext = pretext;

        String type = (String) object.get("filetype");
        if (type != null) {
            switch (type) {
                case "png":
                    data.pretext = null;
                    data.imageUrl = (String) object.get("url_private");
                    data.footer = pretext;
                    break;
                case "text":
                    data.text = (String) object.get("preview");
                    break;
                default:
                    data.text = (String) object.get("preview");
                    data.textType = type;
                    break;
            }
        }

        return data;
    }

    public static AttachmentData fromFile(Context context, SlackFile file) {
        AttachmentData data = new AttachmentData(context, new Identifier(file.getTitle(), file.getComment()));
        data.title = file.getTitle();
        data.titleLink = file.getPermalink();
        data.pretext = file.getName();

        String type = file.getFiletype();
        if (type != null) {
            switch (type) {
                case "png":
                    data.pretext = null;
                    data.imageUrl = file.getUrlPrivate();
                    data.footer = file.getName();
                    break;
                case "text":
                    data.text = file.getName();
                    break;
                default:
                    data.text = file.getName();
                    data.textType = type;
                    break;
            }
        }

        return data;
    }
}
