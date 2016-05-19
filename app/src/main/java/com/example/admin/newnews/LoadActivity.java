package com.example.admin.newnews;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.admin.newnews.adapter.NewSiteAdapter;
import com.example.admin.newnews.dataUtils.FeedsContentProvider;
import com.example.admin.newnews.dataUtils.SqlHelper;
import com.example.admin.newnews.findMadels.Example;
import com.example.admin.newnews.findMadels.Entry;
import com.example.admin.newnews.findMadels.Find;
import com.example.admin.newnews.findMadels.ResponseData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LoadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = LoadActivity.class.getSimpleName();
    private static final int LOADER_ID = 2;

    CardView cardView;
    EditText editText;
    ImageButton imageButton;
    Button saveSiteButton;
    RecyclerView recyclerView;
    NewSiteAdapter adapter;
    ProgressBar progressBar;
    SqlHelper sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("News In Any Subject");
        setUpGUI();


    }

    private void setUpGUI() {
        setUpRecyclerView();
        editText = (EditText) findViewById(R.id.editText);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
       // saveSiteButton = (Button) findViewById(R.id.saveSiteButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        sql = new SqlHelper(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.START | ItemTouchHelper.END,
                ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final Entry entry = adapter.getItem(viewHolder.getAdapterPosition());
                Uri uri = Uri.parse(FeedsContentProvider.CONTENT_URI_SITE + "/" + entry.getId());
                Log.d(TAG, "entry id " + entry.getId());
                try {
                    getContentResolver().delete(uri, null, null);
                } catch (Exception e) {

                }
                adapter.removeItem(viewHolder.getAdapterPosition());
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
        getSupportLoaderManager().initLoader(LOADER_ID, null, LoadActivity.this).forceLoad();
    }

    public void setUpRecyclerView() {
        cardView = (CardView) findViewById(R.id.cardView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewSiteAdapter(this, new NewSiteAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(Entry entry, RecyclerView.ViewHolder viewHolder) {
                getContentResolver().insert(FeedsContentProvider.CONTENT_URI_SITE, entry.toContentValues());
                Log.d(TAG, "on item click " + " item saved " + entry.toContentValues());
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void updateAdapter(List<Entry> feedSearches) {
        adapter.addFeed(feedSearches);
    }

    public void setLoading(boolean isLoading) {
        if (isLoading == true) {
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        new MyAsyncTask(new MyAsyncTask.LoadingCallbacks() {
            @Override
            public void onLoad() {
                setLoading(true);
            }

            @Override
            public void onFinishLoading(Example example) {
                setLoading(false);
                if (example == null) {
                    Toast.makeText(LoadActivity.this, "pls check your connection!!", Toast.LENGTH_SHORT).show();
                } else if (example != null) {
                    updateAdapter(example.getResponseData().getEntries());
                } else {
                    Toast.makeText(LoadActivity.this, "Such a Website is not found! ", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute(editText.getText().toString());
        getSupportLoaderManager().initLoader(LOADER_ID, null, LoadActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected static class MyAsyncTask extends AsyncTask<String, Void, Example> {
        private LoadingCallbacks myLadingCallbacks;

        public MyAsyncTask(LoadingCallbacks Callbacks) {
            myLadingCallbacks = Callbacks;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myLadingCallbacks.onLoad();
        }

        @Override
        protected Example doInBackground(String... params) {
            Example example = null;
            URL url = null;
            Response okhttpResponse = null;
            try {
                url = new URL("https://ajax.googleapis.com/ajax/services/feed/find?v=1.0&q=" + URLEncoder.encode(params[0], "UTF-8"));
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                okhttpResponse = client.newCall(request).execute();
                ObjectMapper objectMapper = new ObjectMapper();
                example = objectMapper.readValue(okhttpResponse.body().byteStream(), Example.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return example;
        }

        @Override
        protected void onPostExecute(Example data) {
            super.onPostExecute(data);
            myLadingCallbacks.onFinishLoading(data);
        }

        public interface LoadingCallbacks {
            public void onLoad();

            public void onFinishLoading(Example example);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Loader Created");
        return new CursorLoader(this, FeedsContentProvider.CONTENT_URI_SITE,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Load Finished");
        if (data != null) {
            Log.d(TAG, "We have " + data.getCount() + " items");
            if (data.getCount() == 0) {
                return;
            }
            data.moveToFirst();
            List<Entry> entry = new LinkedList<>();
            do {
                entry.add(Entry.buildFromCursor(data));
            } while (data.moveToNext());
            adapter.addFeed(entry);
        } else {
            Log.w(TAG, "Data returned null!");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Loader reset");

    }
}
