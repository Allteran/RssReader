package ua.ck.geekhub.prozapas.ghprozapasrssreader.models;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.databases.RealmRssItem;

/**
 * Created by Allteran on 15.11.2014.
 */
public class RssItem  implements Serializable{

    private String title;
    private String link;

    private String contentSnippet;
    private String content;
    private String publishDate;
    private String imageURL;

    public RssItem() {

    }

    public RssItem(RealmRssItem realmRssItem) {
        this.title = realmRssItem.getTitle();
        this.link = realmRssItem.getLink();
        this.contentSnippet = realmRssItem.getContentSnippet();
        this.content = realmRssItem.getContent();
        this.publishDate = realmRssItem.getPublishDate();
        this.imageURL = realmRssItem.getImageURL();
    }

    public String getPublishDate() {
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
        return contentSnippet;
    }

    public void setContentSnippet(String contentSnippet) {
        this.contentSnippet = contentSnippet;
    }

    public String getContent() {
        return content;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setContent(String content) {
        this.content = content;
    }
}