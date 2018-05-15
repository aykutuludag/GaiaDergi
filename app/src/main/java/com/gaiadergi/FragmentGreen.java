package com.gaiadergi;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gaiadergi.adapter.RecyclerViewAdapter;
import com.gaiadergi.model.GridItem;
import com.gaiadergi.rssreader.RssFeed;
import com.gaiadergi.rssreader.RssItem;
import com.gaiadergi.rssreader.RssReader;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FragmentGreen extends Fragment {

    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    URL url;
    RssFeed feed;
    ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorGreenDark));
        }
        ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
        assert bar != null;
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.colorGreenPrimary)));

        pb = v.findViewById(R.id.progressBar1);
        pb.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorGreenPrimary), PorterDuff.Mode.SRC_IN);

        // Analytics
        Tracker t = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
        t.setScreenName("Yeşil");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        mRecyclerView = v.findViewById(R.id.recycler_view);

        if (isNetworkConnected()) {
            ExecuteNetworkOperation rssTask = new ExecuteNetworkOperation();
            rssTask.execute();
        } else {
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private class ExecuteNetworkOperation extends AsyncTask<String, Void, ArrayList<RssItem>> {

        @Override
        protected ArrayList<RssItem> doInBackground(String... params) {
            try {
                url = new URL("https://gaiadergi.com/kategori/yesil/feed");
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                feed = RssReader.read(url);
            } catch (SAXException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(ArrayList<RssItem> result) {
            pb.setVisibility(View.INVISIBLE);
            ArrayList<RssItem> rssItems = feed.getRssItems();
            List<GridItem> feedsList = new ArrayList<>();

            for (RssItem rssItem : rssItems) {
                GridItem item = new GridItem();

                // For Text
                item.setTitle(rssItem.getTitle());

                // For Image
                String s = rssItem.getDescription();
                String[] tokens = s.split("\"");
                item.setThumbnail(tokens[5]);

                // For Link
                item.setLink(rssItem.getLink());

                feedsList.add(item);
            }
            // Adapter
            mAdapter = new RecyclerViewAdapter(getActivity(), feedsList);
            mRecyclerView.setAdapter(mAdapter);

            // The number of Columns
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
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
        }
    }
}