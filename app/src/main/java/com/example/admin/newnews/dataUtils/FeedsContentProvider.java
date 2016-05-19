package com.example.admin.newnews.dataUtils;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v7.appcompat.BuildConfig;
import android.text.TextUtils;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Admin on 12/21/2015.
 */
public class FeedsContentProvider extends ContentProvider {
    public static final String TAG = FeedsContentProvider.class.getSimpleName();
    private SqlHelper database;
    private static final String AUTHORITY = "com.example.admin.newnews.dataUtils.FeedsContentProvider.contentProvider";
    private static final String BASE_PATH_SITE = "sites";
    private static final String BASE_PATH_FEED = "feeds";
    public static final Uri CONTENT_URI_SITE = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_SITE);
    public static final Uri CONTENT_URI_FEED = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_FEED);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/feeds";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/feed";
    public static final String FEED_SORT_ORDER = "publishedDate DESC ";
    public static final String DELETE_FEEDS = SqlHelper.COLUMN_PUBLISHED_DATE + " < " + Calendar.getInstance().getTimeInMillis();
    // used for the UriMacher
    private static final int SITES = 10;
    private static final int SITE_ID = 20;
    private static final int FEEDS = 30;
    private static final int FEED_ID = 40;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_FEED, FEEDS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_FEED + "/#", FEED_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SITE, SITES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SITE + "/#", SITE_ID);
    }

    public FeedsContentProvider() {

    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        database = new SqlHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db;
        Cursor cursor;
        int uriType = sURIMatcher.match(uri);
        switch (sURIMatcher.match(uri)) {
            case SITES:
                queryBuilder.setTables(SqlHelper.TABLE_NEWS_SITES);
                switch (uriType) {
                    case SITES:
                        break;
                    case SITE_ID:
                        queryBuilder.appendWhere(SqlHelper.COLUMN_NEWS_SITE_ID + "=" + uri.getLastPathSegment());
                }
                db = database.getWritableDatabase();
                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case FEEDS:
                queryBuilder.setTables(SqlHelper.TABLE_NEWS_FEEDS);
                switch (uriType) {
                    case FEEDS:
                        break;
                    case FEED_ID:
                        queryBuilder.appendWhere(SqlHelper.COLUMN_ID + "=" + uri.getLastPathSegment());
                }
                db = database.getWritableDatabase();

                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri _uri = null;
        SQLiteDatabase db = database.getWritableDatabase();
        switch (sURIMatcher.match(uri)) {
            case SITES:
                long _ID_SITE = db.insert(SqlHelper.TABLE_NEWS_SITES, null, values);
                //---if added successfully---
                if (_ID_SITE > 0) {
                    //_uri=Uri.parse(BASE_PATH_SITE+"/"+_ID_SITE);
                    _uri = ContentUris.withAppendedId(CONTENT_URI_SITE, _ID_SITE);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            case FEEDS:

                long _ID_FEED = db.insertWithOnConflict(SqlHelper.TABLE_NEWS_FEEDS, BaseColumns._ID, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                //long _ID_FEED = db.insert(SqlHelper.TABLE_NEWS_FEEDS, null, values);
                //---if added successfully---
                if (_ID_FEED > 0) {
                    //_uri=Uri.parse(BASE_PATH_FEED+"/"+_ID_FEED);
                    _uri = ContentUris.withAppendedId(CONTENT_URI_FEED, _ID_FEED);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            default:
                throw new SQLException("Failed to insert row into " + uri);
        }
        return _uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete");
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (sURIMatcher.match(uri)) {
            case FEEDS:
                switch (uriType) {
                    case FEEDS:
                        rowsDeleted = db.delete(SqlHelper.TABLE_NEWS_FEEDS, selection, selectionArgs);
                        break;
                    case FEED_ID:
                        String id = uri.getLastPathSegment();
                        if (TextUtils.isEmpty(selection)) {
                            rowsDeleted = db.delete(SqlHelper.TABLE_NEWS_FEEDS, SqlHelper.COLUMN_ID + "=" + id, null);
                        } else {
                            rowsDeleted = db.delete(SqlHelper.TABLE_NEWS_FEEDS, SqlHelper.COLUMN_ID + "=" + id
                                    + " and " + selection, selectionArgs);
                            Log.d(TAG, " feed id " + rowsDeleted);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown URI: " + uri);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case SITES:
                switch (uriType) {
                    case SITES:
                        rowsDeleted = db.delete(SqlHelper.TABLE_NEWS_SITES, selection, selectionArgs);
                        break;
                    case SITE_ID:
                        String id = uri.getLastPathSegment();
                        if (TextUtils.isEmpty(selection)) {
                            rowsDeleted = db.delete(SqlHelper.TABLE_NEWS_SITES, SqlHelper.COLUMN_NEWS_SITE_ID + "=" + id, null);
                        } else {
                            rowsDeleted = db.delete(SqlHelper.TABLE_NEWS_SITES, SqlHelper.COLUMN_NEWS_SITE_ID + "=" + id
                                    + " and " + selection, selectionArgs);
                        }
                        break;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update");
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (sURIMatcher.match(uri)) {
            case FEEDS:
                switch (uriType) {
                    case FEEDS:
                        rowsUpdated = sqlDB.update(SqlHelper.TABLE_NEWS_FEEDS, values, selection, selectionArgs);
                        break;
                    case FEED_ID:
                        String id = uri.getLastPathSegment();
                        if (TextUtils.isEmpty(selection)) {
                            rowsUpdated = sqlDB.update(SqlHelper.TABLE_NEWS_FEEDS, values, SqlHelper.COLUMN_ID + "=" + id, null);
                        } else {
                            rowsUpdated = sqlDB.update(SqlHelper.TABLE_NEWS_FEEDS, values, SqlHelper.COLUMN_ID + "=" + id
                                    + " and " + selection, selectionArgs);
                        }
                        break;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsUpdated;
            case SITES:
                switch (uriType) {
                    case SITES:
                        rowsUpdated = sqlDB.update(SqlHelper.TABLE_NEWS_SITES, values, selection, selectionArgs);
                        break;
                    case SITE_ID:
                        String id = uri.getLastPathSegment();
                        if (TextUtils.isEmpty(selection)) {
                            rowsUpdated = sqlDB.update(SqlHelper.TABLE_NEWS_SITES, values, SqlHelper.COLUMN_NEWS_SITE_ID
                                    + "=" + id, null);
                        } else {
                            rowsUpdated = sqlDB.update(SqlHelper.TABLE_NEWS_SITES, values, SqlHelper.COLUMN_NEWS_SITE_ID
                                    + "=" + id + " and " + selection, selectionArgs);
                        }
                        break;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsUpdated;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

    }
}
