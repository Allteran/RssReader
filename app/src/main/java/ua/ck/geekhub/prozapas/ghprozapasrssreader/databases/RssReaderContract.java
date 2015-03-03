package ua.ck.geekhub.prozapas.ghprozapasrssreader.databases;

import android.provider.BaseColumns;

/**
 * Created by Allteran on 13.12.2014.
 */
public final class RssReaderContract {

    public RssReaderContract() {
    }

    //Inner class that defines the table content
    public static abstract class RssItemTable implements BaseColumns {
        public static final String TABLE_NAME = "RssItemTable";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_CONTENTSNIPPET = "contentSnippet";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_PUBLISHDATE = "publishDate";
    }
}
