package com.kwendaapp.rideo.Activities;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.kwendaapp.rideo.Constants.AutoCompleteAdapter;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.LocaleUtils;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Models.PlacePredictions;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.Retrofit.ApiInterface;
import com.kwendaapp.rideo.Retrofit.RetrofitClient;
import com.kwendaapp.rideo.UserApplication;
import com.kwendaapp.rideo.Utils.Utilities;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class AddHomeWorkActivity extends AppCompatActivity {

    private ListView mAutoCompleteList;
    private RecyclerView rvRecentResults;
    private EditText txtLocation;
    private PlacePredictions predictions = new PlacePredictions();
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private static final int MY_PERMISSIONS_REQUEST_LOC = 30;
    private final Utilities utils = new Utilities();
    private final PlacePredictions placePredictions = new PlacePredictions();
    private PlacesClient mGoogleApiClient;
    private Handler handler;
    double latitude;
    double longitude;
    private final String GETPLACESHIT = "places_hit";
    private ApiInterface mApiInterface;
    private String strTag = "";
    private CustomDialog customDialog;
    PlacesClient placesClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work);
        strTag = Objects.requireNonNull(getIntent().getExtras()).getString("tag");

        Places.initialize(getApplicationContext(), getString(R.string.google_map_api));
        placesClient = Places.createClient(this);


        init();
    }

    private void init() {
        rvRecentResults = findViewById(R.id.rvRecentResults);
        mAutoCompleteList = findViewById(R.id.searchResultLV);
        txtLocation = findViewById(R.id.txtLocation);
        ImageView backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(view -> onBackPressed());

        customDialog = new CustomDialog(AddHomeWorkActivity.this);

        getFavoriteLocations();

        mAutoCompleteList.setOnItemClickListener((parent, view, position, id) -> setGoogleAddress(position));

        //get permission for Android M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            fetchLocation();
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOC);
            } else {
                fetchLocation();
            }
        }
        //Add a text change listener to implement autocomplete functionality
        txtLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtLocation.getText().length() > 0) {
                    rvRecentResults.setVisibility(View.GONE);
                    if (mAutoCompleteAdapter == null)
                        mAutoCompleteList.setVisibility(View.VISIBLE);

                    Runnable run = () -> {
                        // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                        UserApplication.getInstance().cancelRequestInQueue(GETPLACESHIT);
                        JSONObject object = new JSONObject();
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(txtLocation.getText().toString()),
                                object, response -> {
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Gson gson = new Gson();
                                    predictions = gson.fromJson(response.toString(), PlacePredictions.class);
                                    if (mAutoCompleteAdapter == null) {
                                        mAutoCompleteAdapter = new AutoCompleteAdapter(AddHomeWorkActivity.this, predictions.getPlaces(), AddHomeWorkActivity.this);
                                        mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
                                    } else {
                                        mAutoCompleteList.setVisibility(View.VISIBLE);
                                        mAutoCompleteAdapter.clear();
                                        mAutoCompleteAdapter.addAll(predictions.getPlaces());
                                        mAutoCompleteAdapter.notifyDataSetChanged();
                                        mAutoCompleteList.invalidate();
                                    }
                                }, error -> Log.v("PayNowRequestResponse", error.toString()));
                        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
                    };

                    // only canceling the network calls will not help, you need to remove all callbacks as well
                    // otherwise the pending callbacks and messages will again invoke the handler and will send the request
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    } else {
                        handler = new Handler();
                    }
                    handler.postDelayed(run, 50);

                } else {
                    mAutoCompleteList.setVisibility(View.GONE);
                    rvRecentResults.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

    }

    public String getPlaceAutoCompleteUrl(String input) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/autocomplete/json");
        urlString.append("?input=");
        try {
            urlString.append(URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&location=");
        urlString.append(latitude).append(",").append(longitude); // append lat long of current location to show nearby results.
        urlString.append("&radius=500&language=en");
        urlString.append("&key=").append(getResources().getString(R.string.google_map_api));

        Log.d("FINAL URL:::   ", urlString.toString());
        return urlString.toString();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new PlacesClient() {
            @NotNull
            @Override
            public Task<FindAutocompletePredictionsResponse> findAutocompletePredictions(@NonNull FindAutocompletePredictionsRequest findAutocompletePredictionsRequest) {
                return null;
            }

            @NonNull
            @Override
            public Task<FetchPhotoResponse> fetchPhoto(@NonNull FetchPhotoRequest fetchPhotoRequest) {
                return null;
            }

            @NonNull
            @Override
            public Task<FetchPlaceResponse> fetchPlace(@NonNull FetchPlaceRequest fetchPlaceRequest) {
                return null;
            }

            @NonNull
            @Override
            public Task<FindCurrentPlaceResponse> findCurrentPlace(@NonNull FindCurrentPlaceRequest findCurrentPlaceRequest) {
                ActivityCompat.checkSelfPermission(AddHomeWorkActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                return null;
            }
        };

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOC) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(this, "Please grant permission for using this app!", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void fetchLocation() {
        buildGoogleApiClient();
    }

    void setAddress() {
        utils.hideKeypad(AddHomeWorkActivity.this, getCurrentFocus());
        new Handler().postDelayed(() -> {
            Intent intent = new Intent();
            if (placePredictions != null) {
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
            finish();
        }, 500);
    }




    private class RecentPlacesAdapter extends RecyclerView.Adapter<RecentPlacesAdapter.MyViewHolder> {
        JSONArray jsonArray;

        public RecentPlacesAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        @NotNull
        @Override
        public RecentPlacesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.autocomplete_row, parent, false);
            return new RecentPlacesAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NotNull RecentPlacesAdapter.MyViewHolder holder, int position) {
            String[] name = jsonArray.optJSONObject(position).optString("address").split(",");
            if (name.length > 0) {
                holder.name.setText(name[0]);
            } else {
                holder.name.setText(jsonArray.optJSONObject(position).optString("address"));
            }
            holder.location.setText(jsonArray.optJSONObject(position).optString("address"));

            holder.imgRecent.setImageResource(R.drawable.recent_search);

            holder.lnrLocation.setTag(position);

            holder.lnrLocation.setOnClickListener(view -> {
                int pos = Integer.parseInt(view.getTag().toString());
                AddToHomeWork(strTag, jsonArray.optJSONObject(pos).optString("latitude"), jsonArray.optJSONObject(pos).optString("longitude"),
                        jsonArray.optJSONObject(pos).optString("address"));
            });
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name, location;

            LinearLayout lnrLocation;

            ImageView imgRecent;

            public MyViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.place_name);
                location = itemView.findViewById(R.id.place_detail);
                lnrLocation = itemView.findViewById(R.id.lnrLocation);
                imgRecent = itemView.findViewById(R.id.imgRecent);
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getFavoriteLocations() {
        mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        Call<ResponseBody> call = mApiInterface.getFavoriteLocations("XMLHttpRequest",
                SharedHelper.getKey(AddHomeWorkActivity.this, "token_type") + " " + SharedHelper.getKey(AddHomeWorkActivity.this, "access_token"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull retrofit2.Response<ResponseBody> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("sUCESS", "bodyString" + bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            JSONArray homeArray = jsonObj.optJSONArray("home");
                            JSONArray workArray = jsonObj.optJSONArray("work");
                            JSONArray othersArray = jsonObj.optJSONArray("others");
                            JSONArray recentArray = jsonObj.optJSONArray("recent");
                            assert homeArray != null;
                            if (homeArray.length() > 0) {
                                Log.v("Home Address", "" + homeArray);
                                SharedHelper.putKey(AddHomeWorkActivity.this, "home", homeArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(AddHomeWorkActivity.this, "home_lat", homeArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(AddHomeWorkActivity.this, "home_lng", homeArray.optJSONObject(0).optString("longitude"));
                                SharedHelper.putKey(AddHomeWorkActivity.this, "home_id", homeArray.optJSONObject(0).optString("id"));
                            }
                            assert workArray != null;
                            if (workArray.length() > 0) {
                                Log.v("Work Address", "" + workArray);
                                SharedHelper.putKey(AddHomeWorkActivity.this, "work", workArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(AddHomeWorkActivity.this, "work_lat", workArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(AddHomeWorkActivity.this, "work_lng", workArray.optJSONObject(0).optString("longitude"));
                                SharedHelper.putKey(AddHomeWorkActivity.this, "work_id", workArray.optJSONObject(0).optString("id"));
                            }
                            assert othersArray != null;
                            if (othersArray.length() > 0) {
                                Log.v("Others Address", "" + othersArray);
                            }
                            assert recentArray != null;
                            if (recentArray.length() > 0) {
                                Log.v("Recent Address", "" + recentArray);
                                rvRecentResults.setVisibility(View.VISIBLE);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                rvRecentResults.setLayoutManager(mLayoutManager);
                                rvRecentResults.setItemAnimator(new DefaultItemAnimator());
                                RecentPlacesAdapter recentPlacesAdapter = new RecentPlacesAdapter(recentArray);
                                rvRecentResults.setAdapter(recentPlacesAdapter);
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
            }
        });
    }


    private void AddToHomeWork(String strType, String strLatitude, String strLongitude, String strAddress) {
        mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        customDialog.show();

        Call<ResponseBody> call = mApiInterface.updateFavoriteLocations("XMLHttpRequest", SharedHelper.getKey(AddHomeWorkActivity.this, "token_type") + " " + SharedHelper.getKey(AddHomeWorkActivity.this, "access_token")
                , strType, strLatitude, strLongitude, strAddress);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull retrofit2.Response<ResponseBody> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                customDialog.dismiss();
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("sUCESS", "bodyString" + bodyString);
                        setAddress();
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

    private void setGoogleAddress(int position) {
        if (mGoogleApiClient != null) {


            Utilities.print("", "Place ID == >" + predictions.getPlaces().get(position).getPlaceID());
            List<Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Field.LAT_LNG, Field.ADDRESS, Field.ADDRESS_COMPONENTS);
            FetchPlaceRequest request = FetchPlaceRequest.builder(predictions.getPlaces().get(position).getPlaceID(), placeFields)
                    .build();

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place myPlace = response.getPlace();
                Log.e("Place", "Place found: " + myPlace.getAddress());

                LatLng queriedLocation = myPlace.getLatLng();
                assert queriedLocation != null;
                Log.v("Latitude is", "" + queriedLocation.latitude);
                Log.v("Longitude is", "" + queriedLocation.longitude);
                placePredictions.strDestAddress = myPlace.getName() + ", "+myPlace.getAddress();
                placePredictions.strDestLatLng = myPlace.getLatLng().toString();
                placePredictions.strDestLatitude = myPlace.getLatLng().latitude + "";
                placePredictions.strDestLongitude = myPlace.getLatLng().longitude + "";

                AddToHomeWork(strTag, placePredictions.strDestLatitude,
                        placePredictions.strDestLongitude, placePredictions.strDestAddress);

                mAutoCompleteList.setVisibility(View.GONE);


            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    utils.showAlert(this, "getPlaceById Error, " + apiException.getLocalizedMessage());

                }
            });


        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }


}
