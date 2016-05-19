package com.example.admin.newnews.loadMadels;

/**
 * Created by Admin on 11/25/2015.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.example.admin.newnews.BuildConfig;
import com.example.admin.newnews.MainActivity;
import com.example.admin.newnews.dataUtils.SqlHelper;
import com.example.admin.newnews.findMadels.Url;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "title",
        "link",
        "author",
        "publishedDate",
        "contentSnippet",
        "content",
        "categories",

})
public class Entry {
    private int _id = -1;
    //private int id;
    private boolean is_read;
    private int site_id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("link")
    private String link;
    @JsonProperty("author")
    private String author;
    @JsonProperty("publishedDate")
    private String publishedDate;
    @JsonProperty("contentSnippet")
    private String contentSnippet;
    @JsonProperty("content")
    private String content;
    @JsonProperty("categories")
    private List<Object> categories = new ArrayList<Object>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public boolean getIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
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

    /**
     * @return The author
     */
    @JsonProperty("author")
    public String getAuthor() {
        return author;
    }

    /**
     * @param author The author
     */
    @JsonProperty("author")
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return The publishedDate
     */
    @JsonProperty("publishedDate")
    public String getPublishedDate() {
        return publishedDate;
    }

    /**
     * @param publishedDate The publishedDate
     */
    @JsonProperty("publishedDate")
    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
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
     * @return The content
     */
    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    /**
     * @param content The content
     */
    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return The categories
     */
    @JsonProperty("categories")
    public List<Object> getCategories() {
        return categories;
    }

    /**
     * @param categories The categories
     */
    @JsonProperty("categories")
    public void setCategories(List<Object> categories) {
        this.categories = categories;
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

    @Override
    public int hashCode() {
        return _id;
    }

    public ContentValues toContentValues() {

        ContentValues contentValues = new ContentValues();
        if (_id != -1) contentValues.put(SqlHelper.COLUMN_ID, _id);
        contentValues.put(SqlHelper.COLUMN_PUBLISHED_DATE, publishedDate);
        contentValues.put(SqlHelper.COLUMN_FEED_TITLE, title);
        contentValues.put(SqlHelper.COLUMN_CONTENT_SNIPPET, contentSnippet);
        contentValues.put(SqlHelper.COLUMN_LINK, link);
        contentValues.put(SqlHelper.COLUMN_CONTENT, content);
        contentValues.put(SqlHelper.COLUMN_SITE_ID, site_id);
        contentValues.put(SqlHelper.COLUMN_IS_READ, is_read ? 1 : 0);
        return contentValues;
    }

    public static Entry buildFromCursor(Cursor cursor) {
        int idColumn = cursor.getColumnIndex(SqlHelper.COLUMN_ID);
        int publishedDateColumn = cursor.getColumnIndex(SqlHelper.COLUMN_PUBLISHED_DATE);
        int titleColumn = cursor.getColumnIndex(SqlHelper.COLUMN_FEED_TITLE);
        int contentSnippetColumn = cursor.getColumnIndex(SqlHelper.COLUMN_CONTENT_SNIPPET);
        int linkColumn = cursor.getColumnIndex(SqlHelper.COLUMN_LINK);
        int contentColumn = cursor.getColumnIndex(SqlHelper.COLUMN_CONTENT);
        int site_idColumn = cursor.getColumnIndex(SqlHelper.COLUMN_SITE_ID);
        int is_readColumn = cursor.getColumnIndex(SqlHelper.COLUMN_IS_READ);
        if (publishedDateColumn == -1 || titleColumn == -1 || contentSnippetColumn == -1 || linkColumn == -1 ||
                contentColumn == -1 || site_idColumn == -1 || idColumn == -1) {
            throw new RuntimeException("Database must have all the columns for the todo item!");
        }
        Entry entry = new Entry();
        entry._id = cursor.getInt(idColumn);
        entry.publishedDate = (String) DateUtils.getRelativeTimeSpanString(cursor.getLong(publishedDateColumn));
        entry.title = cursor.getString(titleColumn);
        entry.contentSnippet = cursor.getString(contentSnippetColumn);
        entry.link = cursor.getString(linkColumn);
        entry.content = String.valueOf(Html.fromHtml(cursor.getString(contentColumn).replaceAll("<img.+?>", "")));
        entry.site_id = cursor.getInt(site_idColumn);
        entry.is_read = cursor.getInt(is_readColumn) == 1;
        return entry;
    }
}