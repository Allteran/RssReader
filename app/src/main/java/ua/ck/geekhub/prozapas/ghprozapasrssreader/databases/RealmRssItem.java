package ua.ck.geekhub.prozapas.ghprozapasrssreader.databases;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.models.RssItem;

/**
 * Created by Dante on 4/9/2015.
 */
@RealmClass
public class RealmRssItem extends RealmObject {
    private String title;
    private String link;

    private String contentSnippet;
    private String content;
    private String publishDate;
    private String imageURL;

    public RealmRssItem() {

    }

    public RealmRssItem(RssItem rssItem) {
        this.title = rssItem.getTitle();
        this.link = rssItem.getLink();
        this.contentSnippet = rssItem.getContentSnippet();
        this.content = rssItem.getContent();
        this.publishDate = rssItem.getPublishDate();
        this.imageURL = rssItem.getImageURL();
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

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
