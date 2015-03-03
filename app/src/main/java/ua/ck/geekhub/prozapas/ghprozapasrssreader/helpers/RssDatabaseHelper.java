package ua.ck.geekhub.prozapas.ghprozapasrssreader.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ua.ck.geekhub.prozapas.ghprozapasrssreader.databases.RssReaderContract;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.models.RssItem;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.utilities.Const;

/**
 * Created by Allteran on 13.12.2014.
 */
public class RssDatabaseHelper extends SQLiteOpenHelper {
    private final String TAG = getClass().getSimpleName();
    private static final String TEXT_TYPE = " TEXT";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RssReaderContract.RssItemTable.TABLE_NAME + " (" + RssReaderContract
                    .RssItemTable._ID + " INTEGER PRIMARY KEY," + RssReaderContract.RssItemTable
                    .COLUMN_NAME_TITLE + TEXT_TYPE + "," + RssReaderContract.RssItemTable
                    .COLUMN_NAME_CONTENT + TEXT_TYPE + "," + RssReaderContract.RssItemTable
                    .COLUMN_NAME_CONTENTSNIPPET + TEXT_TYPE + "," + RssReaderContract.RssItemTable
                    .COLUMN_NAME_LINK + TEXT_TYPE + "," + RssReaderContract.RssItemTable
                    .COLUMN_NAME_PUBLISHDATE + " DATE" + " )";//program does not parse the date
    //if you are into FavoriteDetailsActivity
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + RssReaderContract
            .RssItemTable.TABLE_NAME;

    public RssDatabaseHelper(Context context) {
        super(context, Const.DATABASE_NAME, null, Const.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addArticle(RssItem item) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RssReaderContract.RssItemTable.COLUMN_NAME_TITLE, item.getTitle());
        values.put(RssReaderContract.RssItemTable.COLUMN_NAME_CONTENT, item.getContent());
        values.put(RssReaderContract.RssItemTable.COLUMN_NAME_CONTENTSNIPPET, item.getContentSnippet());
        values.put(RssReaderContract.RssItemTable.COLUMN_NAME_LINK, item.getLink());
        values.put(RssReaderContract.RssItemTable.COLUMN_NAME_PUBLISHDATE, item.getPublishDate());

        database.insert(RssReaderContract.RssItemTable.TABLE_NAME,
                null,
                values);
        database.close();
    }

    public ArrayList<RssItem> getAllArticles() {
        ArrayList<RssItem> rssList = new ArrayList<>();
        String query = "SELECT * FROM " + RssReaderContract.RssItemTable.TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        RssItem item = null;
        if (cursor.moveToLast()) {
            Log.i(TAG, "Getting articles");
            do {
                item = new RssItem();
                item.setTitle(cursor.getString(1));
                item.setContent(cursor.getString(2));
                item.setContentSnippet(cursor.getString(3));
                item.setLink(cursor.getString(4));
                item.setPublishDate(cursor.getString(5));

                rssList.add(item);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        Log.i(TAG, "got all articles");
        return rssList;
    }

    public void deleteArticle(RssItem article) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(RssReaderContract.RssItemTable.TABLE_NAME,
                RssReaderContract.RssItemTable.COLUMN_NAME_TITLE + " = ?",
                new String[]{String.valueOf(article.getTitle())});
        database.close();
    }

    public void deleteAllArticles() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(RssReaderContract.RssItemTable.TABLE_NAME, null, null);
        Log.i(TAG, "We've deleted all saved articles");
    }

    public boolean isArticleInDatabase(RssItem item) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT COUNT (*) FROM " +
                        RssReaderContract.RssItemTable.TABLE_NAME + " WHERE " +
                        RssReaderContract.RssItemTable.COLUMN_NAME_LINK + " = \"" + item.getLink() + "\"",
                null);
        cursor.moveToFirst();
        boolean result = !(cursor.getInt(0) == 0);
        cursor.close();
        return result;
    }
}