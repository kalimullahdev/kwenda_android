package com.kwendaapp.rideo.Activities;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.kwendaapp.rideo.Helper.ConnectionHelper;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Helper.URLHelper;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.UserApplication;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChangePassword extends AppCompatActivity {
    public Context context = ChangePassword.this;
    public Activity activity = ChangePassword.this;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Button changePasswordBtn;
    ImageView backArrow;
    EditText current_password, new_password, confirm_new_password;
    AlertDialog alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        findViewByIdandInitialization();

        backArrow.setOnClickListener(view -> onBackPressed());

        changePasswordBtn.setOnClickListener(view -> {
            String current_password_value = current_password.getText().toString();
            String new_password_value = new_password.getText().toString();
            String confirm_password_value = confirm_new_password.getText().toString();
            if (current_password_value.equalsIgnoreCase("")) {
                displayMessage(getString(R.string.please_enter_current_pass));
            } else if (new_password_value.equalsIgnoreCase("")) {
                displayMessage(getString(R.string.please_enter_new_pass));
            } else if (confirm_password_value.equalsIgnoreCase("")) {
                displayMessage(getString(R.string.please_enter_confirm_pass));
            } else if (new_password_value.length() < 6 || new_password_value.length() > 16) {
                displayMessage(getString(R.string.password_validation1));
            } else if (current_password_value.equalsIgnoreCase(new_password_value)) {
                displayMessage(getString(R.string.new_password_validation));
            } else if (!new_password_value.equals(confirm_password_value)) {
                displayMessage(getString(R.string.different_passwords));
            } else {
                if (helper.isConnectingToInternet()) {
                    changePassword(current_password_value, new_password_value, confirm_password_value);
                } else {
                    displayMessage(getString(R.string.something_went_wrong_net));
                }
            }
        });

    }

    public void findViewByIdandInitialization() {
        current_password = findViewById(R.id.current_password);
        new_password = findViewById(R.id.new_password);
        confirm_new_password = findViewById(R.id.confirm_password);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        backArrow = findViewById(R.id.backArrow);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
    }


    private void changePassword(final String current_pass, final String new_pass, final String confirm_new_pass) {

        customDialog = new CustomDialog(context);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("password", new_pass);
            object.put("password_confirmation", confirm_new_pass);
            object.put("old_password", current_pass);
            Log.e("ChangePasswordAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CHANGE_PASSWORD_API, object, response -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            Log.v("SignInResponse", response.toString());
            showDialog();
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json;
            NetworkResponse response = error.networkResponse;
            Log.e("MyTest", "" + error);
            Log.e("MyTestError", "" + error.networkResponse);
            Log.e("MyTestError1", "" + response.statusCode);
            if (response.data != null) {
                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));
                    Log.e("ErrorChangePasswordAPI", "" + errorObj.toString());

                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            displayMessage(errorObj.getString("error"));
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
                    changePassword(current_pass, new_pass, confirm_new_pass);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    private void refreshAccessToken() {


        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(getApplicationContext(), "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, response -> {

            Log.v("SignUpResponse", response.toString());
            SharedHelper.putKey(ChangePassword.this, "access_token", response.optString("access_token"));
            SharedHelper.putKey(ChangePassword.this, "refresh_token", response.optString("refresh_token"));
            SharedHelper.putKey(ChangePassword.this, "token_type", response.optString("token_type"));

            String current_password_value = current_password.getText().toString();
            String new_password_value = new_password.getText().toString();
            String confirm_password_value = confirm_new_password.getText().toString();
            changePassword(current_password_value, new_password_value, confirm_password_value);
        }, error -> {
            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                SharedHelper.putKey(ChangePassword.this, "loggedIn", getString(R.string.False));
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

    }


    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        try {
            Snackbar.make(Objects.requireNonNull(getCurrentFocus()), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private void showDialog() {
        SharedHelper.clearSharedPreferences(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.change_pwd_dialog))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.done), (dialog, id) -> GoToBeginActivity());
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }
}
