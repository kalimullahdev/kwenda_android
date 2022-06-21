package com.kwendaapp.rideo.Activities;


import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.kwendaapp.rideo.Constants.CouponListAdapter;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Helper.URLHelper;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.UserApplication;
import com.kwendaapp.rideo.Utils.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class CouponActivity extends AppCompatActivity {

    private EditText coupon_et;
    private String session_token;
    Context context;
    LinearLayout couponListCardView;
    ListView coupon_list_view;
    ArrayList<JSONObject> listItems;
    ListAdapter couponAdapter;
    CustomDialog customDialog;
    Utilities utils = new Utilities();
    ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.coupon_bg);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_coupon);
        context = CouponActivity.this;
        session_token = SharedHelper.getKey(this, "access_token");
        couponListCardView = findViewById(R.id.cardListViewLayout);
        coupon_list_view = findViewById(R.id.coupon_list_view);
        coupon_et = findViewById(R.id.coupon_et);
        Button apply_button = findViewById(R.id.apply_button);
        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(view -> onBackPressed());

        apply_button.setOnClickListener(view -> {
            Utilities.hideKeyboard(CouponActivity.this);
            if (coupon_et.getText().toString().isEmpty()) {
                Toast.makeText(CouponActivity.this, "Enter a coupon", Toast.LENGTH_SHORT).show();
            } else {
                sendToServer();
            }
        });

        getCoupon();
    }

    private void sendToServer() {
        customDialog = new CustomDialog(context);
        customDialog.show();
        JsonObject json = new JsonObject();
        json.addProperty("promocode", coupon_et.getText().toString());
        Ion.with(this)
                .load(URLHelper.ADD_COUPON_API)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(CouponActivity.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    try {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        // response contains both the headers and the string result
                        if (e != null) {
                            if (e instanceof NetworkErrorException) {
                                displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                            }
                            if (e instanceof TimeoutException) {
                                sendToServer();
                            }
                            return;
                        }
                        if (response.getHeaders().code() == 200) {
                            Utilities.print("AddCouponRes", "" + response.getResult());
                            try {
                                JSONObject jsonObject = new JSONObject(response.getResult());
                                if (jsonObject.optString("code").equals("promocode_applied")) {
                                    Toast.makeText(CouponActivity.this, context.getResources().getString(R.string.coupon_added), Toast.LENGTH_SHORT).show();
                                    couponListCardView.setVisibility(View.GONE);
                                    getCoupon();
                                } else if (jsonObject.optString("code").equals("promocode_expired")) {
                                    Toast.makeText(CouponActivity.this, context.getResources().getString(R.string.expired_coupon), Toast.LENGTH_SHORT).show();
                                } else if (jsonObject.optString("code").equals("promocode_already_in_use")) {
                                    Toast.makeText(CouponActivity.this, context.getResources().getString(R.string.already_in_use_coupon), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CouponActivity.this, context.getResources().getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            Utilities.print("AddCouponErr", "" + response.getResult());
                            if (response.getHeaders().code() == 401) {
                                refreshAccessToken("SEND_TO_SERVER");
                            } else
                                Toast.makeText(CouponActivity.this, context.getResources().getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                });
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

            Utilities.print("SignUpResponse", response.toString());
            SharedHelper.putKey(context, "access_token", response.optString("access_token"));
            SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
            SharedHelper.putKey(context, "token_type", response.optString("token_type"));
            if (tag.equalsIgnoreCase("SEND_TO_SERVER")) {
                sendToServer();
            } else {
                getCoupon();
            }
        }, error -> {
            NetworkResponse response = error.networkResponse;

            if (response != null && response.data != null) {
                SharedHelper.putKey(context, "loggedIn", context.getResources().getString(R.string.False));
                utils.GoToBeginActivity(CouponActivity.this);
            } else {
                if (error instanceof NoConnectionError) {
                    displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                } else if (error instanceof NetworkError) {
                    displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
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

    private void getCoupon() {
        couponListCardView.setVisibility(View.GONE);
        customDialog = new CustomDialog(context);
        customDialog.show();
        Ion.with(this)
                .load(URLHelper.COUPON_LIST_API)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(CouponActivity.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    // response contains both the headers and the string result
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    if (e != null) {
                        if (e instanceof NetworkErrorException) {
                            displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                        }
                        if (e instanceof TimeoutException) {
                            getCoupon();
                        }
                    } else {
                        if (response != null) {
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getResult());
                                    if (jsonArray.length() > 0) {
                                        Utilities.print("CouponActivity", "" + jsonArray.toString());
                                        listItems = getArrayListFromJSONArray(jsonArray);
                                        couponAdapter = new CouponListAdapter(context, R.layout.coupon_list_item, listItems);
                                        coupon_list_view.setAdapter(couponAdapter);
                                        couponListCardView.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                                if (response.getHeaders().code() == 401) {
                                    refreshAccessToken("GET_COUPON");
                                }
                            }
                        } else {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                        }
                    }
                });
    }

    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray) {

        ArrayList<JSONObject> aList = new ArrayList<>();

        try {
            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    aList.add(jsonArray.getJSONObject(i));

                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return aList;

    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(Objects.requireNonNull(getCurrentFocus()), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}
