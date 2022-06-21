package com.kwendaapp.rideo.Activities;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.kwendaapp.rideo.API.RetrofitClient;
import com.kwendaapp.rideo.Helper.ConnectionHelper;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Helper.URLHelper;
import com.kwendaapp.rideo.Models.CityResponse;
import com.kwendaapp.rideo.Models.UserResponse;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.UserApplication;
import com.kwendaapp.rideo.Utils.MyEditText;
import com.kwendaapp.rideo.Utils.Utilities;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;

public class ResetPassword extends AppCompatActivity {
    String verificationCodeBySystem;
    FirebaseAuth mAuth;
//    CountryCodePicker mcpp;
    TextView mPhone, sendotpagain;
    EditText mPin;
    PinView otpcode;
    Button verifiy_btn, get_otp, changePasswordBtn;
    LinearLayout view_beforeverify, view_afterverify;
    public Context context = ResetPassword.this;
    public Activity activity = ResetPassword.this;
    ImageView backArrow;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    TextView sendto;
    LinearLayout phoneverificationlayout;
    String emailto, id;
    MyEditText new_password, confirm_password;
    boolean isverificationrequired = true;
    Utilities utils = new Utilities();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        customDialog = new CustomDialog(ResetPassword.this);
        mAuth = FirebaseAuth.getInstance();

