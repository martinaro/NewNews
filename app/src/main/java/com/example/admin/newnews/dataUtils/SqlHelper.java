package com.example.admin.newnews.dataUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.admin.newnews.findMadels.Entry;
import com.example.admin.newnews.findMadels.Find;
import com.example.admin.newnews.loadMadels.Load;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Admin on 12/21/2015.
 */
public class SqlHelper extends SQLiteOpenHelper {

    private static final String TAG = SqlHelper.class.getSimpleName();

    public static final String TABLE_NEWS_FEEDS = "news_feeds";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PUBLISHED_DATE = "publishedDate";
    public static final String COLUMN_FEED_TITLE = "title";
    public static final String COLUMN_CONTENT_SNIPPET = "contentSnippet";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_SITE_ID = "site_id";
    public static final String COLUMN_IS_READ = "is_read";
    private static final String DATABASE_FEED_CREATE =
            "CREATE TABLE " + TABLE_NEWS_FEEDS
                    + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_PUBLISHED_DATE + " INTEGER NOT NULL, "
                    + COLUMN_FEED_TITLE + " TEXT NOT NULL UNIQUE, "
                    + COLUMN_CONTENT_SNIPPET + " TEXT NOT NULL, "
                    + COLUMN_CONTENT + " TEXT NOT NULL, "
                    + COLUMN_LINK + "  TEXT NOT NULL,"
                    + COLUMN_SITE_ID + "  INTEGER NOT NULL,"
                    + COLUMN_IS_READ + "  INTEGER NOT NULL"
                    + ");";


    public static final String TABLE_NEWS_SITES = "news_sites";

    public static final String COLUMN_NEWS_SITE_ID = "_id";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_SITE_TITLE = "SiteTitle";
    public static final String COLUMN_SITE_CONTENT_SNIPPET = "siteContentSnippet";
    public static final String COLUMN_SITE_LINK = "siteLink";
    private static final String DATABASE_SITE_CREATE =
            "CREATE TABLE " + TABLE_NEWS_SITES
                    + " (" + COLUMN_NEWS_SITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_URL + "  TEXT NOT NULL UNIQUE,"
                    + COLUMN_SITE_TITLE + "  TEXT NOT NULL,"
                    + COLUMN_SITE_CONTENT_SNIPPET + "  TEXT NOT NULL,"
                    + COLUMN_SITE_LINK + "  TEXT NOT NULL" +
                    ");";

    private static final String DATABASE_NAME = "news_feeds.db";
    private static final int DATABASE_VERSION = 13;

    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "DB created");
        db.execSQL(DATABASE_SITE_CREATE);
        db.execSQL(DATABASE_FEED_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "DB upgrading. Dropping old table...");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS_FEEDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS_SITES);
        onCreate(db);
    }

}

