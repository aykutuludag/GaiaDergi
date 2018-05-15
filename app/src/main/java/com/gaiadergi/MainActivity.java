package com.gaiadergi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.kobakei.ratethisapp.RateThisApp;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {
    public static InterstitialAd interstitial;
    boolean doubleBackToExitPressedOnce = false;
    SQLiteDatabase mobiledatabase, mobiledatabase2;

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    SharedPreferences prefs;
    boolean isAlarmOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
        isAlarmOn = prefs.getBoolean("Alarm", true);

        if (isAlarmOn) {
            OneSignal.startInit(this)
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .unsubscribeWhenNotificationsAreDisabled(true)
                    .init();
        } else {
            OneSignal.setSubscription(false);
        }

        //Ads
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5450686271846727/3163724091");
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("1570E844A1342361F2C23869919CF823").build();
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-5450686271846727/3163724091");
        interstitial.loadAd(adRequest);

        //Create databese
        mobiledatabase = openOrCreateDatabase("gaiadergi_mobile", MODE_PRIVATE, null);
        mobiledatabase.execSQL("CREATE TABLE IF NOT EXISTS gaiadergi_mobile(Title TEXT,Thumbnail VARCHAR, Link VARCHAR);");
        mobiledatabase2 = openOrCreateDatabase("gaiadergi_mobile2", MODE_PRIVATE, null);
        mobiledatabase2.execSQL("CREATE TABLE IF NOT EXISTS gaiadergi_mobile2(Title TEXT,Thumbnail VARCHAR, Link VARCHAR);");

        // Initializing Toolbar and setting it as the actionbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer openes as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        //Initializing NavigationView
        navigationView = findViewById(R.id.nav_view);

        //Add Navigation header and its ClickListener
        View headerView = getLayoutInflater().inflate(R.layout.nav_header, navigationView, false);
        navigationView.addHeaderView(headerView);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Gaia Dergi; ırkçılığa, cinsiyet ayrımcılığına, türcülüğe ve betonlaşmaya karşı, doğadan ve doğaldan yana yaşamı savunan alternatif bir platformdur.", Toast.LENGTH_LONG).show();
            }
        });

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        menuItem.setChecked(true);
                        Fragment fragment = new FragmentHome();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Home").commit();
                        toolbar.setTitle(R.string.nav_text_home);
                        return true;
                    case R.id.nav_green:
                        menuItem.setChecked(true);
                        Fragment fragment2 = new FragmentGreen();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment2, "Green").commit();
                        toolbar.setTitle(R.string.nav_text_green);
                        return true;
                    case R.id.nav_life:
                        menuItem.setChecked(true);
                        Fragment fragment3 = new FragmentLife();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment3, "Life").commit();
                        toolbar.setTitle(R.string.nav_text_life);
                        return true;
                    case R.id.nav_ecology:
                        menuItem.setChecked(true);
                        Fragment fragment4 = new FragmentEcology();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment4, "Ecology").commit();
                        toolbar.setTitle(R.string.nav_text_ecology);
                        return true;
                    case R.id.nav_culture:
                        menuItem.setChecked(true);
                        Fragment fragment5 = new FragmentCulture();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment5, "Culture").commit();
                        toolbar.setTitle(R.string.nav_text_culture);
                        return true;
                    case R.id.nav_science:
                        menuItem.setChecked(true);
                        Fragment fragment6 = new FragmentScience();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment6, "Science").commit();
                        toolbar.setTitle(R.string.nav_text_science);
                        return true;
                    case R.id.nav_turkey:
                        menuItem.setChecked(true);
                        Fragment fragment7 = new FragmentTurkey();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment7, "Turkey").commit();
                        toolbar.setTitle(R.string.nav_text_turkey);
                        return true;
                    case R.id.nav_events:
                        menuItem.setChecked(true);
                        Fragment fragment8 = new FragmentEvents();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment8, "Events").commit();
                        toolbar.setTitle(R.string.nav_text_events);
                        return true;
                    case R.id.nav_society:
                        menuItem.setChecked(true);
                        Fragment fragment9 = new FragmentSociety();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment9, "Society").commit();
                        toolbar.setTitle(R.string.nav_text_society);
                        return true;
                    case R.id.nav_favorites:
                        Intent intent = new Intent(MainActivity.this, ActivityFavorites.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_sharings:
                        Intent intent2 = new Intent(MainActivity.this, ActivitySharings.class);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_settings:
                        Intent intent4 = new Intent(MainActivity.this, ActivitySettings.class);
                        startActivity(intent4);
                        return true;
                    case R.id.nav_store:
                        Intent intent5 = new Intent(MainActivity.this, ActivityStore.class);
                        startActivity(intent5);
                        return true;
                    case R.id.nav_about:
                        Intent intent6 = new Intent(MainActivity.this, ActivityAbout.class);
                        startActivity(intent6);
                        return true;
                    case R.id.nav_rate:
                        Intent intent7 = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.gaiadergi"));
                        startActivity(intent7);
                        return true;
                    case R.id.nav_support:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Gaia Dergi | Sürdürülebilir Yaşam Dergisi https://play.google.com/store/apps/details?id=com.gaiadergi");
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "Uygulamayı paylaş"));
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
                        return true;
                }
            }
        });

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String link = intent.getDataString();
            if (link != null) {
                if (link.equals("https://gaiadergi.com/")) {
                    Fragment fragment = new FragmentHome();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Home").commit();
                    getSupportActionBar().setTitle(R.string.nav_text_home);
                    MenuItem item = navigationView.getMenu().getItem(0);
                    item.setChecked(true);
                } else if (link.contains("kategori/yesil")) {
                    Fragment fragment = new FragmentGreen();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Green").commit();
                    getSupportActionBar().setTitle(R.string.nav_text_green);
                    MenuItem item = navigationView.getMenu().getItem(1);
                    item.setChecked(true);
                } else if (link.contains("kategori/yasam")) {
                    Fragment fragment = new FragmentLife();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Life").commit();
                    getSupportActionBar().setTitle(R.string.nav_text_life);
                    MenuItem item = navigationView.getMenu().getItem(2);
                    item.setChecked(true);
                } else if (link.contains("kategori/ekoloji")) {
                    Fragment fragment = new FragmentEcology();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Ecology").commit();
                    getSupportActionBar().setTitle(R.string.nav_text_ecology);
                    MenuItem item = navigationView.getMenu().getItem(3);
                    item.setChecked(true);
                } else if (link.contains("kategori/kultursanat")) {
                    Fragment fragment = new FragmentCulture();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Culture").commit();
                    getSupportActionBar().setTitle(R.string.nav_text_culture);
                    MenuItem item = navigationView.getMenu().getItem(4);
                    item.setChecked(true);
                } else if (link.contains("kategori/bilimteknoloji")) {
                    Fragment fragment = new FragmentScience();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Science").commit();
                    getSupportActionBar().setTitle(R.string.nav_text_science);
                    MenuItem item = navigationView.getMenu().getItem(5);
                    item.setChecked(true);
                } else if (link.contains("kategori/guncel")) {
                    Fragment fragment = new FragmentTurkey();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Turkey").commit();
                    getSupportActionBar().setTitle(R.string.nav_text_turkey);
                    MenuItem item = navigationView.getMenu().getItem(6);
                    item.setChecked(true);
                } else if (link.contains("kategori/duyurular-etkinlikler")) {
                    Fragment fragment = new FragmentEvents();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Events").commit();
                    getSupportActionBar().setTitle(R.string.nav_text_events);
                    MenuItem item = navigationView.getMenu().getItem(7);
                    item.setChecked(true);
                } else if (link.contains("kategori/insan-ve-toplum")) {
                    Fragment fragment = new FragmentSociety();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Society").commit();
                    getSupportActionBar().setTitle(R.string.nav_text_society);
                    MenuItem item = navigationView.getMenu().getItem(8);
                    item.setChecked(true);
                } else if (link.contains("/magaza")) {
                    Intent intent2 = new Intent(MainActivity.this, ActivityStore.class);
                    startActivity(intent2);
                } else {
                    Fragment fragment = new FragmentHome();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Home").commit();
                    getSupportActionBar().setTitle(R.string.nav_text_home);
                    MenuItem item = navigationView.getMenu().getItem(0);
                    item.setChecked(true);

                    Intent intent3 = new Intent(MainActivity.this, ActivityContent.class);
                    intent3.putExtra("link", link);
                    startActivity(intent3);
                }
            } else {
                Fragment fragment = new FragmentHome();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Home").commit();
                getSupportActionBar().setTitle(R.string.nav_text_home);
                MenuItem item = navigationView.getMenu().getItem(0);
                item.setChecked(true);
            }
        }

        // AppRater
        RateThisApp.onCreate(this);
        RateThisApp.showRateDialogIfNeeded(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(MainActivity.this, ActivitySearch.class);
                startActivity(intent);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            navigationView.setCheckedItem(R.id.nav_home);
            // FragmentHome
            FragmentHome fragment0 = (FragmentHome) getSupportFragmentManager().findFragmentByTag("Home");
            // FragmentHome OnBackPressed
            if (fragment0 != null) {
                if (fragment0.isVisible()) {
                    if (doubleBackToExitPressedOnce) {
                        super.onBackPressed();
                        return;
                    }
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, getString(R.string.exit), Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
            } else {
                Fragment fragment = new FragmentHome();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Home").commit();
                toolbar.setTitle(R.string.nav_text_home);
                MenuItem item = navigationView.getMenu().getItem(0);
                item.setChecked(true);
            }
        }
    }
}