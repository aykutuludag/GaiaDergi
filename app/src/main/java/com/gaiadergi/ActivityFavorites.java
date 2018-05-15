package com.gaiadergi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gaiadergi.adapter.RecyclerViewAdapter;
import com.gaiadergi.model.GridItem;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class ActivityFavorites extends AppCompatActivity {

    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    List<GridItem> feedsList = new ArrayList<>();
    ProgressBar pb;
    HostnameVerifier hostnameVerifier;
    SQLiteDatabase mobiledatabase;
    Cursor cur;
    ArrayList<String> arraytitle = new ArrayList<>();
    ArrayList<String> arraythumbnail = new ArrayList<>();
    ArrayList<String> arraylink = new ArrayList<>();

    Window window;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

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

        // Analytics
        Tracker t = ((AnalyticsApplication) this.getApplication()).getDefaultTracker();
        t.setScreenName("Favoriler");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        // ConnectivityManager
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // SSL Verifier
        hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("gaiadergi.com", session);
            }
        };

        //getFavorites
        mobiledatabase = openOrCreateDatabase("gaiadergi_mobile", MODE_PRIVATE, null);
        pb.setVisibility(View.INVISIBLE);
        cur = mobiledatabase.rawQuery("Select * from gaiadergi_mobile", null);
        if (cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                for (int i = 0; i < cur.getColumnCount(); i++) {
                    String row_values = cur.getString(i);
                    if (i % 3 == 0) {
                        // For Text
                        arraytitle.add(row_values);
                    } else if (i % 3 == 1) {
                        // For Image
                        arraythumbnail.add(row_values);
                    } else {
                        // For Link
                        arraylink.add(row_values);
                    }
                }
            } while (cur.moveToNext());
        }
        cur.close();
        for (int i = 0; i < arraytitle.size(); i++) {
            GridItem item = new GridItem();
            item.setTitle(arraytitle.get(i));
            item.setThumbnail(arraythumbnail.get(i));
            item.setLink(arraylink.get(i));
            feedsList.add(item);
        }

        //recyclerView
        mRecyclerView = findViewById(R.id.recycler_view);

        // Adapter
        mAdapter = new RecyclerViewAdapter(ActivityFavorites.this, feedsList);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(ActivityFavorites.this, 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(final int position) {
                if ((position % 3) == 0) {
                    return (2);
                } else {
                    return (1);
                }
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (!isConnected) {
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
        mobiledatabase.close();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}