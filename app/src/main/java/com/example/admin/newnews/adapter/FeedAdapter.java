package com.example.admin.newnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.appcompat.BuildConfig;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.admin.newnews.ContentActivity;
import com.example.admin.newnews.LoadActivity;
import com.example.admin.newnews.dataUtils.FeedsContentProvider;
import com.example.admin.newnews.dataUtils.SqlHelper;
import com.example.admin.newnews.findMadels.Url;
import com.example.admin.newnews.loadMadels.Entry;
import com.example.admin.newnews.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 11/26/2015.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private static final String TAG = " Feed Adapter";
    List<Entry> feeds = new ArrayList<>();
    List<Url> urlFeed = new ArrayList<>();

    public FeedAdapter(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FeedAdapter() {
    }

    public void addUrlFeed(List<Url> addFeed) {
        urlFeed = addFeed;
        notifyDataSetChanged();
    }

    public void addFeed(List<Entry> addFeed) {
        feeds = addFeed;
        notifyDataSetChanged();
    }

    public Entry getItem(int position) {
        return feeds.get(position);
    }

    public void removeItem(int position) {
        feeds.remove(position);
        notifyItemRemoved(position);
    }

    public void delete() {
        feeds.clear();

    }

    ItemClickListener itemClickListener;
    protected final ClickListener clickListener = new ClickListener() {
        @Override
        public void onItemClicked(int position, RecyclerView.ViewHolder viewHolder, View v) {
            Log.d(TAG, "on click adapter");
            // Get the item that was clicked
            Entry items = feeds.get(position);
            Intent intent = new Intent(v.getContext(), ContentActivity.class);
            intent.putExtra("key title", items.getTitle());
            intent.putExtra("key content", items.getContent());
            intent.putExtra("key link", items.getLink());
            v.getContext().startActivity(intent);
            Log.d(TAG, items.getTitle());
            itemClickListener.onItemClicked(items, viewHolder);
        }
    };

    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_feed, parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(final FeedAdapter.ViewHolder holder, int position) {
        final Entry search = feeds.get(position);
        holder.titleText.setText(search.getTitle());
        holder.expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.contentSnippetText.setVisibility(View.VISIBLE);
                holder.linkText.setVisibility(View.VISIBLE);
                holder.closeButton.setVisibility(View.VISIBLE);
                holder.expandButton.setVisibility(View.GONE);
                Log.d(TAG, "expand");
            }
        });
        holder.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.contentSnippetText.setVisibility(View.GONE);
                holder.linkText.setVisibility(View.GONE);
                holder.closeButton.setVisibility(View.GONE);
                holder.expandButton.setVisibility(View.VISIBLE);
                Log.d(TAG, "close");
            }
        });
        holder.contentSnippetText.setText(Html.fromHtml(search.getContentSnippet()));
        holder.publishedDateText.setText(Html.fromHtml(search.getPublishedDate()));
        holder.linkText.setText("For full article:\n " + Html.fromHtml(search.getLink()));
        holder.linkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(search.getLink()));
                v.getContext().startActivity(intent);
            }
        });
        holder.linkText.setTextColor(Color.parseColor("#8994f7"));
    }

    @Override
    public int getItemCount() {
        int i = 0;
        if (feeds != null) {
            i = feeds.size();
        }
        return i;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleText, contentSnippetText, publishedDateText, linkText, categoryText;
        ImageButton expandButton, closeButton;
        final ClickListener clickListener;

        public ViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
            contentSnippetText = (TextView) itemView.findViewById(R.id.contentSnippetText);
            publishedDateText = (TextView) itemView.findViewById(R.id.publishedDateText);
            linkText = (TextView) itemView.findViewById(R.id.linkText);
            categoryText = (TextView) itemView.findViewById(R.id.categoryText);
            expandButton = (ImageButton) itemView.findViewById(R.id.expandButton);
            closeButton = (ImageButton) itemView.findViewById(R.id.closeButton);
            linkText.setPaintFlags(linkText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            titleText.setOnClickListener(this);
            itemView.setOnClickListener(this);
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClicked(getAdapterPosition(), this, v);

        }
    }

    private interface ClickListener {
        void onItemClicked(int position, RecyclerView.ViewHolder viewHolder, View v);
    }

    public interface ItemClickListener {
        void onItemClicked(Entry item, RecyclerView.ViewHolder viewHolder);
    }
}

