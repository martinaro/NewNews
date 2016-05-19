package com.example.admin.newnews.findMadels;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.admin.newnews.dataUtils.SqlHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Admin on 12/29/2015.
 */
//this class is not used at this time
public class Find {

    private int id;
    private String name;
    private int checked;
    private String contentSnippet;
    private String link;
    private String url;
    private boolean isChecked;
    public static List<String> checkList = new LinkedList<>();

    public Find() {

    }

    public Find(String name, int checked) {
        setName(name);
        setChecked(checked);
    }

    public Find(int id, String name, int checked) {
        this.id = id;
        this.name = name;
        this.checked = checked;
    }

    public Find(int id, String name, String contentSnippet, String link, String url, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.contentSnippet = contentSnippet;
        this.link = link;
        this.url = url;
        setIsChecked(isChecked);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getId() {
        return id;

    }

    public static List<String> getCheckList() {
        return checkList;
    }

    public static void setCheckList(List<String> checkList) {
        Find.checkList = checkList;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) throws IllegalArgumentException {
        if (checked < 0 || checked > 1) {
            throw new IllegalArgumentException("Enter 1 for checked or 0 if not");
        }
        this.checked = checked;
    }

    public String getContentSnippet() {
        return contentSnippet;
    }

    public void setContentSnippet(String contentSnippet) {
        this.contentSnippet = contentSnippet;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Find{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", checked=" + checked +
                '}';
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        if (id != -1) contentValues.put(SqlHelper.COLUMN_NEWS_SITE_ID, id);
        contentValues.put(SqlHelper.COLUMN_SITE_TITLE, name);
        contentValues.put(SqlHelper.COLUMN_SITE_CONTENT_SNIPPET, contentSnippet);
        contentValues.put(SqlHelper.COLUMN_SITE_LINK, link);
        contentValues.put(SqlHelper.COLUMN_URL, url);
        return contentValues;
    }

    public static Find buildFromCursor(Cursor cursor) {
        int newsSiteIdColumn = cursor.getColumnIndex(SqlHelper.COLUMN_NEWS_SITE_ID);
        int titleColumn = cursor.getColumnIndex(SqlHelper.COLUMN_SITE_TITLE);
        int contentSnippetColumn = cursor.getColumnIndex(SqlHelper.COLUMN_SITE_CONTENT_SNIPPET);
        int linkColumn = cursor.getColumnIndex(SqlHelper.COLUMN_SITE_LINK);
        int urlColumn = cursor.getColumnIndex(SqlHelper.COLUMN_URL);

        if (newsSiteIdColumn == -1 || titleColumn == -1 || contentSnippetColumn == -1 || linkColumn == -1 || urlColumn == -1) {
            throw new RuntimeException("Database must have all the columns for the news!");
        }
        Find find = new Find();
        find.id = cursor.getInt(newsSiteIdColumn);
        find.name = cursor.getString(titleColumn);
        find.contentSnippet = cursor.getString(contentSnippetColumn);
        find.link = cursor.getString(linkColumn);
        find.url = cursor.getString(urlColumn);
        return find;
    }

}

