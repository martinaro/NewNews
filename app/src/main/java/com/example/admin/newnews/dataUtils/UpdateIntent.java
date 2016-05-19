package com.example.admin.newnews.dataUtils;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.admin.newnews.MainActivity;
import com.example.admin.newnews.R;
import com.example.admin.newnews.findMadels.Url;
import com.example.admin.newnews.loadMadels.Entry;
import com.example.admin.newnews.loadMadels.Example;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//this class is not used at this time
public class UpdateIntent extends IntentService implements Loader.OnLoadCompleteListener<Cursor> {
    public static List<Url> urls = new LinkedList<>();
    private static final String TAG = UpdateIntent.class.getSimpleName();
    private static final int LOADER_ID = 1;
    CursorLoader mCursorLoader;
    List<Entry> feedsList = new LinkedList<>();

    /*Per the IntentService documentation:

    To use it, extend IntentService and implement onHandleIntent(Intent). IntentService will receive the Intents,
     launch a worker thread, and stop the service as appropriate.
    This means that as soon as handleIntent() is finished, the service is stopped.

    As handleIntent() is already on a background thread, you should use the synchronous methods to load data
    such as ContentResolver.query() rather than asynchronous methods such as CursorLoader.
     Make sure you close the Cursor returned by query before your method completes!*/
    public UpdateIntent() {
        super("UpdateIntent");
    }

    public void updateUrls(Cursor data) {
        do {
            urls.add(Url.buildFromCursor(data));
        } while (data.moveToNext());

        new MainActivity.MyAsyncTask(new MainActivity.MyAsyncTask.LoadingCallbacks() {
            @Override
            public void onLoad() {

            }

            @Override
            public void onFinishLoading(Example example) {
                insertValues();

                if (example == null) {
                    Toast.makeText(UpdateIntent.this, "pls check your connection!!", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "intent service start already");
        mCursorLoader = new CursorLoader(this, FeedsContentProvider.CONTENT_URI_SITE,
                new String[]{SqlHelper.COLUMN_NEWS_SITE_ID, SqlHelper.COLUMN_URL}, null, null, null);
        mCursorLoader.registerListener(LOADER_ID, this);
        mCursorLoader.startLoading();

        Log.d(TAG, "will start load");
        //getSupportLoaderManager().initLoader(LOADER_ID, null, MainActivity.this).forceLoad();
        load();
        Log.d(TAG, " start already");
    }

    public Example load() {

        int id;

        Example example = null;
        int longOfEntryList = 0;

        Entry entry;
        try {
            URL url = null;
            int j;
            Log.d(TAG, "  " + MainActivity.urls.size());
            /*
            do {
                urls.add(Url.buildFromCursor(cursor));
            } while (cursor.moveToNext());
            */
            for (int i = 0; i < MainActivity.urls.size(); i++) {
                Url siteUrl = MainActivity.urls.get(i);
                id = siteUrl.getId();
                url = new URL("https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&q=" +
                        URLEncoder.encode(siteUrl.getUrl(), "UTF-8"));
                Log.d(TAG, "acynctesk" + siteUrl.getUrl());
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response okHttpResponse = null;
                okHttpResponse = client.newCall(request).execute();

                ObjectMapper objectMapper = new ObjectMapper();
                example = objectMapper.readValue(okHttpResponse.body().byteStream(), Example.class);

                for (j = 0; j < example.getResponseData().getFeed().getEntries().size(); j++) {
                    feedsList.add(example.getResponseData().getFeed().getEntries().get(j));
                    entry = feedsList.get(longOfEntryList + j);
                    entry.setSite_id(id);
                    Log.d(TAG, "number of entry " + feedsList.size() + " site_id " + id + " sites " + MainActivity.urls.size());
                }
                longOfEntryList += j;
            }
            insertValues();
        } catch (MalformedURLException e) {
            Log.d(TAG, e.getMessage() + " MalformedURLException");
        } catch (JsonMappingException e) {
            Log.d(TAG, e.getMessage() + " JsonMappingException");
        } catch (JsonParseException e) {
            Log.d(TAG, e.getMessage() + " JsonParseException");
        } catch (IOException e) {
            Log.d(TAG, e.getMessage() + " IOException");
        }
        return example;
    }

    public void insertValues() {

        Entry entry;
        ContentValues contentValues = new ContentValues();
        for (int k = 0; k < feedsList.size(); k++) {
            entry = feedsList.get(k);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
            try {
                Date date = sdf.parse(entry.getPublishedDate());
                long startDate = date.getTime();
                contentValues.put(SqlHelper.COLUMN_PUBLISHED_DATE, Long.valueOf(startDate));
            } catch (ParseException e) {
                //e.printStackTrace();
            }
            // contentValues.put(SqlHelper.COLUMN_ID, entry.getId());
            contentValues.put(SqlHelper.COLUMN_FEED_TITLE, entry.getTitle());
            contentValues.put(SqlHelper.COLUMN_CONTENT_SNIPPET, entry.getContentSnippet());
            contentValues.put(SqlHelper.COLUMN_LINK, entry.getLink());
            contentValues.put(SqlHelper.COLUMN_CONTENT, entry.getContent());
            contentValues.put(SqlHelper.COLUMN_SITE_ID, entry.getSite_id());
            contentValues.put(SqlHelper.COLUMN_IS_READ, entry.getIs_read());
            if (!titleExists(entry)) {
                getContentResolver().insert(FeedsContentProvider.CONTENT_URI_FEED, contentValues);
            }
        }
    }

    boolean titleExists(Entry entry) {
        SqlHelper sqlHelper = new SqlHelper(this);
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db,
                SqlHelper.TABLE_NEWS_FEEDS, SqlHelper.COLUMN_FEED_TITLE + " = ?", new String[]{entry.getTitle()});
        return count > 0;
    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            Log.d(TAG, "We have " + data.getCount() + " items");
            if (data.getCount() == 0) {
                return;
            }
            if (data.moveToFirst()) {
                Log.d(TAG, "Load Finished");
                do {
                    MainActivity.urls.add(Url.buildFromCursor(data));
                } while (data.moveToNext());
            }
        } else {
            Log.w(TAG, "Data returned null!");
        }
    }

    @Override
    public void onDestroy() {
        // Stop the cursor loader
        if (mCursorLoader != null) {
            mCursorLoader.unregisterListener(this);
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }
    }
}
