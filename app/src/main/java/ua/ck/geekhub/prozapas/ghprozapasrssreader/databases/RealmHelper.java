package ua.ck.geekhub.prozapas.ghprozapasrssreader.databases;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.models.RssItem;

/**
 * Created by Dante Allteran on 3/14/2015.
 */
public class RealmHelper {
    private final String TAG = getClass().getSimpleName();

    public void addArticle(RssItem currentItem, Realm realm) {
        RealmRssItem realmRssItem = new RealmRssItem(currentItem);

        realm.beginTransaction();
        RealmRssItem realmItem = realm.createObject(RealmRssItem.class);

        realmItem.setTitle(realmRssItem.getTitle());
        realmItem.setContent(realmRssItem.getContent());
        realmItem.setContentSnippet(realmRssItem.getContentSnippet());
        realmItem.setLink(realmRssItem.getLink());
        realmItem.setPublishDate(realmRssItem.getPublishDate());
        realmItem.setImageURL(realmRssItem.getImageURL());

        realm.commitTransaction();
        Log.i(TAG, "Article has been added to realm database");
    }

    public ArrayList<RssItem> getAllArticles(Realm realm) {
        List<RealmRssItem> rssItemsFromDB = realm.where(RealmRssItem.class).findAll();

        ArrayList<RssItem> articlesFromDB = new ArrayList<>();
        for (int i = 0; i < rssItemsFromDB.size(); i++) {
            RssItem rssItem = new RssItem(rssItemsFromDB.get(i));
            articlesFromDB.add(rssItem);
        }
        return articlesFromDB;
    }

    public void deleteArticle(Realm realm, RssItem incItem) {
        List<RealmRssItem> result = realm.where(RealmRssItem.class).equalTo("link", incItem.getLink())
                .findAll();
        realm.beginTransaction();
        result.remove(0);
        realm.commitTransaction();
    }

    public boolean isArticleInDatabase(Realm realm, RssItem incArticle) {
        List<RealmRssItem> result = realm.where(RealmRssItem.class).equalTo("link", incArticle.getLink())
                .findAll();
        return result.size() != 0;
    }

    public void deleteAllArticles(Realm realm, List<RssItem> rssItems) {
        List<RealmRssItem> result = realm.where(RealmRssItem.class).findAll();
        realm.beginTransaction();
        int size = result.size();
        for (int i = 0; i < size; i++) {
            result.remove(i);
        }
        realm.commitTransaction();
        rssItems.clear();
    }
}
