package ua.ck.geekhub.prozapas.ghprozapasrssreader.utilities;

/**
 * Created by Allteran on 11.12.2014.
 */
public final class Const {
    public static final String BUNDLE_POSITION = "position";
    public static final String BUNDLE_ENTRIES = "entries";
    public static final String KEY_POSITION = "position";
    public static final String KEY_RSSLIST = "rssItemList";
    public static final String ARG_ENTRY = "entry";
    public static final String URL = "https://ajax.googleapis.com/ajax/services/feed/load?v=2.0&num=10&q=http://naked-science.ru/feedrss.xml";
    public static final String SHARED_PREFERECES_KEY = "saved";
    public static final String DATABASE_NAME = "RssReader.db";
    public static final String DB_CHECKER_KEY = "news or db";

    public static final int NOTIFICATION_ID = 1;
    public static final int DATABASE_VERSION = 1;
    public static final long SLEEP_UPDATE_TIME = 300;
    public static final int ACTION_ADD_TO_FAV_ID = 1;
    public static final int ACTION_DELETE_ARTICLE_ID = 4;
    public static final int ACTION_SHARE = 5;
}
