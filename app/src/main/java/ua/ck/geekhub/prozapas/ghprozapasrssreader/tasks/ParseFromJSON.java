package ua.ck.geekhub.prozapas.ghprozapasrssreader.tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.ck.geekhub.prozapas.ghprozapasrssreader.models.RssItem;

/**
 * Created by Allteran on 16.11.2014.
 */
public class ParseFromJSON {
    private ArrayList<RssItem> mEntriesList = null;

    public ParseFromJSON(JSONObject jsonObject) {
        parseJSON(jsonObject);
    }

    public ArrayList<RssItem> getRssList() {
        return mEntriesList;
    }

    private void parseJSON(JSONObject jsonObject) {
        try {
            JSONObject responseData = jsonObject.getJSONObject("responseData");
            JSONObject feed = responseData.getJSONObject("feed");
            JSONArray entries = feed.getJSONArray("entries");
            mEntriesList = new ArrayList<RssItem>();
            for (int i = 0; i < entries.length(); i++) {
                JSONObject entry = entries.getJSONObject(i);

                RssItem rssItem = new RssItem();
                rssItem.setTitle(entry.getString("title"));
                rssItem.setLink(entry.getString("link"));
                rssItem.setPublishDate(entry.getString("publishedDate"));
                rssItem.setContentSnippet(entry.getString("contentSnippet"));
                rssItem.setContent(entry.getString("content"));

                mEntriesList.add(rssItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
