package com.kwendaapp.rideo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.kwendaapp.rideo.Adapter.CityAdapter;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Helper.URLHelper;
import com.kwendaapp.rideo.Models.City;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.UserApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectCityActivity extends AppCompatActivity {
    CityAdapter cityAdapter;
    RecyclerView recyclerView;
    RelativeLayout errorLayout;
    CustomDialog customDialog;
    private ArrayList<City> itemsList = new ArrayList<>();
    boolean tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        tag = getIntent().getBooleanExtra("tag", false);
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(view -> onBackPressed());
        recyclerView = findViewById(R.id.cityList);
        errorLayout = findViewById(R.id.errorLayout);
        cityAdapter = new CityAdapter(this, itemsList);
        cityAdapter.setOnItemClickListener((position, v) -> {
            SharedHelper.putKey(SelectCityActivity.this, "current_city", itemsList.get(position).toString());
            if (tag) {
                Intent mainIntent = new Intent(SelectCityActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
            } else
                setResult(RESULT_OK, new Intent());
            finish();
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getCityList();
    }

    public void getCityList() {

        customDialog = new CustomDialog(this);
        customDialog.show();

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(URLHelper.CITIES, response -> {

            Log.v("GetCityList", response.toString());

            try {
                JSONArray jsonArray = response.getJSONArray("cities");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    City city = new City(obj);
                    itemsList.add(city);
                }
                cityAdapter.setItemsList(itemsList);
                if (cityAdapter != null && cityAdapter.getItemCount() > 0) {
                    errorLayout.setVisibility(View.GONE);
                    recyclerView.setAdapter(cityAdapter);
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();

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
                            displayMessage(errorObj.optString("message"));
                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }

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
                    getCityList();
                }
            }
        });

        UserApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    public void displayMessage(String toastString) {
        try {
            Snackbar.make(errorLayout, toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(this, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                e.printStackTrace();
            }
        }
    }
}