package com.example.admin.newnews;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.admin.newnews.adapter.FeedAdapter;
import com.example.admin.newnews.dataUtils.FeedsContentProvider;
import com.example.admin.newnews.dataUtils.MyReceiver;
import com.example.admin.newnews.dataUtils.SqlHelper;
import com.example.admin.newnews.dataUtils.UpdateIntent;
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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_ID = 10;
    public static final int NOTIFICATION_ID = 45;
    private static final int LOADER_URL_ID = 1;
    public static List<Url> urls = new LinkedList<>();

    DrawerLayout drawer;
    RecyclerView recyclerViewFeeds;
    FeedAdapter adapter;
    ProgressBar progressBar2;
    String name = null;
    SqlHelper sqlHelper;
    static Context context;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sqlHelper = new SqlHelper(this);
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra(KEY);
        }
        setUpGUI();
        context = getApplicationContext();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setUpGUI() {
        setUpRecyclerView();
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);

        Log.d(TAG, "intent service start");
        Intent intent = new Intent(MainActivity.this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), NOTIFICATION_ID, intent, 0);
        startService(intent);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (50 * 1000), NOTIFICATION_ID, pendingIntent);
        Log.d(TAG, "intent service continue");
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.START |
                ItemTouchHelper.END, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final Entry entry = adapter.getItem(viewHolder.getAdapterPosition());
                Uri uri = Uri.parse(FeedsContentProvider.CONTENT_URI_FEED + "/" + entry.getId());
                Log.d(TAG, "entry id " + entry.getId());
                try {
                    getContentResolver().delete(uri, null, null);
                } catch (Exception e) {
                }
                adapter.removeItem(viewHolder.getAdapterPosition());
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerViewFeeds);
        getSupportLoaderManager().initLoader(LOADER_ID, null, MainActivity.this).forceLoad();
    }

    public static final String KEY = "key title";

    public void setUpRecyclerView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerViewFeeds = (RecyclerView) findViewById(R.id.recyclerViewFeeds);
        adapter = new FeedAdapter(new FeedAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(com.example.admin.newnews.loadMadels.Entry item, RecyclerView.ViewHolder viewHolder) {

            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerViewFeeds.setAdapter(adapter);
        recyclerViewFeeds.setLayoutManager(new LinearLayoutManager(this));
        getSupportLoaderManager().initLoader(LOADER_URL_ID, null, MainActivity.this);
        getSupportLoaderManager().initLoader(LOADER_ID, null, MainActivity.this);
    }

    public void updateAdapter(List<Entry> feedSearches) {
        adapter.addFeed(feedSearches);
    }

    public void setLoading(boolean isLoading) {
        if (isLoading == true) {
            recyclerViewFeeds.setVisibility(View.GONE);
            progressBar2.setVisibility(View.VISIBLE);
        } else {
            recyclerViewFeeds.setVisibility(View.VISIBLE);
            progressBar2.setVisibility(View.GONE);
            recyclerViewFeeds.getItemAnimator();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.fox) {
            // Handle the camera action
        } else if (id == R.id.cnn) {

        } else if (id == R.id.nav_settings_for_feeds) {
            startActivity(new Intent(this, LoadActivity.class));

        } else if (id == R.id.cnsbc) {

        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "aaaa");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_send) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Loader Created");
        switch (id) {
            case LOADER_URL_ID:
                Log.d(TAG, "Loader url Created");
                return new CursorLoader(this, FeedsContentProvider.CONTENT_URI_SITE,
                        new String[]{SqlHelper.COLUMN_NEWS_SITE_ID, SqlHelper.COLUMN_URL}, null, null, null);

            case LOADER_ID:
                Log.d(TAG, "Loader id Created");
                return new CursorLoader(this, FeedsContentProvider.CONTENT_URI_FEED,
                        null, null, null, FeedsContentProvider.FEED_SORT_ORDER);

        }
        return new CursorLoader(this, null, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.d(TAG, "Load Finished");
        if (data != null) {
            Log.d(TAG, "We have " + data.getCount() + " items");
            if (data.getCount() == 0) {
                return;
            }
            if (data.moveToFirst()) {
                if (loader.getId() == LOADER_URL_ID) {

                    Log.d(TAG, "Load Finished");
                    do {
                        urls.add(Url.buildFromCursor(data));
                    } while (data.moveToNext());

                    new MyAsyncTask(new MyAsyncTask.LoadingCallbacks() {
                        @Override
                        public void onLoad() {
                            setLoading(true);
                        }

                        @Override
                        public void onFinishLoading(Example example) {
                            insertValues();
                            setLoading(false);
                            if (example == null) {
                                Toast.makeText(MainActivity.this, "pls check your connection!!", Toast.LENGTH_SHORT).show();
                            }
                            getSupportLoaderManager().initLoader(LOADER_ID, null, MainActivity.this);
                        }
                    }).execute();
                } else {

                    Log.d(TAG, "loder_id");
                    List<Entry> entry = new LinkedList<>();
                    do {
                        entry.add(Entry.buildFromCursor(data));
                    } while (data.moveToNext());
                    updateAdapter(entry);
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        } else {
            Log.w(TAG, "Data returned null!");
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Loader reset");
        getSupportLoaderManager().initLoader(LOADER_ID, null, MainActivity.this);
    }

    @Override
    public void onRefresh() {
        new MyAsyncTask(new MyAsyncTask.LoadingCallbacks() {
            @Override
            public void onLoad() {
            }

            @Override
            public void onFinishLoading(Example example) {
                insertValues();
                if (example == null) {
                    Toast.makeText(MainActivity.this, "pls check your connection!!", Toast.LENGTH_SHORT).show();
                }
                getSupportLoaderManager().initLoader(LOADER_ID, null, MainActivity.this);
            }
        }).execute();

    }

    public void insertValues() {
        getContentResolver().delete(FeedsContentProvider.CONTENT_URI_FEED, SqlHelper.COLUMN_PUBLISHED_DATE + " <= ?",
                new String[]{String.valueOf(System.currentTimeMillis() - 3 * 24 * 3600 * 1000)});
        Log.d(TAG, "     B           feeds deleted");
        Entry entry;
        ContentValues contentValues = new ContentValues();
        for (int k = 0; k < MyAsyncTask.feedsList.size(); k++) {
            entry = MyAsyncTask.feedsList.get(k);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
            try {
                Date date = sdf.parse(entry.getPublishedDate());
                long startDate = date.getTime();
                contentValues.put(SqlHelper.COLUMN_PUBLISHED_DATE, Long.valueOf(startDate));
                contentValues.put(SqlHelper.COLUMN_FEED_TITLE, entry.getTitle());
                contentValues.put(SqlHelper.COLUMN_CONTENT_SNIPPET, entry.getContentSnippet());
                contentValues.put(SqlHelper.COLUMN_LINK, entry.getLink());
                contentValues.put(SqlHelper.COLUMN_CONTENT, entry.getContent());
                contentValues.put(SqlHelper.COLUMN_SITE_ID, entry.getSite_id());
                contentValues.put(SqlHelper.COLUMN_IS_READ, entry.getIs_read());
                if (!titleExists(entry)) {
                    Log.d(TAG, "  A  id is:" + SqlHelper.COLUMN_ID + " site id is:" + entry.getSite_id());

                    if (startDate > System.currentTimeMillis() - (3 * 24 * 3600 * 1000) || startDate < System.currentTimeMillis()) {
                        getContentResolver().insert(FeedsContentProvider.CONTENT_URI_FEED, contentValues);
                    }
                }
            } catch (ParseException e) {
                //e.printStackTrace();
            }


        }
    }

    boolean titleExists(Entry entry) {
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db,
                SqlHelper.TABLE_NEWS_FEEDS, SqlHelper.COLUMN_FEED_TITLE + " = ?", new String[]{entry.getTitle()});
        return count > 0;
    }

    public static class MyAsyncTask extends AsyncTask<String, Void, Example> {
        public static List<Entry> feedsList = new LinkedList<>();
        public int id;
        private LoadingCallbacks myLadingCallbacks;

        public MyAsyncTask() {
        }

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
            int longOfEntryList = 0;
            Entry entry;
            try {
                URL url = null;
                int j;
                Log.d(TAG, " A " + urls.size());

                for (int i = 0; i < urls.size(); i++) {
                    Url siteUrl = urls.get(i);
                    Log.d(TAG, " A " + siteUrl.getId());
                    id = siteUrl.getId();
                    url = new URL("https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&q=" +
                            URLEncoder.encode(siteUrl.getUrl(), "UTF-8"));
                    Log.d(TAG, "  A acynctesk" + siteUrl.getUrl());
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response okhttpResponse = null;
                    okhttpResponse = client.newCall(request).execute();

                    ObjectMapper objectMapper = new ObjectMapper();
                    example = objectMapper.readValue(okhttpResponse.body().byteStream(), Example.class);

                    for (j = 0; j < example.getResponseData().getFeed().getEntries().size(); j++) {
                        feedsList.add(example.getResponseData().getFeed().getEntries().get(j));
                        entry = feedsList.get(longOfEntryList + j);
                        entry.setSite_id(id);
                        Log.d(TAG, "number of entrys " + feedsList.size() + " site_id " + id + " sites " + urls.size());
                    }
                    longOfEntryList += j;
                }
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
}

