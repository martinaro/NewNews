package com.example.admin.newnews.loadMadels;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.admin.newnews.dataUtils.SqlHelper;

/**
 * Created by Admin on 12/29/2015.
 */
//this class is not used at this time

public class Load {

    private int id;
    private String article;
    long date;
    private String title;
    private String link;
    private String author;
    private String publishedDate;
    private String contentSnippet;
    private String content;
    private int site_id;


    public Load() {
    }

    public Load(int id, String article, long date) {
        this.id = id;
        this.article = article;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getContentSnippet() {
        return contentSnippet;
    }

    public void setContentSnippet(String contentSnippet) {
        this.contentSnippet = contentSnippet;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        if (id != -1) contentValues.put(SqlHelper.COLUMN_ID, id);
        contentValues.put(SqlHelper.COLUMN_PUBLISHED_DATE, publishedDate);
        contentValues.put(SqlHelper.COLUMN_FEED_TITLE, title);
        contentValues.put(SqlHelper.COLUMN_CONTENT_SNIPPET, contentSnippet);
        contentValues.put(SqlHelper.COLUMN_LINK, link);
        contentValues.put(SqlHelper.COLUMN_CONTENT, content);
        contentValues.put(SqlHelper.COLUMN_SITE_ID, site_id);

        return contentValues;
    }

    public static Load buildFromCursor(Cursor cursor) {
        int idColumn = cursor.getColumnIndex(SqlHelper.COLUMN_ID);
        int publishedDateColumn = cursor.getColumnIndex(SqlHelper.COLUMN_PUBLISHED_DATE);
        int titleColumn = cursor.getColumnIndex(SqlHelper.COLUMN_FEED_TITLE);
        int contentSnippetColumn = cursor.getColumnIndex(SqlHelper.COLUMN_CONTENT_SNIPPET);
        int linkColumn = cursor.getColumnIndex(SqlHelper.COLUMN_LINK);
        int contentColumn = cursor.getColumnIndex(SqlHelper.COLUMN_CONTENT);
        int site_idColumn = cursor.getColumnIndex(SqlHelper.COLUMN_SITE_ID);

        if (idColumn == -1 || publishedDateColumn == -1 || titleColumn == -1 || contentSnippetColumn == -1 || linkColumn == -1 ||
                contentColumn == -1 || site_idColumn == -1) {
            throw new RuntimeException("Database must have all the columns for the todo item!");
        }
        Load load = new Load();
        load.id = cursor.getInt(idColumn);
        load.publishedDate = cursor.getString(publishedDateColumn);
        load.title = cursor.getString(titleColumn);
        load.contentSnippet = cursor.getString(contentSnippetColumn);
        load.link = cursor.getString(linkColumn);
        load.content = cursor.getString(contentColumn);
        load.site_id = cursor.getInt(site_idColumn);

        return load;
    }
}

