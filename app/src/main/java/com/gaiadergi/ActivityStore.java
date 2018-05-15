package com.gaiadergi;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
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

public class ActivityStore extends AppCompatActivity {

    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    RelativeLayout relLoading, relNormal, relPremium;
    Button btn1, btn2;
    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    List<GridItem> feedsList;
    ProgressBar pb;
    Window window;
    Toolbar toolbar;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

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
        t.setScreenName("Mağaza");
        t.send(new HitBuilders.ScreenViewBuilder().build());

        //Layouts
        relLoading = findViewById(R.id.loading_screen);
        relNormal = findViewById(R.id.buy_screen);
        relPremium = findViewById(R.id.read_screen);

        //Preferences
        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);

        //InAppBilling
        InAppBilling();
    }

    private void InAppBilling() {
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
                Toast.makeText(ActivityStore.this, "Bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.",
                        Toast.LENGTH_LONG).show();
                relLoading.setVisibility(View.VISIBLE);
                relNormal.setVerticalGravity(View.INVISIBLE);
                relPremium.setVerticalGravity(View.INVISIBLE);
                finish();
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                try {
                    checkPremium();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    public void checkPremium() throws RemoteException {
        Bundle ownedItems = mService.getPurchases(3, getPackageName(), "subs", null);
        if (ownedItems.getInt("RESPONSE_CODE") == 0) {
            ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            if (ownedSkus != null && ownedSkus.contains("edergi")) {
                premiumLayout();
                prefs.edit().putBoolean("edergi", true).apply();
            } else {
                regularLayout();
                prefs.edit().putBoolean("edergi", false).apply();
            }
        } else {
            Toast.makeText(ActivityStore.this, "Bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.",
                    Toast.LENGTH_LONG).show();
            relLoading.setVisibility(View.VISIBLE);
            relNormal.setVerticalGravity(View.INVISIBLE);
            relPremium.setVerticalGravity(View.INVISIBLE);
            finish();
        }
    }

    //Regular layout
    public void regularLayout() {
        relLoading.setVisibility(View.INVISIBLE);
        relNormal.setVisibility(View.VISIBLE);

        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);

        View.OnClickListener buttonListener = new View.OnClickListener() {
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.button1:
                        try {
                            buyPremium();
                        } catch (RemoteException | IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.button2:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gaiadergi.com/magaza"));
                        startActivity(intent);
                        break;
                }
            }
        };

        btn1.setOnClickListener(buttonListener);
        btn2.setOnClickListener(buttonListener);
    }

    //Premium layout
    public void premiumLayout() {
        relLoading.setVisibility(View.INVISIBLE);
        relPremium.setVisibility(View.VISIBLE);

        mRecyclerView = findViewById(R.id.recycler_view);
        pb = findViewById(R.id.progressBar1);
        pb.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorHomePrimary), PorterDuff.Mode.SRC_IN);

        ActivityStore.ExecuteNetworkOperation rssTask = new ActivityStore.ExecuteNetworkOperation();
        rssTask.execute();
    }

    //Buying staffs
    public void buyPremium() throws RemoteException, IntentSender.SendIntentException {
        Toast.makeText(ActivityStore.this,
                "Aylık 3.49 TL'ye Gaia Dergi'nin mevcut ve çıkacak tüm e-dergilerini okuyabilirsiniz.", Toast.LENGTH_LONG)
                .show();
        Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), "edergi", "subs",
                "/tYMgwhg1DVikb4R4iLNAO5pNj/QWh19+vwajyUFbAyw93xVnDkeTZFdhdSdJ8M");
        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
        assert pendingIntent != null;
        startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0,
                0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(ActivityStore.this, "Satın alma başarılı. E-dergi aboneliğiniz aktif ediliyor. İyi okumalar...",
                        Toast.LENGTH_LONG).show();

                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(ActivityStore.this, "Bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.",
                        Toast.LENGTH_LONG).show();
            }
        }
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
        relLoading.setVisibility(View.VISIBLE);
        relNormal.setVerticalGravity(View.INVISIBLE);
        relPremium.setVerticalGravity(View.INVISIBLE);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceConn != null) {
            unbindService(mServiceConn);
            finish();
        }
    }

    private class ExecuteNetworkOperation extends AsyncTask<String, Void, ArrayList<RssItem>> {
        URL url;
        RssFeed feed;

        @Override
        protected ArrayList<RssItem> doInBackground(String... params) {
            try {
                url = new URL("https://gaiadergi.com/wp-content/uploads/android-api/sellinglist.xml");
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

        @Override
        protected void onPostExecute(ArrayList<RssItem> result) {
            super.onPostExecute(result);
            pb.setVisibility(View.INVISIBLE);
            ArrayList<RssItem> rssItems = feed.getRssItems();
            feedsList = new ArrayList<>();

            for (RssItem rssItem : rssItems) {
                GridItem item = new GridItem();

                // For Text
                item.setTitle(rssItem.getTitle());

                // For Image
                item.setThumbnail(rssItem.getDescription());

                // For Link
                item.setLink(rssItem.getLink());

                feedsList.add(item);
            }
            // Adapter
            mAdapter = new RecyclerViewAdapter(ActivityStore.this, feedsList);
            mRecyclerView.setAdapter(mAdapter);

            // The number of Columns
            mLayoutManager = new GridLayoutManager(ActivityStore.this, 1);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
    }
}