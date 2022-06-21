package com.kwendaapp.rideo.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.accountkit.AccountKit;
import com.facebook.login.LoginManager;
import com.hbb20.CountryCodePicker;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.LocaleUtils;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Helper.URLHelper;
import com.kwendaapp.rideo.Models.Errorresponse;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.Retrofit.ApiInterface;
import com.kwendaapp.rideo.Retrofit.RetrofitClient;
import com.kwendaapp.rideo.UserApplication;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.kwendaapp.rideo.Adapter.LanguageAdapter;
import com.kwendaapp.rideo.Adapter.LanguageData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;
import static com.google.android.material.snackbar.Snackbar.make;


public class ActivitySettings extends AppCompatActivity {

    private RadioButton radioEnglish, radioArabic;
    private final int UPDATE_HOME_WORK = 1;
    private ApiInterface mApiInterface;
    private TextView txtHomeLocation;
    private TextView txtWorkLocation;
    private CustomDialog customDialog;
    private TextView sosnum;
    private Dialog myDialog;
    private Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        myDialog = new Dialog(this);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {

        radioEnglish = findViewById(R.id.radioEnglish);
        radioArabic = findViewById(R.id.radioArabic);

        LinearLayout lnrEnglish = findViewById(R.id.lnrEnglish);
        LinearLayout lnrArabic = findViewById(R.id.lnrArabic);
        LinearLayout lnrHome = findViewById(R.id.lnrHome);
        LinearLayout lnrWork = findViewById(R.id.lnrWork);

        txtHomeLocation = findViewById(R.id.txtHomeLocation);
        txtWorkLocation = findViewById(R.id.txtWorkLocation);
        TextView txtDeleteWork = findViewById(R.id.txtDeleteWork);
        TextView txtDeleteHome = findViewById(R.id.txtDeleteHome);
        sosnum = findViewById(R.id.SOSNumText);
        LinearLayout soslayout = findViewById(R.id.SOS_num_layout);
        LinearLayout changelang = findViewById(R.id.changelanguage);
        LinearLayout shareapp = findViewById(R.id.shareapp);
        LinearLayout logout = findViewById(R.id.logout);
        LinearLayout privacy = findViewById(R.id.privacy);
        LinearLayout terms = findViewById(R.id.terms);

        String sosnumtext = SharedHelper.getKey(this, "sosnum");
        if (sosnumtext != null && !sosnumtext.equals("")) {
            sosnum.setText(sosnumtext);
        } else {
            sosnum.setText("SOS Number Not Available");
        }

        soslayout.setOnClickListener(v -> ShowPopup());


        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(view -> onBackPressed());

        customDialog = new CustomDialog(ActivitySettings.this);
        customDialog.show();

        getFavoriteLocations();

        lnrHome.setOnClickListener(view -> {
            if (SharedHelper.getKey(ActivitySettings.this, "home").equalsIgnoreCase("")) {
                gotoHomeWork("home");
            } else {
                deleteFavoriteLocations("home");
            }
        });

        lnrWork.setOnClickListener(view -> {
            if (SharedHelper.getKey(ActivitySettings.this, "work").equalsIgnoreCase("")) {
                gotoHomeWork("work");
            } else {
                deleteFavoriteLocations("work");
            }
        });

        txtDeleteHome.setOnClickListener(view -> deleteFavoriteLocations("home"));

        txtDeleteWork.setOnClickListener(view -> deleteFavoriteLocations("work"));


        if (SharedHelper.getKey(ActivitySettings.this, "language").equalsIgnoreCase("en")) {
            radioEnglish.setChecked(true);
        } else {
            radioArabic.setChecked(true);
        }

        lnrEnglish.setOnClickListener(view -> {
            radioArabic.setChecked(false);
            radioEnglish.setChecked(true);
        });

        lnrArabic.setOnClickListener(view -> {
            radioEnglish.setChecked(false);
            radioArabic.setChecked(true);
        });

        radioArabic.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                radioEnglish.setChecked(false);
                SharedHelper.putKey(ActivitySettings.this, "language", "ar");
                setLanguage();
                recreate();
            }
        });

        radioEnglish.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                radioArabic.setChecked(false);
                SharedHelper.putKey(ActivitySettings.this, "language", "en");
                setLanguage();
                recreate();
            }
        });

        changelang.setOnClickListener(v -> language_alert_view());

        shareapp.setOnClickListener(v -> navigateToShareScreen(URLHelper.APP_URL + getPackageName()));

        logout.setOnClickListener(v -> showLogoutDialog());

        privacy.setOnClickListener(v -> {
            Intent intent = new Intent(ActivitySettings.this, custom_webview.class);
            intent.putExtra("page", "privacy_page");
            intent.putExtra("page_name", "Privacy Policy");
            startActivity(intent);
        });

        terms.setOnClickListener(v -> {
            Intent intent = new Intent(ActivitySettings.this, custom_webview.class);
            intent.putExtra("page", "terms_page");
            intent.putExtra("page_name", "Terms & Conditions");
            startActivity(intent);
        });
    }

    public void navigateToShareScreen(String shareUrl) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl + " -via " + getString(R.string.app_name));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void ShowPopup() {
        myDialog.setContentView(R.layout.add_sos_num);
        Button submit = myDialog.findViewById(R.id.sos_btn);
        EditText phone = myDialog.findViewById(R.id.sos_phone);
        CountryCodePicker sos_cpp = myDialog.findViewById(R.id.sos_cpp);
        submit.setOnClickListener(v -> {
            if (!phone.getText().toString().equals("")) {
                SharedHelper.putKey(ActivitySettings.this, "sosnum", sos_cpp.getSelectedCountryCodeWithPlus() + phone.getText().toString().trim());
                sosnum.setText(phone.getText().toString().trim());
                myDialog.dismiss();
            } else {
                phone.setError("Please Enter The SOS Number");
            }
        });

        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.closeOptionsMenu();
        myDialog.show();
    }

    private void gotoHomeWork(String strTag) {
        Intent intentHomeWork = new Intent(ActivitySettings.this, AddHomeWorkActivity.class);
        intentHomeWork.putExtra("tag", strTag);
        startActivityForResult(intentHomeWork, UPDATE_HOME_WORK);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    private void setLanguage() {
        String languageCode = SharedHelper.getKey(ActivitySettings.this, "language");
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
//        LocaleUtils.setLocale(this, languageCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_HOME_WORK) {
            if (resultCode == Activity.RESULT_OK) {
                getFavoriteLocations();
            }
        }
    }

    public void language_alert_view() {
        final ArrayList<LanguageData> languageDataArrayList = new ArrayList<>();
        if (alertDialog != null)
            if (alertDialog.isShowing())
                alertDialog.dismiss();
        final View view = View.inflate(ActivitySettings.this, R.layout.language_lay, null);

        alertDialog = new Dialog(ActivitySettings.this, R.style.dialogwinddow);
        alertDialog.setContentView(view);
        alertDialog.setCancelable(true);
        alertDialog.show();

        RecyclerView langList = alertDialog.findViewById(R.id.langList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivitySettings.this, LinearLayoutManager.VERTICAL, false);
        langList.setHasFixedSize(true);
        langList.setLayoutManager(linearLayoutManager);
        try {
            for (int i = 0; i < getResources().getStringArray(R.array.languagelist).length; i++) {

                String lang_name = (getResources().getStringArray(R.array.languagelist))[i];
                String lang_code = (getResources().getStringArray(R.array.languagecode))[i];
                String lang_country_code = (getResources().getStringArray(R.array.languagecountrycode))[i];
                LanguageData languageData = new LanguageData(lang_name, lang_code, lang_country_code);
                languageDataArrayList.add(languageData);
            }

            LanguageAdapter detailsSizeAdapter = new LanguageAdapter(ActivitySettings.this, languageDataArrayList);
            langList.setAdapter(detailsSizeAdapter);


            detailsSizeAdapter.setOnItemClickListener((position, v) -> {
                alertDialog.dismiss();
                updateLocale(languageDataArrayList.get(position));
                Intent intent = getIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void updateLocale(LanguageData languageData) {

        SharedHelper.putKey(this, "language", languageData.getLanguageCode());

        setLanguage();

    }

    private void getFavoriteLocations() {
        mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        Call<ResponseBody> call = mApiInterface.getFavoriteLocations("XMLHttpRequest",
                SharedHelper.getKey(ActivitySettings.this, "token_type") + " " + SharedHelper.getKey(ActivitySettings.this, "access_token"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull retrofit2.Response<ResponseBody> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                customDialog.dismiss();
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("sUCESS", "bodyString" + bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            JSONArray homeArray = jsonObj.optJSONArray("home");
                            JSONArray workArray = jsonObj.optJSONArray("work");
                            JSONArray othersArray = jsonObj.optJSONArray("others");
                            assert homeArray != null;
                            if (homeArray.length() > 0) {
                                Log.v("Home Address", "" + homeArray);
                                txtHomeLocation.setText(homeArray.optJSONObject(0).optString("address"));
//                                txtDeleteHome.setVisibility(View.VISIBLE);
                                SharedHelper.putKey(ActivitySettings.this, "home", homeArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(ActivitySettings.this, "home_lat", homeArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(ActivitySettings.this, "home_lng", homeArray.optJSONObject(0).optString("longitude"));
                                SharedHelper.putKey(ActivitySettings.this, "home_id", homeArray.optJSONObject(0).optString("id"));
                            } else {
//                                txtDeleteHome.setVisibility(View.GONE);
//                                txtDeleteHome.setText(getResources().getString(R.string.delete));
                                SharedHelper.putKey(ActivitySettings.this, "home", "");
                                SharedHelper.putKey(ActivitySettings.this, "home_lat", "");
                                SharedHelper.putKey(ActivitySettings.this, "home_lng", "");
                                SharedHelper.putKey(ActivitySettings.this, "home_id", "");
                            }
                            assert workArray != null;
                            if (workArray.length() > 0) {
                                Log.v("Work Address", "" + workArray);
                                txtWorkLocation.setText(workArray.optJSONObject(0).optString("address"));
//                                txtDeleteWork.setVisibility(View.VISIBLE);
                                SharedHelper.putKey(ActivitySettings.this, "work", workArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(ActivitySettings.this, "work_lat", workArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(ActivitySettings.this, "work_lng", workArray.optJSONObject(0).optString("longitude"));
                                SharedHelper.putKey(ActivitySettings.this, "work_id", workArray.optJSONObject(0).optString("id"));
                            } else {
//                                txtDeleteWork.setVisibility(View.GONE);
//                                txtDeleteWork.setText(getResources().getString(R.string.delete));
                                SharedHelper.putKey(ActivitySettings.this, "work", "");
                                SharedHelper.putKey(ActivitySettings.this, "work_lat", "");
                                SharedHelper.putKey(ActivitySettings.this, "work_lng", "");
                                SharedHelper.putKey(ActivitySettings.this, "work_id", "");
                            }
                            assert othersArray != null;
                            if (othersArray.length() > 0) {
                                Log.v("Others Address", "" + othersArray);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.e("onFailure", "onFailure" + call.request().url());
                customDialog.dismiss();
            }
        });
    }

    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(this.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(getString(R.string.logout_alert));
            builder.setPositiveButton(R.string.yes, (dialog, which) -> logout());
            builder.setNegativeButton(R.string.no, (dialog, which) -> {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(arg -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ActivitySettings.this, R.color.colorPrimaryDark));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ActivitySettings.this, R.color.colorPrimaryDark));
            });
            dialog.show();
        }
    }

    public void logout() {
        customDialog = new CustomDialog(this);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(this, "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("MainActivity", "logout: " + object);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGOUT, object, response -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            if (SharedHelper.getKey(ActivitySettings.this, "login_by").equals("facebook"))
                LoginManager.getInstance().logOut();
            if (SharedHelper.getKey(ActivitySettings.this, "login_by").equals("google"))

                if (!SharedHelper.getKey(ActivitySettings.this, "account_kit_token").equalsIgnoreCase("")) {
                    Log.e("MainActivity", "Account kit logout: " + SharedHelper.getKey(ActivitySettings.this, "account_kit_token"));
                    AccountKit.logOut();
                    SharedHelper.putKey(ActivitySettings.this, "account_kit_token", "");
                }
            SharedHelper.putKey(ActivitySettings.this, "current_status", "");
            SharedHelper.putKey(ActivitySettings.this, "loggedIn", getString(R.string.False));
            SharedHelper.putKey(ActivitySettings.this, "email", "");
            SharedHelper.putKey(ActivitySettings.this, "login_by", "");
            Intent goToLogin = new Intent(ActivitySettings.this, WelcomeScreenActivity.class);
            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(goToLogin);
            finish();
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json;
            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));
                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            displayMessage(errorObj.getString("message"));
                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }
                    } else if (response.statusCode == 401) {
                        refreshAccessToken();
                    } else if (response.statusCode == 422) {
                        json = UserApplication.trimMessage(new String(response.data));
                        assert json != null;
                        if (!json.equals("")) {
                            displayMessage(json);
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }
                    } else if (response.statusCode == 503) {
                        displayMessage(getString(R.string.server_down));
                    } else {
                        displayMessage(getString(R.string.please_try_again));
                    }
                } catch (Exception e) {
                    displayMessage(getString(R.string.something_went_wrong));
                }
            } else {
                if (error instanceof NoConnectionError) {
                    displayMessage(getString(R.string.oops_connect_your_internet));
                } else if (error instanceof NetworkError) {
                    displayMessage(getString(R.string.oops_connect_your_internet));
                } else if (error instanceof TimeoutError) {
                    logout();
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                Log.e("getHeaders: Token", SharedHelper.getKey(ActivitySettings.this, "access_token") + SharedHelper.getKey(ActivitySettings.this, "token_type"));
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(ActivitySettings.this, "access_token"));
                return headers;
            }
        };
        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @SuppressLint("WrongConstant")
    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        try {
            make(Objects.requireNonNull(getCurrentFocus()), toastString, LENGTH_LONG)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(this, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private void refreshAccessToken() {


        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(ActivitySettings.this, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, response -> {

            Log.v("SignUpResponse", response.toString());
            SharedHelper.putKey(ActivitySettings.this, "access_token", response.optString("access_token"));
            SharedHelper.putKey(ActivitySettings.this, "refresh_token", response.optString("refresh_token"));
            SharedHelper.putKey(ActivitySettings.this, "token_type", response.optString("token_type"));
            logout();


        }, error -> {
            NetworkResponse response = error.networkResponse;

            if (response != null && response.data != null) {
                SharedHelper.putKey(ActivitySettings.this, "loggedIn", getString(R.string.False));

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


    String strLatitude = "", strLongitude = "", strAddress = "", id = "";

    private void deleteFavoriteLocations(final String strTag) {
        if (strTag.equalsIgnoreCase("home")) {
            strLatitude = SharedHelper.getKey(ActivitySettings.this, "home_lat");
            strLongitude = SharedHelper.getKey(ActivitySettings.this, "home_lng");
            strAddress = SharedHelper.getKey(ActivitySettings.this, "home");
            id = SharedHelper.getKey(ActivitySettings.this, "home_id");
        } else {
            strLatitude = SharedHelper.getKey(ActivitySettings.this, "work_lat");
            strLongitude = SharedHelper.getKey(ActivitySettings.this, "work_lng");
            strAddress = SharedHelper.getKey(ActivitySettings.this, "work");
            id = SharedHelper.getKey(ActivitySettings.this, "work_id");
        }
        mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        customDialog.show();

        Call<Errorresponse> call = com.kwendaapp.rideo.API.RetrofitClient.getInstance().getApi().deletefav(id);
        call.enqueue(new Callback<Errorresponse>() {
            @Override
            public void onResponse(@NotNull Call<Errorresponse> call, @NotNull Response<Errorresponse> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                customDialog.dismiss();
                if (response.body() != null) {
                    if (strTag.equalsIgnoreCase("home")) {
                        SharedHelper.putKey(ActivitySettings.this, "home", "");
                        SharedHelper.putKey(ActivitySettings.this, "home_lat", "");
                        SharedHelper.putKey(ActivitySettings.this, "home_lng", "");
                        SharedHelper.putKey(ActivitySettings.this, "home_id", "");
                        txtHomeLocation.setText(getResources().getString(R.string.add_home_location));
//                        txtDeleteHome.setVisibility(View.GONE);
                        gotoHomeWork("home");
                    } else {
                        SharedHelper.putKey(ActivitySettings.this, "work", "");
                        SharedHelper.putKey(ActivitySettings.this, "work_lat", "");
                        SharedHelper.putKey(ActivitySettings.this, "work_lng", "");
                        SharedHelper.putKey(ActivitySettings.this, "work_id", "");
                        txtWorkLocation.setText(getResources().getString(R.string.add_work_location));
//                        txtDeleteWork.setVisibility(View.GONE);
                        gotoHomeWork("work");

                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<Errorresponse> call, @NotNull Throwable t) {
                Log.e("onFailure", "onFailure" + call.request().url());
                customDialog.dismiss();
            }
        });

    }


}
