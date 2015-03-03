package ua.ck.geekhub.prozapas.ghprozapasrssreader.models;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Allteran on 15.11.2014.
 */
public class RssItem implements Serializable {
    private final String TAG = getClass().getSimpleName();

    private String title;
    private String link;
    private String contentSnippet;
    private String content;
    private String publishDate;
    private String imageURL;


    public String getPublishDate() {
        Log.i(TAG, "We've got date");
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContentSnippet() {
        return contentSnippet.replace("\n", "");
    }

    public void setContentSnippet(String contentSnippet) {
        this.contentSnippet = contentSnippet;
    }

    public String getContent() {
        return content;
    }

    public String getContentWithoutImageURL() {
       return content.replace(content.substring(content.lastIndexOf("<img"), content.indexOf("br") + 4), "");
    }

    public void setImageUrl() {
        imageURL = content.substring(getContent().lastIndexOf("http"), getContent().indexOf("jpg") + 3);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageURL;
    }
}