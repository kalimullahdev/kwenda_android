package com.kwendaapp.rideo.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
import com.kwendaapp.rideo.API.RetrofitClient;
import com.kwendaapp.rideo.Helper.ConnectionHelper;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Helper.URLHelper;
import com.kwendaapp.rideo.Models.UserResponse;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.UserApplication;
import com.kwendaapp.rideo.Utils.Utilities;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class otpscreen extends AppCompatActivity {
    String emilto;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    Context context = otpscreen.this;
    String TAG = "BEGINSCREEN";
    String device_token, device_UDID;
    ProgressDialog mProg;
    FirebaseAuth mAuth;
    String phone;
    EditText Phonenum;
    Button verifyCodeButton;
    CardView phone_layout;
    EditText verifyCodeET;
    //    CountryCodePicker cpp;
    TextView changePasswordTxt;
    ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otpscreen);

        customDialog = new CustomDialog(this);
        mProg = new ProgressDialog(otpscreen.this);
        helper = new ConnectionHelper(otpscreen.this);

        mAuth = FirebaseAuth.getInstance();
        verifyCodeButton = findViewById(R.id.submit);
        verifyCodeET = findViewById(R.id.pinView);
        Phonenum = findViewById(R.id.idphone);
        phone_layout = findViewById(R.id.phone_layout);
        backArrow = findViewById(R.id.backArrow);
        changePasswordTxt = findViewById(R.id.changePasswordTxt);
        isInternet = helper.isConnectingToInternet();
//        cpp = findViewById(R.id.mcpp);

        GetToken();
        backArrow.setOnClickListener(view -> onBackPressed());
        changePasswordTxt.setOnClickListener(v -> startActivity(new Intent(otpscreen.this, ResetPassword.class)));
        Phonenum.setOnFocusChangeListener((view, b) -> hideKeyboard(view));
        verifyCodeButton.setOnClickListener(v -> {
            if (!Phonenum.getText().toString().trim().equals("")) {
                if (!verifyCodeET.getText().toString().trim().equals("")) {
                    customDialog.show();
                    phone = Phonenum.getText().toString().trim();
                    Call<UserResponse> call = RetrofitClient
                            .getInstance().getApi().userlogin(getString(R.string.country_code) + Phonenum.getText().toString());
                    call.enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<UserResponse> call, @NotNull retrofit2.Response<UserResponse> response) {
                            assert response.body() != null;
                            if (!response.body().isError()) {
                                UserResponse userResponse = response.body();
                                emilto = userResponse.getUser().getEmail();
                                hideKeyboard(v);
                                signIn(emilto);
                            } else {
                                Toast.makeText(otpscreen.this, "Please Register: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                customDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<UserResponse> call, @NotNull Throwable t) {
                            customDialog.dismiss();
                        }
                    });
                } else {
                    verifyCodeET.setError(getResources().getString(R.string.please_enter_password));
                }
            } else {
                Phonenum.setError(getResources().getString(R.string.please_enter_your_phone));
            }

        });
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void signIn(String mail) {
        if (helper.isConnectingToInternet()) {

            JSONObject object = new JSONObject();
            try {

                object.put("grant_type", "password");
                object.put("client_id", URLHelper.client_id);
                object.put("client_secret", URLHelper.client_secret);
                object.put("username", mail);
                object.put("password", verifyCodeET.getText().toString());
                object.put("scope", "");
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                Utilities.print("InputToLoginAPI", "" + object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, response -> {
                customDialog.dismiss();
                Utilities.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                getProfile();
            }, error -> {
                if (customDialog.isShowing())
                    customDialog.dismiss();
                if (customDialog.isShowing())
                    customDialog.dismiss();
                String json;
                NetworkResponse response = error.networkResponse;
                Utilities.print("MyTest", "" + error);
                Utilities.print("MyTestError", "" + error.networkResponse);

                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 401) {
                            try {
                                customDialog.dismiss();
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 422) {
                            if (customDialog.isShowing())
                                customDialog.dismiss();
                            json = UserApplication.trimMessage(new String(response.data));
                            assert json != null;
                            if (!json.equals("")) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

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
                        signIn(mail);
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
        } else {
            displayMessage(context.getResources().getString(R.string.something_went_wrong_net));
        }

    }

    public void getProfile() {
        if (isInternet) {

            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.UserProfile + "?device_type=android&device_id=" + device_UDID + "&device_token=" + device_token, object, response -> {
                if ((customDialog != null) && customDialog.isShowing())
                    customDialog.dismiss();
                Utilities.print("GetProfile", response.toString());
                SharedHelper.putKey(context, "id", response.optString("id"));
                SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                SharedHelper.putKey(context, "email", response.optString("email"));
                if (response.optString("picture").startsWith("http"))
                    SharedHelper.putKey(context, "picture", response.optString("picture"));
                else
                    SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("picture"));
                SharedHelper.putKey(context, "gender", response.optString("gender"));
                SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                if (!response.optString("currency").equalsIgnoreCase(""))
                    SharedHelper.putKey(context, "currency", response.optString("currency"));
                else
                    SharedHelper.putKey(context, "currency", "$");
                SharedHelper.putKey(context, "sos", response.optString("sos"));
                SharedHelper.putKey(context, "loggedIn", getString(R.string.True));
                if (SharedHelper.getKey(context, "current_city").equals(""))
                    GoToCityActivity();
                else
                    GoToMainActivity();

            }, error -> {
                if ((customDialog != null) && customDialog.isShowing())
                    customDialog.dismiss();
                String json;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
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
                        getProfile();
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
                            + SharedHelper.getKey(context, "access_token"));
                    Utilities.print("authoization", "" + SharedHelper.getKey(context, "token_type") + " "
                            + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    private void refreshAccessToken() {
        if (isInternet) {

            if (customDialog != null)
                customDialog.show();
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
                if ((customDialog != null) && customDialog.isShowing())
                    customDialog.dismiss();
                Utilities.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                getProfile();


            }, error -> {
                if ((customDialog != null) && customDialog.isShowing())
                    customDialog.dismiss();
                NetworkResponse response = error.networkResponse;
                Utilities.print("MyTest", "" + error);
                Utilities.print("MyTestError", "" + error.networkResponse);
                Utilities.print("MyTestError1", "" + response.statusCode);

                if (response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    GoToBeginActivity();
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
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

        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    @SuppressLint("HardwareIds")
    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Utilities.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "COULD NOT GET FCM TOKEN";
                Utilities.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Utilities.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Utilities.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Utilities.print(TAG, "Failed to complete device UDID");
        }
    }


    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(context, WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        customDialog.dismiss();
        Snackbar.make(Objects.requireNonNull(getCurrentFocus()), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }
    public void GoToCityActivity() {
        Intent mainIntent = new Intent(context, SelectCityActivity.class);
        mainIntent.putExtra("tag", true);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }
}
