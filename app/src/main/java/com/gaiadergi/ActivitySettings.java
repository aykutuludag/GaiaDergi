package com.gaiadergi;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.onesignal.OneSignal;

public class ActivitySettings extends AppCompatActivity {

    Window window;
    Toolbar toolbar;
    SharedPreferences prefs;
    boolean alarm, edergi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        t.setScreenName("Ayarlar");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
        edergi = prefs.getBoolean("edergi", false);
        alarm = prefs.getBoolean("Alarm", true);

        Switch alarmSwitch = findViewById(R.id.mySwitch);
        if (alarm) {
            alarmSwitch.setChecked(true);
        } else {
            alarmSwitch.setChecked(false);
        }

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs.edit().putBoolean("Alarm", true).apply();
                    OneSignal.startInit(ActivitySettings.this)
                            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                            .unsubscribeWhenNotificationsAreDisabled(true)
                            .init();
                } else {
                    prefs.edit().putBoolean("Alarm", false).apply();
                    OneSignal.setSubscription(false);
                }
            }
        });

        TextView textViewAccount = findViewById(R.id.textViewAccountInfo);
        if (edergi) {
            textViewAccount.setText(R.string.account_premium);
        } else {
            textViewAccount.setText(R.string.account_standart);
        }

        TextView textViewVersion = findViewById(R.id.textViewVersionInfo);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = "v" + pInfo.versionName;
        textViewVersion.setText(version);

        ImageView beta = findViewById(R.id.imageButton2);
        beta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Privacy
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                builder.enableUrlBarHiding();
                builder.setShowTitle(true);
                builder.setToolbarColor(Color.parseColor("#212121"));
                customTabsIntent.launchUrl(ActivitySettings.this, Uri.parse("https://play.google.com/apps/testing/com.gaiadergi"));
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
        finish();
    }
}