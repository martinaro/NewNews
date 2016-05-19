package com.example.admin.newnews.findMadels;

/**
 * Created by Admin on 11/25/2015.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.text.Html;

import java.util.HashMap;
import java.util.Map;

import com.example.admin.newnews.dataUtils.SqlHelper;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "url",
        "title",
        "contentSnippet",
        "link",

})
public class Entry {

    private int _id = -1;

    private boolean isChecked;
    @JsonProperty("url")
    private String url;
    @JsonProperty("title")
    private String title;
    @JsonProperty("contentSnippet")
    private String contentSnippet;
    @JsonProperty("link")
    private String link;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Entry() {

    }


    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    /**
     * @return The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The contentSnippet
     */
    @JsonProperty("contentSnippet")
    public String getContentSnippet() {
        return contentSnippet;
    }

    /**
     * @param contentSnippet The contentSnippet
     */
    @JsonProperty("contentSnippet")
    public void setContentSnippet(String contentSnippet) {
        this.contentSnippet = contentSnippet;
    }

    /**
     * @return The link
     */
    @JsonProperty("link")
    public String getLink() {
        return link;
    }

    /**
     * @param link The link
     */
    @JsonProperty("link")
    public void setLink(String link) {
        this.link = link;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Entry) {
            return ((Entry) o)._id == this._id;
        }
        return false;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        if (_id != -1) contentValues.put(SqlHelper.COLUMN_NEWS_SITE_ID, _id);
        contentValues.put(SqlHelper.COLUMN_SITE_TITLE, title);
        contentValues.put(SqlHelper.COLUMN_SITE_CONTENT_SNIPPET, contentSnippet);
        contentValues.put(SqlHelper.COLUMN_SITE_LINK, link);
        contentValues.put(SqlHelper.COLUMN_URL, url);
        return contentValues;
    }

    public static Entry buildFromCursor(Cursor cursor) {
        int newsSiteIdColumn = cursor.getColumnIndex(SqlHelper.COLUMN_NEWS_SITE_ID);
        int titleColumn = cursor.getColumnIndex(SqlHelper.COLUMN_SITE_TITLE);
        int contentSnippetColumn = cursor.getColumnIndex(SqlHelper.COLUMN_SITE_CONTENT_SNIPPET);
        int linkColumn = cursor.getColumnIndex(SqlHelper.COLUMN_SITE_LINK);
        int urlColumn = cursor.getColumnIndex(SqlHelper.COLUMN_URL);

        if (newsSiteIdColumn == -1 || titleColumn == -1 || contentSnippetColumn == -1 || linkColumn == -1 || urlColumn == -1) {
            throw new RuntimeException("Database must have all the columns for the news!");
        }
        Entry entry = new Entry();
        entry._id = cursor.getInt(newsSiteIdColumn);
        entry.title = cursor.getString(titleColumn);
        entry.contentSnippet = cursor.getString(contentSnippetColumn);
        entry.link = cursor.getString(linkColumn);
        entry.url = cursor.getString(urlColumn);
        return entry;
    }

}
