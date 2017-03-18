package doubledotlabs.butteryslack.data;

import android.content.Context;
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
    private String pretext, text;
    @Nullable
    private String imageUrl, thumbUrl;
    @Nullable
    private String footer, footerIcon;

    public AttachmentData(Context context, SlackAttachment attachment) {
        super(context, new Identifier(attachment.getTitle(), attachment.getPretext()));
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

    public AttachmentData(Context context, JSONObject object) {
        super(context, new Identifier((String) object.get("title"), (String) object.get("pretext")));
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
    }

    public AttachmentData(Context context, String title, String titleLink, String authorName, String authorLink, String authorIcon, String pretext, String text, String imageUrl, String thumbUrl, String footer, String footerIcon) {
        super(context, new Identifier(title, pretext));
        this.title = title;
        this.titleLink = titleLink;
        this.authorName = authorName;
        this.authorLink = authorLink;
        this.authorIcon = authorIcon;
        this.pretext = pretext;
        this.text = text;
        this.imageUrl = imageUrl;
        this.thumbUrl = thumbUrl;
        this.footer = footer;
        this.footerIcon = footerIcon;
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_attachment, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (!(holder.title.getMovementMethod() instanceof SlackMovementMethod) && getContext() instanceof AppCompatActivity)
            holder.title.setMovementMethod(new SlackMovementMethod((AppCompatActivity) getContext()));

        if (titleLink != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                holder.title.setText(Html.fromHtml(SlackUtils.getHtmlLink(titleLink, title), 0));
            else holder.title.setText(Html.fromHtml(SlackUtils.getHtmlLink(titleLink, title)));
        }

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

        if (footer != null) {
            holder.footerName.setVisibility(View.VISIBLE);
            holder.footerName.setText(footer);
        } else holder.footerName.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
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
}
