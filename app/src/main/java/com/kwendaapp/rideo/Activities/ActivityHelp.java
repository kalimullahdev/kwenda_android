package com.kwendaapp.rideo.Activities;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Helper.URLHelper;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.UserApplication;
import com.kwendaapp.rideo.Utils.Utilities;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivityHelp extends AppCompatActivity implements View.OnClickListener {
    ImageView imgEmail;
    ImageView imgPhone;
    ImageView imgchat;
    ImageView backArrow;
    String phone = "";
    String email = "";
    Context context = ActivityHelp.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_help);
        findviewByIdS();
        setOnClickListener();
        getHelp();
    }

    private void findviewByIdS() {
        imgEmail = findViewById(R.id.img_mail);
        imgPhone = findViewById(R.id.img_phone);
        imgchat = findViewById(R.id.img_chat);
        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(view -> onBackPressed());
    }

    private void setOnClickListener() {
        imgEmail.setOnClickListener(this);
        imgPhone.setOnClickListener(this);
        imgchat.setOnClickListener(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View v) {
        if (v == imgEmail) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.app_name) + "-" + getString(R.string.help));
            intent.putExtra(Intent.EXTRA_TEXT, "Hello team");
            startActivity(Intent.createChooser(intent, "Send Email"));
        }
        if (v == imgPhone) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            assert telephonyManager != null;
            boolean hasNetwork = telephonyManager.getNetworkType() != TelephonyManager.NETWORK_TYPE_UNKNOWN;
            if (phone != null && !phone.equalsIgnoreCase("null") && !phone.equalsIgnoreCase("") && phone.length() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone));
                    context.startActivity(intent);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(context.getResources().getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(context.getResources().getString(R.string.sorry_for_inconvinent))
                        .setCancelable(false)
                        .setPositiveButton("ok",
                                (dialog, id) -> dialog.dismiss());
                AlertDialog alert1 = builder.create();
                alert1.show();
            }
        }
        if (v == imgchat) {
            openWhatsappSupport();
//            openlivesupport();
        }
    }

    private void openlivesupport() {
        startActivity(new Intent(ActivityHelp.this, chat_support.class));
    }

    private void openWhatsappSupport() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+phone));
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            } else {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void getHelp() {
        final CustomDialog customDialog = new CustomDialog(this);
        customDialog.show();
        JSONObject object = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.HELP, object, response -> {
            customDialog.dismiss();
            phone = response.optString("contact_number");
            email = response.optString("contact_email");
        }, error -> {
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
                            displayMessage(context.getResources().getString(R.string.something_went_wrong));
                            e.printStackTrace();
                        }
                    } else if (response.statusCode == 401) {
                        refreshAccessToken();
                    } else if (response.statusCode == 422) {

                        json = UserApplication.trimMessage(new String(response.data));
                        assert json != null;
                        if (!json.equals("")) {
                            displayMessage(json);
                        } else {
                            displayMessage(context.getResources().getString(R.string.please_try_again));
                        }
                    } else if (response.statusCode == 503) {
                        displayMessage(context.getResources().getString(R.string.server_down));
                    } else {
                        displayMessage(context.getResources().getString(R.string.please_try_again));
                    }

                } catch (Exception e) {
                    displayMessage(context.getResources().getString(R.string.something_went_wrong));
                    e.printStackTrace();
                }

            } else {
                if (error instanceof NoConnectionError) {
                    displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                } else if (error instanceof NetworkError) {
                    displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                } else if (error instanceof TimeoutError) {
                    getHelp();
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(ActivityHelp.this, "access_token"));
                Log.e("", "Access_Token" + SharedHelper.getKey(ActivityHelp.this, "access_token"));
                return headers;
            }
        };
        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
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
            SharedHelper.putKey(ActivityHelp.this, "access_token", response.optString("access_token"));
            SharedHelper.putKey(ActivityHelp.this, "refresh_token", response.optString("refresh_token"));
            SharedHelper.putKey(ActivityHelp.this, "token_type", response.optString("token_type"));
            getHelp();

        }, error -> {
            NetworkResponse response = error.networkResponse;

            if (response != null && response.data != null) {
                SharedHelper.putKey(ActivityHelp.this, "loggedIn", context.getResources().getString(R.string.False));
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


    public void displayMessage(String toastString) {
        Utilities.print("displayMessage", "" + toastString);
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

    public void GoToBeginActivity() {
        SharedHelper.putKey(this, "loggedIn", context.getResources().getString(R.string.False));
        Intent mainIntent = new Intent(this, WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        this.finish();
    }
}