        findViewById();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        backArrow.setOnClickListener(view -> {
            Utilities.hideKeyboard(ResetPassword.this);
            onBackPressed();
        });
        verifiy_btn.setOnClickListener(v -> {
            if (otpcode.length() < 6) {
                Toast.makeText(context, "Wrong OTP Code", Toast.LENGTH_SHORT).show();
            } else {
                Utilities.hideKeyboard(ResetPassword.this);
                customDialog.show();
                verifyCode(String.valueOf(otpcode.getText()));
            }
        });
        sendotpagain.setOnClickListener(v -> {
            sendotpagain.setEnabled(false);
            customDialog.show();
            sendVerificationCodeToUser(mPhone.getText().toString());
        });
        changePasswordBtn.setOnClickListener(v -> {
            if (new_password.getText().toString().trim().equals("")) {
                new_password.setError("Please Enter Password");
            } else if (confirm_password.getText().toString().trim().equals("")) {
                confirm_password.setError("Please Confirm Password");
            } else if (!new_password.getText().toString().trim().equals(confirm_password.getText().toString().trim())) {
                displayMessage("Password Doesn't Match");
            } else {
                resetpassword();
            }
        });
        get_otp.setOnClickListener(v -> {
            if (isverificationrequired) {
                if (mPhone.getText().toString().equals("")) {
                    displayMessage("Please Enter Your Phone Number");
                } else {
                    new CountDownTimer(60000, 1000) {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTick(long millisUntilFinished) {
                            sendotpagain.setText(getResources().getString(R.string.send_otp_again) + " (" + millisUntilFinished / 1000 + ")");
                        }

                        public void onFinish() {
                            sendotpagain.setEnabled(true);
                            v.invalidate();
                        }
                    }.start();
                    Utilities.hideKeyboard(ResetPassword.this);
                    customDialog.show();
                    sendVerificationCodeToUser(mPhone.getText().toString());
                }
            } else {
                Utilities.hideKeyboard(ResetPassword.this);
                getemail();
            }

        });

        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String x = s.toString();
                if (x.startsWith("0")) {
                    mPhone.setText("");
                }
            }
        });

        checkverification();
    }

    private void checkverification() {
        customDialog.show();
        Call<CityResponse> call = RetrofitClient.getInstance().getApi().getcity();
        call.enqueue(new Callback<CityResponse>() {
            @Override
            public void onResponse(@NotNull Call<CityResponse> call, @NotNull retrofit2.Response<CityResponse> response) {
                if (response.isSuccessful()) {
                    isverificationrequired = response.body().isVerification();
                }
                if (customDialog.isShowing())
                    customDialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<CityResponse> call, @NotNull Throwable t) {
                if (customDialog.isShowing())
                    customDialog.dismiss();
            }
        });
    }

    public void findViewById() {
        otpcode = findViewById(R.id.otppin);
        verifiy_btn = findViewById(R.id.verify_btn);
        get_otp = findViewById(R.id.get_otp);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        view_beforeverify = findViewById(R.id.view_beforeverification);
        view_afterverify = findViewById(R.id.view_afterverify);
        mPin = findViewById(R.id.passpin);
        mPhone = findViewById(R.id.idphone);
//        mcpp = findViewById(R.id.mcpp);
        backArrow = findViewById(R.id.backArrow);
        sendotpagain = findViewById(R.id.sendotpagain);
        sendto = findViewById(R.id.senttotext);
        new_password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_password);
        helper = new ConnectionHelper(context);
        phoneverificationlayout = findViewById(R.id.phoneverificationlayout);
        isInternet = helper.isConnectingToInternet();
    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(getString(R.string.country_code) + phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        @SuppressLint("SetTextI18n")
        @Override
        public void onCodeSent(@NotNull String s, @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
            if (customDialog.isShowing())
                customDialog.dismiss();
            mPhone.setEnabled(false);
            sendto.setText(getResources().getString(R.string.sent_to) + " " +getString(R.string.country_code)  + mPhone.getText().toString());
            phoneverificationlayout.setVisibility(View.GONE);
            view_beforeverify.setVisibility(View.VISIBLE);
        }


        @Override
        public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {

        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(ResetPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };


    private void verifyCode(String codeByUser) {


        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInTheUserByCredentials(credential);


    }


    private void signInTheUserByCredentials(PhoneAuthCredential credential) {


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(ResetPassword.this, task -> {
                    if (task.isSuccessful()) {
                        getemail();
                    } else {
                        if (customDialog.isShowing())
                            customDialog.dismiss();
                        Toast.makeText(ResetPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }


                });

    }

    private void getemail() {
        Call<UserResponse> call = RetrofitClient
                .getInstance().getApi().userlogin(getString(R.string.country_code)  + mPhone.getText().toString());
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NotNull Call<UserResponse> call, @NotNull retrofit2.Response<UserResponse> response) {
                if (!response.body().isError()) {
                    phoneverificationlayout.setVisibility(View.GONE);
                    UserResponse userResponse = response.body();
                    emailto = userResponse.getUser().getEmail();
                    customDialog.dismiss();
                    ForgetPassword();
                } else {
                    Toast.makeText(ResetPassword.this, "This Phone Number is not registered", Toast.LENGTH_SHORT).show();
                }

                customDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<UserResponse> call, @NotNull Throwable t) {
                customDialog.dismiss();
            }
        });
    }

    private void ForgetPassword() {
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {

            object.put("email", emailto);
            Log.e("ResetPassword", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.FORGET_PASSWORD, object, response -> {
            customDialog.dismiss();
            JSONObject userObject = response.optJSONObject("user");
            id = String.valueOf(userObject.optInt("id"));
            view_beforeverify.setVisibility(View.GONE);
            view_afterverify.setVisibility(View.VISIBLE);
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json ;
            NetworkResponse response = error.networkResponse;
            Log.e("MyTest", "" + error);
            Log.e("MyTestError", "" + error.networkResponse);
            Log.e("MyTestError1", "" + response.statusCode);
            if (response != null && response.data != null) {
                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));

                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            displayMessage(errorObj.optString("message"));
                        } catch (Exception e) {
                            displayMessage("Something went wrong.");
                        }
                    } else if (response.statusCode == 401) {
                        try {
                            if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                refreshAccessToken("FORGOT_PASSWORD");
                            } else {
                                displayMessage(errorObj.optString("message"));
                            }
                        } catch (Exception e) {
                            displayMessage("Something went wrong.");
                        }

                    } else if (response.statusCode == 422) {

                        json = UserApplication.trimMessage(new String(response.data));
                        if (!json.equals("") && json != null) {
                            displayMessage(json);
                        } else {
                            displayMessage("Please try again.");
                        }

                    } else {
                        displayMessage("Please try again.");
                    }

                } catch (Exception e) {
                    displayMessage("Something went wrong.");
                }

            } else {
                if (error instanceof NoConnectionError) {
                    displayMessage(getString(R.string.oops_connect_your_internet));
                } else if (error instanceof NetworkError) {
                    displayMessage(getString(R.string.oops_connect_your_internet));
                } else if (error instanceof TimeoutError) {
                    ForgetPassword();
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


    private void resetpassword() {
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("password", new_password.getText().toString());
            object.put("password_confirmation", confirm_password.getText().toString());
            Log.e("ResetPassword", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.RESET_PASSWORD, object, response -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            Log.v("ResetPasswordResponse", response.toString());
            try {
                JSONObject object1 = new JSONObject(response.toString());
                Toast.makeText(context, object1.optString("message"), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ResetPassword.this, otpscreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json;
            NetworkResponse response = error.networkResponse;
            Log.e("MyTest", "" + error);
            Log.e("MyTestError", "" + error.networkResponse);
            Log.e("MyTestError1", "" + response.statusCode);
            if (response != null && response.data != null) {
                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));

                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            displayMessage(errorObj.optString("message"));
                        } catch (Exception e) {
                            displayMessage("Something went wrong.");
                        }
                    } else if (response.statusCode == 401) {
                        try {
                            if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                refreshAccessToken("RESET_PASSWORD");
                            } else {
                                displayMessage(errorObj.optString("message"));
                            }
                        } catch (Exception e) {
                            displayMessage("Something went wrong.");
                        }

                    } else if (response.statusCode == 422) {

                        json = UserApplication.trimMessage(new String(response.data));
                        if (!json.equals("") && json != null) {
                            displayMessage(json);
                        } else {
                            displayMessage("Please try again.");
                        }

                    } else {
                        displayMessage("Please try again.");
                    }

                } catch (Exception e) {
                    displayMessage("Something went wrong.");
                }


            } else {
                if (error instanceof NoConnectionError) {
                    displayMessage(getString(R.string.oops_connect_your_internet));
                } else if (error instanceof NetworkError) {
                    displayMessage(getString(R.string.oops_connect_your_internet));
                } else if (error instanceof TimeoutError) {
                    resetpassword();
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

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private void refreshAccessToken(final String tag) {

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
            SharedHelper.putKey(context, "access_token", response.optString("access_token"));
            SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
            SharedHelper.putKey(context, "token_type", response.optString("token_type"));
            if (tag.equalsIgnoreCase("FORGOT_PASSWORD")) {
                ForgetPassword();
            } else {
                resetpassword();
            }
        }, error -> {
            String json = "";
            NetworkResponse response = error.networkResponse;

            if (response != null && response.data != null) {
                SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                utils.GoToBeginActivity(ResetPassword.this);
            } else {
                if (error instanceof NoConnectionError) {
                    displayMessage(getString(R.string.oops_connect_your_internet));
                } else if (error instanceof NetworkError) {
                    displayMessage(getString(R.string.oops_connect_your_internet));
                } else if (error instanceof TimeoutError) {
                    refreshAccessToken(tag);
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
}
