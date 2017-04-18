package doubledotlabs.butteryslack.data;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import doubledotlabs.butteryslack.adapters.BaseItemAdapter;
import doubledotlabs.butteryslack.utils.CustomTabsBuilder;
import doubledotlabs.butteryslack.utils.SlackMovementMethod;
import doubledotlabs.butteryslack.utils.SlackUtils;

public class AttachmentData extends BaseItemAdapter.BaseItem<AttachmentData.ViewHolder> implements View.OnClickListener {

    public static final String TYPE_ATTACHMENT = "attachment";
    public static final String TYPE_FILE = "file";

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

    public AttachmentData(SlackAttachment attachment) {
        title = attachment.getTitle();
        titleLink = attachment.getTitleLink();
        authorName = attachment.getAuthorName();
        authorLink = attachment.getAuthorLink();
        authorIcon = attachment.getAuthorIcon();
        pretext = attachment.getPretext();
        text = attachment.getText();
        imageUrl = attachment.getImageUrl();
        thumbUrl = attachment.getThumbUrl();
        footer = attachment.getFooter();
        footerIcon = attachment.getFooterIcon();
    }

    public AttachmentData(String type, JSONObject object) {
        switch (type) {
            case TYPE_ATTACHMENT:
                title = (String) object.get("title");
                titleLink = (String) object.get("title_link");
                authorName = (String) object.get("author_name");
                authorLink = (String) object.get("author_link");
                authorIcon = (String) object.get("author_icon");
                pretext = (String) object.get("pretext");
                text = (String) object.get("text");
                imageUrl = (String) object.get("image_url");
                thumbUrl = (String) object.get("thumb_url");
                footer = (String) object.get("footer");
                footerIcon = (String) object.get("footer_icon");
                break;
            case TYPE_FILE:
                JSONObject comment = (JSONObject) object.get("initial_comment");

                String title = (String) object.get("title");
                String pretext = comment != null ? (String) comment.get("comment") : null;

                this.title = title;
                titleLink = (String) object.get("permalink");
                this.pretext = pretext;

                String fileType = (String) object.get("filetype");
                if (fileType != null) {
                    switch (fileType) {
                        case "png":
                            pretext = null;
                            imageUrl = (String) object.get("url_private");
                            footer = pretext;
                            break;
                        case "text":
                            text = (String) object.get("preview");
                            break;
                        default:
                            text = (String) object.get("preview");
                            textType = fileType;
                            break;
                    }
                }
                break;
        }
    }

    public AttachmentData(SlackFile file) {
        title = file.getTitle();
        titleLink = file.getPermalink();
        pretext = file.getName();

        String type = file.getFiletype();
        if (type != null) {
            switch (type) {
                case "png":
                    pretext = null;
                    imageUrl = file.getUrlPrivate();
                    footer = file.getName();
                    break;
                case "text":
                    text = file.getName();
                    break;
                default:
                    text = file.getName();
                    textType = type;
                    break;
            }
        }
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_attachment, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(this);

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
            CustomTabsBuilder.open(getContext(), Uri.parse(titleLink));
        else if (authorLink != null)
            CustomTabsBuilder.open(getContext(), Uri.parse(authorLink));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, subtitle;
        View authorIconContainer, thumbIconContainer, imageContainer, footerIconContainer;
        ImageView authorIcon, thumbIcon, image, footerIcon;
        TextView authorName, footerName;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            authorIconContainer = itemView.findViewById(R.id.authorIconContainer);
            thumbIconContainer = itemView.findViewById(R.id.thumbIconContainer);
            imageContainer = itemView.findViewById(R.id.imageContainer);
            footerIconContainer = itemView.findViewById(R.id.footerIconContainer);
            authorIcon = (ImageView) itemView.findViewById(R.id.authorIcon);
            thumbIcon = (ImageView) itemView.findViewById(R.id.thumbIcon);
            image = (ImageView) itemView.findViewById(R.id.image);
            footerIcon = (ImageView) itemView.findViewById(R.id.footerIcon);
            authorName = (TextView) itemView.findViewById(R.id.authorName);
            footerName = (TextView) itemView.findViewById(R.id.footerName);
        }
    }
}
