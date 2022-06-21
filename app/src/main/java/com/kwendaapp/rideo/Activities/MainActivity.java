package com.kwendaapp.rideo.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kwendaapp.rideo.Helper.AutoStartHelper;
import com.squareup.picasso.Picasso;
import com.kwendaapp.rideo.API.RetrofitClient;
import com.kwendaapp.rideo.FCM.ForceUpdateChecker;
import com.kwendaapp.rideo.Fragments.HomeFragment;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Models.ConstData;
import com.kwendaapp.rideo.Models.ConstDataResponse;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.Retrofit.ResponseListener;
import com.kwendaapp.rideo.Utils.CustomTypefaceSpan;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements HomeFragment.HomeFragmentListener,
        ForceUpdateChecker.OnUpdateNeededListener, ResponseListener {


    private static final String TAG_HOME = "home";
    public Context context = MainActivity.this;
    public Activity activity = MainActivity.this;
    public int navItemIndex = 0;
    public String CURRENT_TAG = TAG_HOME;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ImageView imgProfile;
    private TextView txtName;
    public String[] activityTitles;
    CustomDialog customDialog;
    String[] key = new String[1];
    final String[][] value = new String[1][1];
    public BroadcastReceiver mRegistrationBroadcastReceiver;
    private Handler mHandler;
    private String notificationMsg;
    public static int UPDATE_CITY = 1010;
    private String lat;
    private String lng;
    private String query;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        String autoStart = sharedpreferences.getString("autoStart", "");
        if (autoStart.equals("")) {
            AutoStartHelper.getInstance().getAutoStartPermission(this);
        }
        FirebaseMessaging.getInstance().subscribeToTopic("user");
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        Intent intent = getIntent();
        if (savedInstanceState == null) {
            if (intent != null && intent.getExtras() != null) {
                if (intent.getAction().equals("GetServices")) {
                    intent.setAction(null);
                    lat = intent.getStringExtra("lat");
                    lng = intent.getStringExtra("lng");
                    query = intent.getStringExtra("query");
                }
            }
        }
        if (intent != null && intent.getExtras() != null)
            notificationMsg = intent.getExtras().getString("Notification");
        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        // Navigation view header
        View navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.usernameTxt);
        imgProfile = navHeader.findViewById(R.id.img_profile);
        TextView drive_link = navigationView.findViewById(R.id.drive_link);

        drive_link.setOnClickListener(v -> {
            String url = SharedHelper.getKey(context, "f_p_url");
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        navHeader.setOnClickListener(view -> {
            startActivity(new Intent(activity, Profile.class));
            drawer.closeDrawers();
        });

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        getconstdata();
        setLanguage();

    }


    private void loadNavHeader() {
        // name, website
        txtName.setText(SharedHelper.getKey(context, "first_name"));


        if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase("") && SharedHelper.getKey(context, "picture") != null) {
            Picasso.get().load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(imgProfile);
        } else {
            Picasso.get().load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(imgProfile);
        }


    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {

        SharedHelper.putKey(context, "current_status", "");
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button
            return;
        }

        Runnable mPendingRunnable = () -> {
            // update the main content by replacing fragments
            Fragment fragment = getHomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        };

        // If mPendingRunnable is not null, then add to the message queue
        mHandler.post(mPendingRunnable);


        //Closing drawer on item click
        drawer.closeDrawers();
        // refresh toolbar menu
        invalidateOptionsMenu();

    }

    private Fragment getHomeFragment() {

        if (navItemIndex == 0) {// home
            HomeFragment homeFragment = HomeFragment.newInstance();
            Bundle bundle = new Bundle();
            if (lat != null && lng != null) {
                bundle.putString("lat", lat);
                bundle.putString("lng", lng);
                bundle.putString("query", query);
            }
            bundle.putString("Notification", notificationMsg);
            homeFragment.setArguments(bundle);
            return homeFragment;
        }
        return new HomeFragment();

    }


    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {

            //Check to see which item was being clicked and perform appropriate action
            switch (menuItem.getItemId()) {

                case R.id.nav_yourtrips:
                    drawer.closeDrawers();
                  /*  navItemIndex = 2;
                    CURRENT_TAG = TAG_YOURTRIPS;*/
                    SharedHelper.putKey(context, "current_status", "");
                    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                    intent.putExtra("tag", "past");
                    startActivity(intent);
                    return true;
                case R.id.nav_upcoming:
                    drawer.closeDrawers();
                    SharedHelper.putKey(context, "current_status", "");
                    Intent intent2 = new Intent(MainActivity.this, OnGoingActivity.class);
                    startActivity(intent2);
                    return true;
                case R.id.settings:
                    drawer.closeDrawers();
                    SharedHelper.putKey(context, "current_status", "");
                    startActivity(new Intent(MainActivity.this, ActivitySettings.class));
                    return true;
                case R.id.nav_wallet:
                    drawer.closeDrawers();
                    SharedHelper.putKey(context, "current_status", "");
                    startActivity(new Intent(MainActivity.this, ActivityWallet.class));
                    return true;
                case R.id.nav_history:
                    drawer.closeDrawers();
                    SharedHelper.putKey(context, "current_status", "");
                    Intent intent1 = new Intent(MainActivity.this, WalletAndCouponHistory.class);
                    intent1.putExtra("tag", "past");
                    startActivity(intent1);
                    return true;
                case R.id.nav_City:
                    drawer.closeDrawers();
                    GoToCityActivity();
                    return true;
                case R.id.nav_payments:
                    drawer.closeDrawers();
                    SharedHelper.putKey(context, "current_status", "");
                    startActivity(new Intent(MainActivity.this, Payment.class));
                    return true;
                case R.id.nav_help:
                    drawer.closeDrawers();
                    SharedHelper.putKey(context, "current_status", "");
                    startActivity(new Intent(MainActivity.this, ActivityHelp.class));
                    break;
                case R.id.nav_home:
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                    drawer.closeDrawers();
                    return true;
                default:
                    navItemIndex = 0;
            }
            loadHomeFragment();

            return true;
        });

        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            MenuItem menuItem = m.getItem(i);
            applyFontToMenuItem(menuItem);

        }


    }

    public void refreshView() {
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        finish();
    }

    private void setLanguage() {
        String languageCode = SharedHelper.getKey(MainActivity.this, "language");
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
//        LocaleUtils.setLocale(this, languageCode);
    }

    public void AppMaintainDialogShow() {
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.app_maintain_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.closeOptionsMenu();
        dialog.setCancelable(false);
        dialog.show();

        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                onBackPressed();
            }
        };
    }


    public void getconstdata() {
        customDialog = new CustomDialog(this);
//        customDialog.show();
        Call<ConstDataResponse> call = RetrofitClient.getInstance().getApi().getconstdata();
        call.enqueue(new Callback<ConstDataResponse>() {
            @Override
            public void onResponse(@NotNull Call<ConstDataResponse> call, @NotNull Response<ConstDataResponse> response) {
                if (response.isSuccessful()) {
                    List<ConstData> constData;
                    ConstDataResponse constDataResponse = response.body();
                    assert constDataResponse != null;
                    constData = constDataResponse.getData();

                    key = new String[constData.size()];
                    value[0] = new String[constData.size()];

                    for (int i = 0; i < constData.size(); i++) {
                        key[i] = constData.get(i).getKey();
                        value[0][i] = constData.get(i).getValue();

                        SharedHelper.putKey(context, "appmaintain", "0");

                        if (key[i].equals("appmaintain"))
                            SharedHelper.putKey(context, "appmaintain", value[0][i]);
                        if (SharedHelper.getKey(context, "appmaintain").equals("1")) {
                            AppMaintainDialogShow();
                            return;
                        }
                        if (key[i].equals("page_privacy"))
                            SharedHelper.putKey(context, "privacy_page", value[0][i]);

                        if (key[i].equals("f_p_url"))
                            SharedHelper.putKey(context, "f_p_url", value[0][i]);

                        if (key[i].equals("tawk_live"))
                            SharedHelper.putKey(context, "tawk_live", value[0][i]);

                        if (key[i].equals("page_terms"))
                            SharedHelper.putKey(context, "terms_page", value[0][i]);

                        if (key[i].equals("CARD"))
                            SharedHelper.putKey(context, "CARD", value[0][i]);

                        if (key[i].equals("paypal"))
                            SharedHelper.putKey(context, "paypal", value[0][i]);

                        if (key[i].equals("rave"))
                            SharedHelper.putKey(context, "rave", value[0][i]);

                        if (key[i].equals("UPI"))
                            SharedHelper.putKey(context, "UPI", value[0][i]);

                        if (key[i].equals("razor"))
                            SharedHelper.putKey(context, "razor", value[0][i]);

                        if (key[i].equals("stripe_publishable_key"))
                            SharedHelper.putKey(context, "stripe_publishable_key", value[0][i]);

                        if (key[i].equals("paypal_client_id"))
                            SharedHelper.putKey(context, "paypal_client_id", value[0][i]);

                        if (key[i].equals("rave_publicKey"))
                            SharedHelper.putKey(context, "rave_publicKey", value[0][i]);

                        if (key[i].equals("rave_encryptionKey"))
                            SharedHelper.putKey(context, "rave_encryptionKey", value[0][i]);

                        if (key[i].equals("rave_country"))
                            SharedHelper.putKey(context, "rave_country", value[0][i]);

                        if (key[i].equals("rave_currency"))
                            SharedHelper.putKey(context, "rave_currency", value[0][i]);

                        if (key[i].equals("UPI_key"))
                            SharedHelper.putKey(context, "UPI_key", value[0][i]);

                        if (key[i].equals("razor_key"))
                            SharedHelper.putKey(context, "razor_key", value[0][i]);

                        if (key[i].equals("cat_app_ecomony"))
                            SharedHelper.putKey(context, "cat_app_ecomony", value[0][i]);

                        if (key[i].equals("cat_app_lux"))
                            SharedHelper.putKey(context, "cat_app_lux", value[0][i]);

                        if (key[i].equals("cat_app_ext"))
                            SharedHelper.putKey(context, "cat_app_ext", value[0][i]);

                        if (key[i].equals("cat_app_out"))
                            SharedHelper.putKey(context, "cat_app_out", value[0][i]);

                    }
                    if (customDialog.isShowing())
                        customDialog.dismiss();
                } else {
                    if (customDialog.isShowing())
                        customDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Some Error Occurred ! Please Try Again Later", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NotNull Call<ConstDataResponse> call, @NotNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(MainActivity.this, "Some Error Occurred ! Please Try Again Later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToCityActivity() {
        Intent mainIntent = new Intent(MainActivity.this, SelectCityActivity.class);
        mainIntent.putExtra("tag", false);
        startActivityForResult(mainIntent, UPDATE_CITY);
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        if (navItemIndex != 0) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            SharedHelper.putKey(context, "current_status", "");
            loadHomeFragment();
            return;
        } else {
            SharedHelper.putKey(context, "current_status", "");
        }
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("home");
        if (fragment != null) {
            if (fragment instanceof HomeFragment) {
                if (((HomeFragment) fragment).onBackPressed())
                    super.onBackPressed();
                else
                    return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notification, menu);
        }
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
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void getJSONArrayResult(String strTag, JSONArray arrayResponse) {

    }


    @Override
    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.new_version_available))
                .setMessage(getResources().getString(R.string.update_to_continue))
                .setPositiveButton(getResources().getString(R.string.update),
                        (dialog12, which) -> redirectStore(updateUrl)).setNegativeButton(getResources().getString(R.string.no_thanks),
                        (dialog1, which) -> finish()).create();
        dialog.show();
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MainActivity.UPDATE_CITY) {
            refreshView();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
}