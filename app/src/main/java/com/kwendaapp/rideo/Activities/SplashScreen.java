package com.kwendaapp.rideo.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kwendaapp.rideo.Helper.ConnectionHelper;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Helper.URLHelper;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.UserApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SplashScreen extends AppCompatActivity {
    private final String TAG = "SplashActivity";
    public Activity activity = SplashScreen.this;
    public Context context = SplashScreen.this;
    private final ConnectionHelper helper = new ConnectionHelper(context);
    private Boolean isInternet;
    private String device_token, device_UDID;
    private String lat, lng, query;
    private Handler handleCheckStatus;
    private int retryCount = 0;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            String protocol = uri.getScheme();
            String arg = uri.getSchemeSpecificPart();
            if (protocol.equals("geo") && arg != null) {
                String[] split = arg.split("\\?");
                String[] latlng = split[0].split(",");
                if (latlng.length == 2) {
                    lat = latlng[0];
                    lng = latlng[1];
                }
                if (split.length == 2) {
                    query = split[1].replace("q=", "");
                }
            }
        }
        isInternet = helper.isConnectingToInternet();
        handleCheckStatus = new Handler();
        changeStatusBarColor();
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isInternet) {
                    if (helper.isConnectingToInternet()) {
                        if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase(context.getResources().getString(R.string.True))) {
                            GetToken();
                            getProfile();
                        } else {
                            GoToBeginActivity();
                            handleCheckStatus.removeCallbacksAndMessages(null);
                        }
                        if (alert != null && alert.isShowing()) {
                            alert.dismiss();
                        }
                    } else {
                        showDialog();
                        handleCheckStatus.postDelayed(this, 1000);
                    }
                } else {
                    handleCheckStatus.postDelayed(this, 1000);
                    displayMessage(getResources().getString(R.string.oops_connect_your_internet));
                }
            }
        }, 1000);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setLanguage();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = SplashScreen.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.setStatusBarColor(getResources().getColor(R.color.orange));
        }
    }

    private void setLanguage() {
        String languageCode = SharedHelper.getKey(SplashScreen.this, "language");
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
//        LocaleUtils.setLocale(this, languageCode);
    }

    public void getProfile() {
        retryCount++;
        Log.e("GetPostAPI", "" + URLHelper.UserProfile + "?device_type=android&device_id=" + device_UDID + "&device_token=" + device_token);
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.UserProfile + "" +
                "?device_type=android&device_id=" + device_UDID + "&device_token=" + device_token, object, response -> {

            SharedHelper.putKey(context, "id", response.optString("id"));
            SharedHelper.putKey(context, "first_name", response.optString("first_name"));
            SharedHelper.putKey(context, "last_name", response.optString("last_name"));
            SharedHelper.putKey(context, "email", response.optString("email"));
            SharedHelper.putKey(context, "created_at", response.optString("created_at"));
            if (response.optString("picture").startsWith("http"))
                SharedHelper.putKey(context, "picture", response.optString("picture"));
            else
                SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("picture"));
            SharedHelper.putKey(context, "gender", response.optString("gender"));
            SharedHelper.putKey(context, "mobile", response.optString("mobile"));
            SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
            SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
            if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                SharedHelper.putKey(context, "currency", response.optString("currency"));
            else
                SharedHelper.putKey(context, "currency", "$");
            SharedHelper.putKey(context, "sos", response.optString("sos"));
            Log.e(TAG, "onResponse: Sos Call" + response.optString("sos"));
            SharedHelper.putKey(context, "loggedIn", context.getResources().getString(R.string.True));
            Log.e(TAG, "current_city: " + SharedHelper.getKey(context, "current_city"));
//            SharedHelper.putKey(context, "current_city", "");
            if (SharedHelper.getKey(context, "current_city").equals(""))
                GoToCityActivity();
            else
                GoToMainActivity();
        }, error -> {
            if (retryCount < 5) {
                getProfile();
            } else {
                GoToBeginActivity();
            }
            String json;
            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {

                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));

                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            displayMessage(errorObj.optString("message"));
                        } catch (Exception e) {
                            displayMessage(context.getResources().getString(R.string.something_went_wrong));
                        }
                    } else if (response.statusCode == 401) {
                        refreshAccessToken();
                    } else if (response.statusCode == 422) {

                        json = UserApplication.trimMessage(new String(response.data));
                        if (!json.equals("") && json != null) {
                            displayMessage(json);
                        } else {
                            displayMessage(context.getResources().getString(R.string.please_try_again));
                        }

                    } else if (response.statusCode == 503) {
                        displayMessage(context.getResources().getString(R.string.server_down));
                    }
                } catch (Exception e) {
                    displayMessage(context.getResources().getString(R.string.something_went_wrong));
                }

            } else {
                if (error instanceof NoConnectionError) {
                    displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                } else if (error instanceof NetworkError) {
                    displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                } else if (error instanceof TimeoutError) {
                    getProfile();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    @Override
    protected void onDestroy() {
        handleCheckStatus.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void refreshAccessToken() {
        JSONObject object = new JSONObject();
        try {
            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, response -> {
            Log.v("SignUpResponse", response.toString());
            SharedHelper.putKey(context, "access_token", response.optString("access_token"));
            SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
            SharedHelper.putKey(context, "token_type", response.optString("token_type"));
            getProfile();
        }, error -> {
            NetworkResponse response = error.networkResponse;

            if (response != null && response.data != null) {
                SharedHelper.putKey(context, "loggedIn", context.getResources().getString(R.string.False));
                GoToBeginActivity();
            } else {
                if (error instanceof NoConnectionError) {
                    displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                } else if (error instanceof NetworkError) {
                    displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                } else if (error instanceof TimeoutError) {
                    refreshAccessToken();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (lat != null && lng != null) {
            mainIntent.setAction("GetServices");
            mainIntent.putExtra("lat", lat);
            mainIntent.putExtra("lng", lng);
            mainIntent.putExtra("query", query);
        }
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToCityActivity() {
        Intent mainIntent = new Intent(activity, SelectCityActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.putExtra("tag", true);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Toast.makeText(activity, toastString, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("HardwareIds")
    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.i(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseMessaging.getInstance().getToken();
                SharedHelper.putKey(context, "device_token", "" + FirebaseMessaging.getInstance().getToken());
                Log.i(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.i(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(context.getResources().getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.connect_to_wifi), (dialog, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton(context.getResources().getString(R.string.quit), (dialog, id) -> finish());
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

}
