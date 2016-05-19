package com.example.admin.newnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.admin.newnews.dataUtils.SqlHelper;
import com.example.admin.newnews.findMadels.Entry;
import com.example.admin.newnews.MainActivity;
import com.example.admin.newnews.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 11/15/2015.
 */
public class NewSiteAdapter extends RecyclerView.Adapter<NewSiteAdapter.ViewHolder> {
    private static final String TAG = "new site adapter";
    Context context;
    List<Entry> sites = new ArrayList<>();
    List<Entry> find = new ArrayList<>(sites);
    private ItemClickListener itemClickListener;
    SqlHelper sqlHelper = new SqlHelper(context);

    public NewSiteAdapter(Context context, ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.context = context;
        notifyDataSetChanged();
    }

    public void addFeed(List<Entry> addFeed) {
        sites = addFeed;
        notifyDataSetChanged();
    }


    public Entry getItem(int position) {
        return sites.get(position);
    }

    public void removeItem(int position) {
        sites.remove(position);
        notifyItemRemoved(position);
    }

    public void delete() {
        sites.clear();
    }

    private final ClickListener clickListener = new ClickListener() {
        @Override
        public void onItemClicked(int position, RecyclerView.ViewHolder viewHolder, View v) {
            Log.d(TAG, "on click adapter");
            Entry entry = sites.get(position);
            Snackbar.make(v, Html.fromHtml(entry.getTitle()) + " Saved Correctly", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            notifyItemChanged(position);
            itemClickListener.onItemClicked(entry, viewHolder);
        }
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_site, parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Entry search = sites.get(position);
        holder.url.setText(Html.fromHtml(search.getContentSnippet()));
        holder.title.setText(Html.fromHtml(search.getTitle()));
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        int i = 0;
        if (sites != null) {
            i = sites.size();
        }
        Log.d(TAG, "item count " + i);
        return i;

    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, url;
        ImageButton check;
        final ClickListener clickListener;

        public ViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            check = (ImageButton) itemView.findViewById(R.id.checkBox);
            title = (TextView) itemView.findViewById(R.id.title);
            url = (TextView) itemView.findViewById(R.id.url);
            itemView.setOnClickListener(this);
            title.setOnClickListener(this);
            check.setOnClickListener(this);
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClicked(getAdapterPosition(), this, v);
        }


    }

    // Private listener fot the adapter to know about view clicks
    private interface ClickListener {
        void onItemClicked(int position, RecyclerView.ViewHolder viewHolder, View v);
    }

    // Public listener to pass the item back to the activity
    public interface ItemClickListener {
        void onItemClicked(Entry entry, RecyclerView.ViewHolder viewHolder);
    }
}

