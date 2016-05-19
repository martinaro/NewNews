package com.example.admin.newnews.findMadels;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.admin.newnews.dataUtils.SqlHelper;
import com.example.admin.newnews.loadMadels.*;
import com.example.admin.newnews.loadMadels.Entry;

/**
 * Created by Admin on 1/5/2016.
 */
public class Url {
    private String url;
    private int id;
    private String name;

    public Url() {
    }

    public Url(String url, int id) {
        this.url = url;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Url buildFromCursor(Cursor cursor) {
        int idColumn = cursor.getColumnIndex(SqlHelper.COLUMN_NEWS_SITE_ID);
        int urlColumn = cursor.getColumnIndex(SqlHelper.COLUMN_URL);
        if (idColumn == -1 || urlColumn == -1) {
            throw new RuntimeException("Database must have all the columns for the todo item!");
        }
        Url url = new Url();
        url.id = cursor.getInt(idColumn);
        url.url = cursor.getString(urlColumn);
        return url;
    }
}
