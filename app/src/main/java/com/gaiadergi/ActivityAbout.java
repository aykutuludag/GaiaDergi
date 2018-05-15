package com.gaiadergi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ActivityAbout extends AppCompatActivity {

    String str0 = "https://gaiadergi.com";
    String str1 = "https://facebook.com/dergigaia";
    String str2 = "https://twitter.com/gaiadergi";
    String str3 = "https://plus.google.com/u/0/+GaiaDergi";
    String str4 = "https://instagram.com/gaiadergi";
    String str5 = "https://linkedin.com/company/gaia-dergi";
    String str6 = "https://gaiadergi.com/feed";
    String str7 = "http://gaiadergi.tumblr.com";
    String str8 = "https://youtube.com/channel/UCorDAuLS9gj1gFvLdmf1p2Q";

    Window window;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        //StatusBar
        window = this.getWindow();

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coloredBars(ContextCompat.getColor(this, R.color.colorHomeDark), ContextCompat.getColor(this, R.color.colorHomePrimary));

        // Analytics
        Tracker t = ((AnalyticsApplication) this.getApplication()).getDefaultTracker();
        t.setScreenName("Hakkımızda");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        final ImageView button0 = findViewById(R.id.imageView1);
        final ImageView button1 = findViewById(R.id.imageView3);
        final ImageView button2 = findViewById(R.id.imageView4);
        final ImageView button3 = findViewById(R.id.imageView5);
        final ImageView button4 = findViewById(R.id.imageView6);
        final ImageView button5 = findViewById(R.id.imageView7);
        final ImageView button6 = findViewById(R.id.imageView8);
        final ImageView button7 = findViewById(R.id.imageView9);
        final ImageView button8 = findViewById(R.id.imageView10);
        final ImageView button9 = findViewById(R.id.imageView11);
        final ImageView button10 = findViewById(R.id.imageView12);

        OnClickListener buttonListener = new OnClickListener() {
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.imageView1:
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("plain/text");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"iletisim@gaiadergi.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "Gaia Dergi Android Uygulaması");
                        try {
                            startActivity(Intent.createChooser(i, getString(R.string.send_email)));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(ActivityAbout.this, getString(R.string.no_app), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.imageView3:
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:+905325778789"));
                        startActivity(callIntent);
                        break;
                    case R.id.imageView4:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str0));
                        startActivity(intent);
                        break;
                    case R.id.imageView5:
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(str1));
                        startActivity(intent2);
                        break;
                    case R.id.imageView6:
                        Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(str2));
                        startActivity(intent3);
                        break;
                    case R.id.imageView7:
                        Intent intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse(str3));
                        startActivity(intent4);
                        break;
                    case R.id.imageView8:
                        Intent intent5 = new Intent(Intent.ACTION_VIEW, Uri.parse(str4));
                        startActivity(intent5);
                        break;
                    case R.id.imageView9:
                        Intent intent6 = new Intent(Intent.ACTION_VIEW, Uri.parse(str5));
                        startActivity(intent6);
                        break;
                    case R.id.imageView10:
                        Intent intent7 = new Intent(Intent.ACTION_VIEW, Uri.parse(str6));
                        startActivity(intent7);
                        break;
                    case R.id.imageView11:
                        Intent intent8 = new Intent(Intent.ACTION_VIEW, Uri.parse(str7));
                        startActivity(intent8);
                        break;
                    case R.id.imageView12:
                        Intent intent9 = new Intent(Intent.ACTION_VIEW, Uri.parse(str8));
                        startActivity(intent9);
                        break;
                }
            }
        };

        button0.setOnClickListener(buttonListener);
        button1.setOnClickListener(buttonListener);
        button2.setOnClickListener(buttonListener);
        button3.setOnClickListener(buttonListener);
        button4.setOnClickListener(buttonListener);
        button5.setOnClickListener(buttonListener);
        button6.setOnClickListener(buttonListener);
        button7.setOnClickListener(buttonListener);
        button8.setOnClickListener(buttonListener);
        button9.setOnClickListener(buttonListener);
        button10.setOnClickListener(buttonListener);
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