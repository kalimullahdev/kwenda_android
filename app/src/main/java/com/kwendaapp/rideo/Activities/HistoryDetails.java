package com.kwendaapp.rideo.Activities;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.kwendaapp.rideo.Helper.ConnectionHelper;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Helper.URLHelper;
import com.kwendaapp.rideo.Models.Driver;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.UserApplication;
import com.kwendaapp.rideo.Utils.MyButton;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HistoryDetails extends AppCompatActivity {

    public JSONObject jsonObject;
    Activity activity;
    Context context;
    Boolean isInternet;
    ConnectionHelper helper;
    CustomDialog customDialog;
    TextView tripAmount;
    TextView tripDate;
    TextView paymentType;
    TextView booking_id;
    TextView tripComments;
    TextView tripProviderName;
    TextView tripSource;
    TextView lblTotalPrice;
    TextView lblBookingID;
    TextView tripDestination;
    TextView lblBasePrice;
    TextView lblDistancePrice;
    TextView lbDistanceCovered;
    TextView lblTaxPrice;
    TextView lblTimeTaken;
    TextView lblTimeTakenPrice;
    ImageView tripImg, tripProviderImg, paymentTypeImg;
    RatingBar tripProviderRating;
    LinearLayout sourceAndDestinationLayout, lnrComments, lnrUpcomingLayout;
    ImageView backArrow;
    LinearLayout parentLayout;
    LinearLayout profileLayout;
    LinearLayout lnrInvoice, lnrInvoiceSub;
    String tag = "";
    MyButton btnCancelRide;
    Driver driver;
    String reason = "";
    Button btnViewInvoice, btnCall, btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);
        findViewByIdAndInitialize();
        try {
            Intent intent = getIntent();
            String post_details = intent.getStringExtra("post_value");
            tag = intent.getStringExtra("tag");
            assert post_details != null;
            jsonObject = new JSONObject(post_details);
        } catch (Exception e) {
            jsonObject = null;
        }

        if (jsonObject != null) {

            if (tag.equalsIgnoreCase("past_trips")) {
                btnCancelRide.setVisibility(View.GONE);
                lnrComments.setVisibility(View.VISIBLE);
                lnrUpcomingLayout.setVisibility(View.GONE);
                getRequestDetails();
            } else {
                lnrUpcomingLayout.setVisibility(View.VISIBLE);
                btnViewInvoice.setVisibility(View.GONE);
                btnCancelRide.setVisibility(View.VISIBLE);
                lnrComments.setVisibility(View.GONE);
                getUpcomingDetails();
            }
        }
        profileLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryDetails.this, ShowProfile.class);
            intent.putExtra("driver", driver);
            startActivity(intent);
        });

        backArrow.setOnClickListener(view -> onBackPressed());
    }

    public void findViewByIdAndInitialize() {
        activity = HistoryDetails.this;
        context = HistoryDetails.this;
        helper = new ConnectionHelper(activity);
        isInternet = helper.isConnectingToInternet();
        parentLayout = findViewById(R.id.parentLayout);
        profileLayout = findViewById(R.id.profile_detail_layout);
        lnrInvoice = findViewById(R.id.lnrInvoice);
        lnrInvoiceSub = findViewById(R.id.lnrInvoiceSub);
        parentLayout.setVisibility(View.GONE);
        backArrow = findViewById(R.id.backArrow);
        tripAmount = findViewById(R.id.tripAmount);
        tripDate = findViewById(R.id.tripDate);
        paymentType = findViewById(R.id.paymentType);
        booking_id = findViewById(R.id.booking_id);
        paymentTypeImg = findViewById(R.id.paymentTypeImg);
        tripProviderImg = findViewById(R.id.tripProviderImg);
        tripImg = findViewById(R.id.tripImg);
        tripComments = findViewById(R.id.tripComments);
        tripProviderName = findViewById(R.id.tripProviderName);
        tripProviderRating = findViewById(R.id.tripProviderRating);
        tripSource = findViewById(R.id.tripSource);
        tripDestination = findViewById(R.id.tripDestination);
        lblBookingID = findViewById(R.id.lblBookingID);
        lblBasePrice = findViewById(R.id.lblBasePrice);
        lblTaxPrice = findViewById(R.id.lblTaxPrice);
        lblDistancePrice = findViewById(R.id.lblDistancePrice);
        lbDistanceCovered = findViewById(R.id.lblDistanceCovered);
        lblTimeTaken = findViewById(R.id.lblTimeTaken);
        lblTimeTakenPrice = findViewById(R.id.lblTimeTakenprice);
        lblTotalPrice = findViewById(R.id.lblTotalPrice);
        btnCancelRide = findViewById(R.id.btnCancelRide);
        sourceAndDestinationLayout = findViewById(R.id.sourceAndDestinationLayout);
        lnrComments = findViewById(R.id.lnrComments);
        lnrUpcomingLayout = findViewById(R.id.lnrUpcomingLayout);
        btnViewInvoice = findViewById(R.id.btnViewInvoice);
        btnCall = findViewById(R.id.btnCall);
        btnClose = findViewById(R.id.btnClose);

        btnCancelRide.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.app_name)
                    .setMessage(getString(R.string.cencel_request))
                    .setCancelable(false)
                    .setPositiveButton("YES", (dialog, id) -> {
                        dialog.dismiss();
                        showreasonDialog();
                    })
                    .setNegativeButton("NO", (dialog, id) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        });

        btnViewInvoice.setOnClickListener(v -> lnrInvoice.setVisibility(View.VISIBLE));

        lnrInvoice.setOnClickListener(v -> lnrInvoice.setVisibility(View.GONE));

        btnClose.setOnClickListener(v -> lnrInvoice.setVisibility(View.GONE));

        btnCall.setOnClickListener(v -> {
            if (driver.getMobile() != null && !driver.getMobile().equalsIgnoreCase("null") && driver.getMobile().length() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
                } else {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + driver.getMobile()));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(intentCall);
                }
            } else {
                displayMessage(getString(R.string.user_no_mobile));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + driver.getMobile()));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        }
    }

    private void showreasonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = view.findViewById(R.id.reason_etxt);
        Button submitBtn = view.findViewById(R.id.submit_btn);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setView(view)
                .setCancelable(true);
        submitBtn.setOnClickListener(v -> {
            reason = reasonEtxt.getText().toString();
            cancelRequest();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("SetTextI18n")
    public void getRequestDetails() {

        customDialog = new CustomDialog(context);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_HISTORY_DETAILS_API + "?request_id=" + jsonObject.optString("id"), response -> {

            Log.v("GetPaymentList", response.toString());
            if (response.length() > 0) {
                if (response.optJSONObject(0) != null) {
                    Glide.with(activity).load(response.optJSONObject(0).optString("static_map")).apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder)).into(tripImg);
                    Log.e("History Details", "onResponse: Currency" + SharedHelper.getKey(context, "currency"));
                    JSONObject providerObj = response.optJSONObject(0).optJSONObject("provider");
                    if (providerObj != null) {
                        driver = new Driver();
                        driver.setFname(providerObj.optString("first_name"));
                        driver.setLname(providerObj.optString("last_name"));
                        driver.setMobile(providerObj.optString("mobile"));
                        driver.setEmail(providerObj.optString("email"));
                        driver.setImg(providerObj.optString("avatar"));
                        driver.setRating(providerObj.optString("rating"));
                    }
                    response.optJSONObject(0).optString("booking_id");
                    if (!response.optJSONObject(0).optString("booking_id").equalsIgnoreCase("")) {
                        booking_id.setText(response.optJSONObject(0).optString("booking_id"));
                        lblBookingID.setText(response.optJSONObject(0).optString("booking_id"));
                    }
                    String form;
                    if (tag.equalsIgnoreCase("past_trips")) {
                        form = response.optJSONObject(0).optString("assigned_at");
                    } else {
                        form = response.optJSONObject(0).optString("schedule_at");
                    }
                    if (response.optJSONObject(0).optJSONObject("payment") != null && !Objects.requireNonNull(response.optJSONObject(0).optJSONObject("payment")).optString("payable").equalsIgnoreCase("")) {
                        tripAmount.setText(SharedHelper.getKey(context, "currency") + "" + Objects.requireNonNull(response.optJSONObject(0).optJSONObject("payment")).optString("payable"));
                        response.optJSONObject(0).optJSONObject("payment");
                        lblBasePrice.setText((SharedHelper.getKey(context, "currency") + ""
                                + Objects.requireNonNull(response.optJSONObject(0).optJSONObject("payment")).optString("fixed")));

                        lbDistanceCovered.setText(response.optJSONObject(0).optString("distance") + " KM");

                        /*
                        price
                         */
                        lblDistancePrice.setText((SharedHelper.getKey(context, "currency") + ""
                                + Objects.requireNonNull(response.optJSONObject(0).optJSONObject("payment")).optString("distance")));


                        lblTimeTaken.setText("" + response.optJSONObject(0).optString("travel_time") + " mins");

                           /*
                        price added by biplob
                         */
                        lblTimeTakenPrice.setText((SharedHelper.getKey(context, "currency") + ""
                                + Objects.requireNonNull(response.optJSONObject(0).optJSONObject("payment")).optString("travel_time")));

                        lblTaxPrice.setText((SharedHelper.getKey(context, "currency") + ""
                                + Objects.requireNonNull(response.optJSONObject(0).optJSONObject("payment")).optString("tax")));
                        lblTotalPrice.setText((SharedHelper.getKey(context, "currency") + ""
                                + Objects.requireNonNull(response.optJSONObject(0).optJSONObject("payment")).optString("payable" + "")));
                    } else {
                        tripAmount.setVisibility(View.GONE);
                    }
                    try {
                        tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + "\n" + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (response.optJSONObject(0).optString("payment_mode").equals("CASH")) {
                        paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                    } else {
                        paymentType.setText("ONLINE");
                    }
                    if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
                        paymentTypeImg.setImageResource(R.drawable.money_icon);
                    } else {
                        paymentTypeImg.setImageResource(R.drawable.visa);
                    }
                    Glide.with(activity).load(URLHelper.base + "storage/" + Objects.requireNonNull(response.optJSONObject(0).optJSONObject("provider")).optString("avatar"))
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder)).into(tripProviderImg);
                    if (response.optJSONObject(0).optJSONObject("rating") != null &&
                            !Objects.requireNonNull(response.optJSONObject(0).optJSONObject("rating")).optString("provider_comment").equalsIgnoreCase("")) {
                        tripComments.setText(Objects.requireNonNull(response.optJSONObject(0).optJSONObject("rating")).optString("provider_comment", ""));
                    } else {
                        tripComments.setText(getString(R.string.no_comments));
                    }
                    Objects.requireNonNull(response.optJSONObject(0).optJSONObject("provider")).optString("rating");
                    if (!Objects.requireNonNull(response.optJSONObject(0).optJSONObject("provider")).optString("rating").equalsIgnoreCase("")) {
                        tripProviderRating.setRating(Float.parseFloat(Objects.requireNonNull(response.optJSONObject(0).optJSONObject("provider")).optString("rating")));
                    } else {
                        tripProviderRating.setRating(0);
                    }
                    tripProviderName.setText(Objects.requireNonNull(response.optJSONObject(0).optJSONObject("provider")).optString("first_name") + " " + Objects.requireNonNull(response.optJSONObject(0).optJSONObject("provider")).optString("last_name"));

                    tripSource.setText(response.optJSONObject(0).optString("s_address"));
                    tripDestination.setText(response.optJSONObject(0).optString("d_address"));

                }
            }
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            parentLayout.setVisibility(View.VISIBLE);

        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json ;
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
                        refreshAccessToken("PAST_TRIPS");
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
                displayMessage(getString(R.string.please_try_again));

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

        UserApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    @SuppressLint("SetTextI18n")
    public void getUpcomingDetails() {

        customDialog = new CustomDialog(context);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.UPCOMING_TRIP_DETAILS + "?request_id=" + jsonObject.optString("id"), response -> {
            Log.v("GetPaymentList", response.toString());
            if (response.length() > 0) {
                if (response.optJSONObject(0) != null) {
                    Glide.with(activity).load(response.optJSONObject(0).optString("static_map")).apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder)).into(tripImg);
//                    tripDate.setText(response.optJSONObject(0).optString("assigned_at"));
                    paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                    String form = response.optJSONObject(0).optString("schedule_at");
                    JSONObject providerObj = response.optJSONObject(0).optJSONObject("provider");
                    response.optJSONObject(0).optString("booking_id");
                    if (!response.optJSONObject(0).optString("booking_id").equalsIgnoreCase("")) {
                        booking_id.setText(response.optJSONObject(0).optString("booking_id"));
                    }
                    if (providerObj != null) {
                        driver = new Driver();
                        driver.setFname(providerObj.optString("first_name"));
                        driver.setLname(providerObj.optString("last_name"));
                        driver.setMobile(providerObj.optString("mobile"));
                        driver.setEmail(providerObj.optString("email"));
                        driver.setImg(providerObj.optString("avatar"));
                        driver.setRating(providerObj.optString("rating"));
                    }
                    try {
                        tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + "\n" + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
                        paymentTypeImg.setImageResource(R.drawable.money_icon);
                    } else {
                        paymentTypeImg.setImageResource(R.drawable.visa);
                    }

                    Objects.requireNonNull(response.optJSONObject(0).optJSONObject("provider")).optString("avatar");
                    Glide.with(activity).load(URLHelper.base + "storage/" + Objects.requireNonNull(response.optJSONObject(0).optJSONObject("provider")).optString("avatar"))
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontAnimate()).into(tripProviderImg);

                    tripProviderRating.setRating(Float.parseFloat(Objects.requireNonNull(response.optJSONObject(0).optJSONObject("provider")).optString("rating")));
                    tripProviderName.setText(Objects.requireNonNull(response.optJSONObject(0).optJSONObject("provider")).optString("first_name") + " " + Objects.requireNonNull(response.optJSONObject(0).optJSONObject("provider")).optString("last_name"));
                    response.optJSONObject(0).optString("s_address");
                    response.optJSONObject(0).optString("d_address");
                    if (response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
                        sourceAndDestinationLayout.setVisibility(View.GONE);

                    } else {
                        tripSource.setText(response.optJSONObject(0).optString("s_address"));
                        tripDestination.setText(response.optJSONObject(0).optString("d_address"));
                    }

                    try {
                        JSONObject serviceObj = response.optJSONObject(0).optJSONObject("service_type");
                        if (serviceObj != null) {
//                            holder.car_name.setText(serviceObj.optString("name"));
                            if (tag.equalsIgnoreCase("past_trips")) {
                                tripAmount.setText(SharedHelper.getKey(context, "currency") + serviceObj.optString("price"));
                            } else {
                                tripAmount.setVisibility(View.GONE);
                            }
                            Glide.with(activity).load(serviceObj.optString("image"))
                                    .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontAnimate()).into(tripProviderImg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                parentLayout.setVisibility(View.VISIBLE);

            }
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json ;
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
                        refreshAccessToken("UPCOMING_TRIPS");
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
                displayMessage(getString(R.string.please_try_again));

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

        UserApplication.getInstance().addToRequestQueue(jsonArrayRequest);
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

            Log.v("SignUpResponse", response.toString());
            SharedHelper.putKey(context, "access_token", response.optString("access_token"));
            SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
            SharedHelper.putKey(context, "token_type", response.optString("token_type"));
            if (tag.equalsIgnoreCase("PAST_TRIPS")) {
                getRequestDetails();
            } else if (tag.equalsIgnoreCase("UPCOMING_TRIPS")) {
                getUpcomingDetails();
            } else if (tag.equalsIgnoreCase("CANCEL_REQUEST")) {
                cancelRequest();
            }
        }, error -> {
            NetworkResponse response = error.networkResponse;

            if (response != null && response.data != null) {
                SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                GoToBeginActivity();
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
        Snackbar.make(Objects.requireNonNull(getCurrentFocus()), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    public void onBackPressed() {
        if (lnrInvoice.getVisibility() == View.VISIBLE) {
            lnrInvoice.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public void cancelRequest() {

        customDialog = new CustomDialog(context);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", jsonObject.optString("id"));
            object.put("cancel_reason", reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, response -> {
            Log.v("CancelRequestResponse", response.toString());
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            finish();
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json ;
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
                        refreshAccessToken("CANCEL_REQUEST");
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
                displayMessage(getString(R.string.please_try_again));
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

    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        @SuppressLint("SimpleDateFormat") String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    private String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        @SuppressLint("SimpleDateFormat") String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        @SuppressLint("SimpleDateFormat") String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

    private String getTime(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        @SuppressLint("SimpleDateFormat") String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
        return timeName;
    }
}
