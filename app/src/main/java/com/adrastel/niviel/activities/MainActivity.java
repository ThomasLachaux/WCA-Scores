package com.adrastel.niviel.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.adrastel.niviel.BuildConfig;
import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.fragments.CompetitionFragment;
import com.adrastel.niviel.fragments.FollowerFragment;
import com.adrastel.niviel.fragments.ProfileFragment;
import com.adrastel.niviel.fragments.RankingFragment;
import com.adrastel.niviel.services.EditRecordService;
import com.kobakei.ratethisapp.RateThisApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.cketti.mailto.EmailIntentBuilder;

public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener {

    public static final int RESTART_ACTIVITY = 25;
    public static final String FRAGMENT = "fragment";

    public @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.navigation_view) NavigationView navigationView;
    @BindView(R.id.tab_layout) TabLayout tabLayout;


    private FragmentManager fragmentManager;
    private BaseFragment fragment;
    private SharedPreferences preferences;

    private long prefId = -1;
    private boolean isDark = false;

    // This runnable is executed as soon as the drawer is closed
    private Runnable fragmentToRun;


    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getIntExtra(EditRecordService.ACTION, EditRecordService.ADD_RECORD_FAILURE)) {

                case EditRecordService.ADD_RECORD_SUCCESS:
                    updateUiOnProfileChange();
                    break;

                case EditRecordService.ADD_RECORD_FAILURE:
                    updateUiOnProfileChange();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fragmentManager = getSupportFragmentManager();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // If the intro is lauched, stop executing the following lines
        if(initIntro()) return;

        // Init the theme
        isDark = Assets.isDark(preferences.getString(getString(R.string.pref_isdark), "0"));
        setDayNightTheme(isDark);

        // Init the UI according to the WCA profile
        updateUiOnProfileChange();

        /*
        If there is a fragment already stored, executes it
        Otherwise, launches the ProfileFragment
         */
        if (this.fragment == null) {
            if (savedInstanceState != null && fragmentManager.getFragment(savedInstanceState, FRAGMENT) != null) {
                this.fragment = (BaseFragment) fragmentManager.getFragment(savedInstanceState, FRAGMENT);
            } else {
                this.fragment = ProfileFragment.newInstance(prefId);
            }
        }

        switchFragment(fragment);

        // When the user clicks on an item
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initRateApp();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        try {
            if (fragment != null) {
                fragmentManager.putFragment(outState, FRAGMENT, fragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;

            case R.id.search:
                gotoSearch();
                return true;

            case R.id.settings:
                gotoSettings();
                return true;

            case R.id.dark_light:
                switchTheme();
                return true;

            case R.id.contact:
                contactDev();
                return true;

            case R.id.share:
                shareApp();
                return true;

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Has to restart
        if (resultCode == RESTART_ACTIVITY) {
            restart();
        }

        // Has to go to the selected profile
        else if(resultCode == SearchActivity.SEARCH_SUCCESS) {


            final String name = data.getStringExtra(SearchActivity.NAME);
            final String wca_id = data.getStringExtra(SearchActivity.WCA_ID);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    searchUser(wca_id, name);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        if (isDrawerOpen()) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        drawerLayout.removeDrawerListener(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activityReceiver);

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawerLayout.addDrawerListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(activityReceiver, new IntentFilter(EditRecordService.INTENT_FILTER));

    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    /**
     * Executes the runnable which should have been set before
     *
     * @param drawerView drawer
     */
    @Override
    public void onDrawerClosed(View drawerView) {
        if (fragmentToRun != null) {
            fragmentToRun.run();
            fragmentToRun = null;
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    /**
     * Called when the user selects an item in the NavigationView
     * In most of the case, executes a fragment
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        closeDrawer();

        final BaseFragment fragment = selectDrawerItem(item);

        if (fragment != null) {

            runWhenDrawerClose(new Runnable() {
                @Override
                public void run() {
                    switchFragment(fragment);
                }
            });
        }

        return true;
    }

    /**
     * Checks if it is necessary to launch the intro
     * @return true if the intro is launched
     */
    private boolean initIntro() {
        if(preferences.getBoolean(getString(R.string.pref_first_launch), true)) {

            preferences
                    .edit()
                    .putBoolean(getString(R.string.pref_first_launch), false)
                    .putLong(getString(R.string.pref_time_first_launch), System.currentTimeMillis())
                    .apply();

            startActivityForResult(new Intent(this, MainIntroActivity.class), 0);
            return true;
        }

        return false;
    }

    public void rateApp() {
        Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName());
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public void gotoSearch() {
        Intent search = new Intent(this, SearchActivity.class);
        startActivityForResult(search, 0);
    }

    public void gotoSettings() {
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivityForResult(settings, 0);
    }

    public void restart() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void switchTheme() {
        isDark = !isDark;
        preferences
                .edit()
                .putString(getString(R.string.pref_isdark), Assets.dayNightBooleanToString(isDark))
                .apply();

        restart();
    }

    public void gotoGithub() {
        Uri uri = Uri.parse("https://github.com/HerelDev/Niviel");
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public void contactDev() {
        EmailIntentBuilder
                .from(this)
                .to(getString(R.string.email))
                .subject(getString(R.string.mail_subject))
                .body(getString(R.string.mail_body, Build.MODEL, Build.VERSION.RELEASE, Build.VERSION.SDK_INT, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE))
                .start();
    }

    /**
     * Choisit le fragment à prendre
     *
     * @param item item
     */
    @Nullable
    private BaseFragment selectDrawerItem(MenuItem item) {
        tabLayout.setVisibility(View.GONE);

        switch (item.getItemId()) {
            case R.id.profile:
                return ProfileFragment.newInstance(prefId);

            case R.id.ranking:
                return RankingFragment.newInstance();

            case R.id.competitions:
                return CompetitionFragment.newInstance();

            case R.id.explore:
                return ProfileFragment.newInstance(null, null);

            case R.id.followers:
                return new FollowerFragment();

            case R.id.settings:

                runWhenDrawerClose(new Runnable() {
                    @Override
                    public void run() {
                        gotoSettings();
                    }
                });
                return null;

            case R.id.rate:

                runWhenDrawerClose(new Runnable() {
                    @Override
                    public void run() {
                        rateApp();
                    }
                });
                return null;

            case R.id.contribute:

                runWhenDrawerClose(new Runnable() {
                    @Override
                    public void run() {
                        gotoGithub();
                    }
                });

                return null;

            default:
                return ProfileFragment.newInstance(prefId);

        }
    }

    public void showFab() {
        fab.show();
    }

    public void hideFab() {
        fab.hide();
    }

    public void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message));

        startActivity(Intent.createChooser(intent, getString(R.string.share_app)));
    }

    public void switchFragment(BaseFragment fragment) {

        fragment.getStyle();

        fragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();

        updateUiOnFragmentChange(fragment);
        this.fragment = fragment;
    }

    private void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }
    }

    private boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void setToolbarColor(int color) {
        if (toolbar != null && color != 0) {
            toolbar.setBackgroundColor(color);
        }
    }

    private void setStatusBar(int color) {
        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && color != 0) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }

    }

    public void setSubtitle(String subtitle) {

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

    /**
     * Update the UI when there is new profile
     */
    private void updateUiOnProfileChange() {
        prefId = preferences.getLong(getString(R.string.pref_personal_id), -1);
        View headerView = navigationView.getHeaderView(0);

        TextView nameView = (TextView) headerView.findViewById(R.id.name);
        TextView wca_idView = (TextView) headerView.findViewById(R.id.wca_id);

        MenuItem profileItem = navigationView.getMenu().findItem(R.id.profile);

        if (prefId != -1) {

            try {
                DatabaseHelper database = DatabaseHelper.getInstance(this);
                Follower follower = database.selectFollowerFromId(prefId);

                String prefWcaId = follower.wca_id();
                String prefWcaName = follower.name();

                profileItem.setVisible(true);

                nameView.setText(prefWcaName);
                wca_idView.setText(prefWcaId);

            } catch (Exception e) {
                e.printStackTrace();
                removePersonalId();
            }
        } else {
            profileItem.setVisible(false);

            nameView.setText(R.string.app_name);
            wca_idView.setText("");
        }


    }

    public void removePersonalId() {
        preferences
                .edit()
                .putLong(getString(R.string.pref_personal_id), -1)
                .apply();
    }

    /**
     * Update the UI when the fragment is changed
     *
     * @param fragment BaseFragement
     */
    @SuppressWarnings("ResourceType")
    private void updateUiOnFragmentChange(BaseFragment fragment) {

        setSubtitle(null);

        int style = fragment.getStyle();

        int[] attrs = {R.attr.colorPrimary, R.attr.colorPrimaryDark, R.attr.toolbarTitle, R.attr.fabVisible, R.attr.fabIcon};

        TypedArray typedArray = obtainStyledAttributes(style, attrs);


        String title = typedArray.getString(2);
        setTitle(title);

        int primaryColor = typedArray.getColor(0, Color.WHITE);
        setToolbarColor(primaryColor);

        int primaryColorDark = typedArray.getColor(1, Color.WHITE);
        setStatusBar(primaryColorDark);


        boolean fabVisible = typedArray.getBoolean(3, false);

        if (fabVisible) {
            showFab();
        } else {
            hideFab();
        }

        Drawable fabIcon = typedArray.getDrawable(4);
        fab.setImageDrawable(fabIcon);

        typedArray.recycle();
    }

    /**
     * Starts a profile fragment
     */
    private void searchUser(String wca_id, String name) {

        ProfileFragment profileFragment = ProfileFragment.newInstance(wca_id, name);
        switchFragment(profileFragment);

    }

    /**
     * Sets the parameter as the global runnable. This runnable is executed few seconds later
     */
    private void runWhenDrawerClose(Runnable runnable) {
        this.fragmentToRun = runnable;
    }

    /**
     * Decides or no to show a rate dialog
     */
    private void initRateApp() {
        RateThisApp.onStart(this);

        RateThisApp.Config config = new RateThisApp.Config();
        config.setTitle(R.string.rate_share_app);
        config.setMessage(R.string.rate_app_msg);
        config.setYesButtonText(R.string.rate);
        config.setNoButtonText(R.string.no_thanks);
        // On Share
        config.setCancelButtonText(R.string.share);

        RateThisApp.init(config);
        RateThisApp.showRateDialogIfNeeded(this);
        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {

            }

            @Override
            public void onNoClicked() {

            }

            // On share
            @Override
            public void onCancelClicked() {
                shareApp();
                RateThisApp.stopRateDialog(MainActivity.this);
            }
        });
    }
}