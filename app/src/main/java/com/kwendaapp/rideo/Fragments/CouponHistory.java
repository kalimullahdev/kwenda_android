package com.kwendaapp.rideo.Fragments;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.kwendaapp.rideo.Activities.WelcomeScreenActivity;
import com.kwendaapp.rideo.Helper.ConnectionHelper;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Helper.URLHelper;
import com.kwendaapp.rideo.Models.CouponHistoryData;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.UserApplication;
import com.kwendaapp.rideo.Utils.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CouponHistory extends Fragment {
    Activity activity;
    Context context;
    Boolean isInternet;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    RelativeLayout errorLayout;
    ConnectionHelper helper;
    CustomDialog customDialog;
    View rootView;
    ArrayList<CouponHistoryData> lstCouponHistoryData = new ArrayList<CouponHistoryData>();

    public CouponHistory() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_coupon_history, container, false);
        findViewByIdAndInitialize();

        if (isInternet) {
            getHistoryList();
        }

        return rootView;
    }


    public void getHistoryList() {

        customDialog = new CustomDialog(context);
        if (customDialog != null)
            customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_COUPON_HISTORY, response -> {

            Log.v("GetHistoryList", response.toString());

            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObj = response.optJSONObject(i);
                CouponHistoryData couponHistoryData = new CouponHistoryData();
                couponHistoryData.id = jsonObj.optInt("id");
                couponHistoryData.userId = jsonObj.optInt("id");
                couponHistoryData.promocode_id = jsonObj.optInt("promocode_id");
                couponHistoryData.status = jsonObj.optString("status");
                couponHistoryData.createdAt = jsonObj.optString("created_at");

                String strPromocode = jsonObj.optString("promocode");

                JSONObject jsonPromoCode = null;
                try {
                    jsonPromoCode = new JSONObject(strPromocode);

                    if (jsonPromoCode != null) {
                        couponHistoryData.promocode_id = jsonPromoCode.optInt("id");
                        couponHistoryData.promoCode = jsonPromoCode.optString("promo_code");
                        couponHistoryData.discount = jsonPromoCode.optInt("discount");
                        couponHistoryData.discountType = jsonPromoCode.optString("discount_type");
                        couponHistoryData.expiration = jsonPromoCode.optString("expiration");
                        couponHistoryData.promocode_status = jsonPromoCode.optString("status");
                        couponHistoryData.deletedAt = jsonPromoCode.optString("deleted_at");
                        lstCouponHistoryData.add(couponHistoryData);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (response != null) {
                postAdapter = new PostAdapter(response, lstCouponHistoryData);
                //  recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                if (postAdapter != null && postAdapter.getItemCount() > 0) {
                    errorLayout.setVisibility(View.GONE);
                    recyclerView.setAdapter(postAdapter);
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                }

            } else {
                errorLayout.setVisibility(View.VISIBLE);
            }
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();

        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json = null;
            String Message;
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
                        if (!json.equals("") && json != null) {
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
                    getHistoryList();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
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
                getHistoryList();
            }


        }, error -> {
            String json = null;
            String Message;
            NetworkResponse response = error.networkResponse;

            if (response != null && response.data != null) {
                SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                GoToBeginActivity();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    public void findViewByIdAndInitialize() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
        errorLayout = rootView.findViewById(R.id.errorLayout);
        errorLayout.setVisibility(View.GONE);
        helper = new ConnectionHelper(getActivity());
        isInternet = helper.isConnectingToInternet();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                e.printStackTrace();
            }
        }
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
        JSONArray jsonArray;
        ArrayList<CouponHistoryData> lstCouponHistory;

        public PostAdapter(JSONArray array, ArrayList<CouponHistoryData> lstCouponHistory) {
            this.jsonArray = array;
            this.lstCouponHistory = lstCouponHistory;
        }

        public void append(JSONArray array) {
            try {
                for (int i = 0; i < array.length(); i++) {
                    this.jsonArray.put(array.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public PostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.coupon_history_list_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PostAdapter.MyViewHolder holder, int position) {
            try {
                holder.couponCode.setText(lstCouponHistory.get(position).promoCode);
                holder.date.setText("" + getDate(lstCouponHistory.get(position).createdAt) + " " + Utilities.getMonth(lstCouponHistory.get(position).createdAt) + " " + getYear(lstCouponHistory.get(position).createdAt));
                if (lstCouponHistory.get(position).discountType.equalsIgnoreCase("percent")) {
                    holder.offer.setText(lstCouponHistory.get(position).discount + "% OFF");
                } else {
                    holder.offer.setText(SharedHelper.getKey(context, "currency") + lstCouponHistory.get(position).discount + " OFF");
                }
                if (lstCouponHistory.get(position).status.equalsIgnoreCase("ADDED")) {
                    holder.status.setTextColor(ContextCompat.getColor(context, R.color.blue));
                    holder.status.setText("" + lstCouponHistory.get(position).status);

                } else if (lstCouponHistory.get(position).promocode_status.equalsIgnoreCase("USED")) {
                    holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
                    holder.status.setText("" + lstCouponHistory.get(position).status);
                } else {
                    holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
                    holder.status.setText("" + lstCouponHistory.get(position).status);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return lstCouponHistory.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView offer;
            TextView couponCode, date, status;

            public MyViewHolder(View itemView) {
                super(itemView);
                offer = itemView.findViewById(R.id.offer);
                couponCode = itemView.findViewById(R.id.couponCode);
                date = itemView.findViewById(R.id.date);
                status = itemView.findViewById(R.id.status);

                itemView.setOnClickListener(view -> {
                    //onClick Event
                });

            }
        }
    }


    private String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

}