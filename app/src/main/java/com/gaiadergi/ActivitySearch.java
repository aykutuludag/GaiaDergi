package com.gaiadergi;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gaiadergi.adapter.RecyclerViewAdapter;
import com.gaiadergi.model.GridItem;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ActivitySearch extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    SearchView searchView;
    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ProgressBar pb;
    Window window;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //StatusBar
        window = this.getWindow();

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coloredBars(ContextCompat.getColor(this, R.color.colorHomeDark), ContextCompat.getColor(this, R.color.colorHomePrimary));

        pb = findViewById(R.id.progressBar1);
        pb.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorHomePrimary), PorterDuff.Mode.SRC_IN);

        mRecyclerView = findViewById(R.id.recycler_view);

        // Analytics
        Tracker t = ((AnalyticsApplication) this.getApplication()).getDefaultTracker();
        t.setScreenName("Arama");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adds item to action bar
        getMenuInflater().inflate(R.menu.activity_search_menu, menu);
        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.action_search_online);
        SearchManager searchManager = (SearchManager) ActivitySearch.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(ActivitySearch.this.getComponentName()));
            searchView.setIconified(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // Every time when you press search button on keypad an Activity is recreated which in turn calls this function
    @Override
    protected void onNewIntent(Intent intent) {
        // Get search query and create object of class AsyncFetch
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }
            new AsyncFetch(query).execute();
            pb.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void coloredBars(int color1, int color2) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color1);
            toolbar.setBackgroundColor(color2);
        } else {
            toolbar.setBackgroundColor(color2);
        }
    }

    // Create class AsyncFetch
    private class AsyncFetch extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        String searchQuery;

        AsyncFetch(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL("https://gaiadergi.com/wp-content/uploads/android-api/post-search.php");
            } catch (MalformedURLException e) {
                Toast.makeText(ActivitySearch.this, R.string.error, Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return e.toString();
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput to true as we send and recieve data
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // add parameter to our above url
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("searchQuery", searchQuery);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                Toast.makeText(ActivitySearch.this, R.string.error, Toast.LENGTH_LONG).show();
                e1.printStackTrace();
                return e1.toString();
            }

            try {
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    Toast.makeText(ActivitySearch.this, R.string.no_internet, Toast.LENGTH_LONG).show();
                    return ("Connection error");
                }
            } catch (IOException e) {
                Toast.makeText(ActivitySearch.this, R.string.error, Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pb.setVisibility(View.INVISIBLE);
            List<GridItem> feedsList = new ArrayList<>();
            if (result.equals("no rows")) {
                Toast.makeText(ActivitySearch.this, R.string.nomatch, Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONArray jArray = new JSONArray(result);

                    // Extract data from json and store into ArrayList as class objects
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        GridItem item = new GridItem();
                        item.setTitle(json_data.getString("post_title"));
                        item.setThumbnail("https://gaiadergi.com/wp-content/uploads/" + json_data.getString("meta_value"));
                        item.setLink("https://gaiadergi.com/" + json_data.getString("post_name") + "/amp");
                        feedsList.add(item);
                    }

                    // Adapter
                    mAdapter = new RecyclerViewAdapter(ActivitySearch.this, feedsList);
                    mRecyclerView.setAdapter(mAdapter);

                    // The number of Columns
                    mLayoutManager = new GridLayoutManager(ActivitySearch.this, 1);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                } catch (JSONException e) {
                    Toast.makeText(ActivitySearch.this, R.string.nomatch, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }
}