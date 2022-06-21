package com.kwendaapp.rideo.Activities;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Models.PlacePredictions;
import com.kwendaapp.rideo.Models.RecentAddressData;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class CustomGooglePlacesSearch extends AppCompatActivity  {
    double latitude;
    double longitude;
    private ListView mAutoCompleteList;
    private EditText txtDestination, txtaddressSource;
    private final String GETPLACESHIT = "places_hit";
    private PlacePredictions predictions = new PlacePredictions();
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private static final int MY_PERMISSIONS_REQUEST_LOC = 30;
    private Handler handler;
    private TextView txtPickLocation;
    private final Utilities utils = new Utilities();
    private ImageView imgDestClose;
    private ImageView imgSourceClose;
    private LinearLayout lnrFavorite;
    private Activity thisActivity;
    private String strSelected = "";
    private final PlacePredictions placePredictions = new PlacePredictions();
    private TextView txtHomeLocation, txtWorkLocation;
    private final int UPDATE_HOME_WORK = 1;
    private final ArrayList<RecentAddressData> lstRecentList = new ArrayList<>();
    private PlacesClient placesClient = null;
    private PlacesClient mGoogleApiClient;
    private RecyclerView rvRecentResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_soruce_and_destination);
        thisActivity = this;

//        changeStatusBarColor();

        Places.initialize(getApplicationContext(), getString(R.string.google_map_api));
        placesClient = Places.createClient(this);

        txtDestination = findViewById(R.id.txtDestination);
        txtaddressSource = findViewById(R.id.txtaddressSource);

        ImageView backArrow = findViewById(R.id.backArrow);
        imgDestClose = findViewById(R.id.imgDestClose);
        imgSourceClose = findViewById(R.id.imgSourceClose);

        txtPickLocation = findViewById(R.id.txtPickLocation);
        txtWorkLocation = findViewById(R.id.txtWorkLocation);
        txtHomeLocation = findViewById(R.id.txtHomeLocation);

        lnrFavorite = findViewById(R.id.lnrFavorite);
        LinearLayout lnrHome = findViewById(R.id.lnrHome);
        LinearLayout lnrWork = findViewById(R.id.lnrWork);

        RelativeLayout rytAddressSource = findViewById(R.id.rytAddressSource);

        rvRecentResults = findViewById(R.id.rvRecentResults);
        mAutoCompleteList = findViewById(R.id.searchResultLV);

        String cursor = Objects.requireNonNull(getIntent().getExtras()).getString("cursor");
        String s_address = getIntent().getExtras().getString("s_address");
        String d_address = getIntent().getExtras().getString("d_address");
        Log.e("CustomGoogleSearch", "onCreate: source " + s_address);
        Log.e("CustomGoogleSearch", "onCreate: destination" + d_address);
        txtaddressSource.setText(s_address);

        if (d_address != null && !d_address.equalsIgnoreCase("")) {
            txtDestination.setText(d_address);
        }

        assert cursor != null;
        if (cursor.equalsIgnoreCase("source")) {
            strSelected = "source";
//            txtaddressSource.requestFocus();
            imgSourceClose.setVisibility(View.VISIBLE);
            imgDestClose.setVisibility(View.GONE);
        } else {
//            txtDestination.requestFocus();
            strSelected = "destination";
            imgDestClose.setVisibility(View.VISIBLE);
            imgSourceClose.setVisibility(View.GONE);
        }

        String strStatus = SharedHelper.getKey(thisActivity, "req_status");

        if (strStatus.equalsIgnoreCase("PICKEDUP")) {
            if (SharedHelper.getKey(thisActivity, "track_status").equalsIgnoreCase("YES")) {
                rytAddressSource.setVisibility(View.GONE);
            } else {
                rytAddressSource.setVisibility(View.VISIBLE);
            }
        }


        txtaddressSource.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                strSelected = "source";
                imgSourceClose.setVisibility(View.VISIBLE);
            } else {
                imgSourceClose.setVisibility(View.GONE);
            }
        });

        txtDestination.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                strSelected = "destination";
                imgDestClose.setVisibility(View.VISIBLE);
            } else {
                imgDestClose.setVisibility(View.GONE);
            }
        });

        getFavoriteLocations();
        imgDestClose.setOnClickListener(v -> {
            txtDestination.setText("");
            mAutoCompleteList.setVisibility(View.GONE);
            imgDestClose.setVisibility(View.GONE);
            txtDestination.requestFocus();
        });

        imgSourceClose.setOnClickListener(v -> {
            txtaddressSource.setText("");
            mAutoCompleteList.setVisibility(View.GONE);
            imgSourceClose.setVisibility(View.GONE);
            txtaddressSource.requestFocus();
        });

        txtPickLocation.setOnClickListener(v -> {
            utils.hideKeypad(thisActivity, thisActivity.getCurrentFocus());
            new Handler().postDelayed(() -> {
                Intent intent = new Intent();
                intent.putExtra("pick_location", "yes");
                intent.putExtra("type", strSelected);
                setResult(RESULT_OK, intent);
                finish();
            }, 500);
        });

        lnrHome.setOnClickListener(view -> {
            if (SharedHelper.getKey(CustomGooglePlacesSearch.this, "home").equalsIgnoreCase("")) {
                gotoHomeWork("home");
            } else {
                if (strSelected.equalsIgnoreCase("destination")) {
                    placePredictions.strDestAddress = SharedHelper.getKey(CustomGooglePlacesSearch.this, "home");
                    placePredictions.strDestLatitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "home_lat");
                    placePredictions.strDestLongitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "home_lng");
                    LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strDestLatitude), Double.parseDouble(placePredictions.strDestLatitude));
                    placePredictions.strDestLatLng = "" + latlng;
                    txtDestination.setText(SharedHelper.getKey(CustomGooglePlacesSearch.this, "home"));
                    txtDestination.setSelection(0);
                } else {
                    placePredictions.strSourceAddress = SharedHelper.getKey(CustomGooglePlacesSearch.this, "home");
                    placePredictions.strSourceLatitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "home_lat");
                    placePredictions.strSourceLongitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "home_lng");
                    LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strSourceLatitude), Double.parseDouble(placePredictions.strSourceLongitude));
                    placePredictions.strSourceLatLng = "" + latlng;
                    txtaddressSource.setText(placePredictions.strSourceAddress);
                    txtaddressSource.setSelection(0);
                    txtDestination.requestFocus();
                    mAutoCompleteAdapter = null;
                }
                setAddress();
            }
        });

        lnrWork.setOnClickListener(view -> {
            if (SharedHelper.getKey(CustomGooglePlacesSearch.this, "work").equalsIgnoreCase("")) {
                gotoHomeWork("work");
            } else {
                if (strSelected.equalsIgnoreCase("destination")) {
                    placePredictions.strDestAddress = SharedHelper.getKey(CustomGooglePlacesSearch.this, "work");
                    placePredictions.strDestLatitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "work_lat");
                    placePredictions.strDestLongitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "work_lng");
                    LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strDestLatitude), Double.parseDouble(placePredictions.strDestLatitude));
                    placePredictions.strDestLatLng = "" + latlng;
                    txtDestination.setText(SharedHelper.getKey(CustomGooglePlacesSearch.this, "work"));
                    txtDestination.setSelection(0);
                } else {
                    placePredictions.strSourceAddress = SharedHelper.getKey(CustomGooglePlacesSearch.this, "work");
                    placePredictions.strSourceLatitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "work_lat");
                    placePredictions.strSourceLongitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "work_lng");
                    LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strSourceLatitude), Double.parseDouble(placePredictions.strSourceLongitude));
                    placePredictions.strSourceLatLng = "" + latlng;
                    txtaddressSource.setText(placePredictions.strSourceAddress);
                    txtaddressSource.setSelection(0);
                    txtDestination.requestFocus();
                    mAutoCompleteAdapter = null;
                }
                setAddress();
            }
        });

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

        txtDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgDestClose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                imgDestClose.setVisibility(View.VISIBLE);
                strSelected = "destination";
                if (txtDestination.getText().length() > 0) {
                    lnrFavorite.setVisibility(View.GONE);
                    txtPickLocation.setVisibility(View.VISIBLE);
                    imgDestClose.setVisibility(View.VISIBLE);
                    txtPickLocation.setText(getString(R.string.pin_location));
                    Runnable run = () -> {
                        UserApplication.getInstance().cancelRequestInQueue(GETPLACESHIT);
                        JSONObject object = new JSONObject();
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(txtDestination.getText().toString()), object, response -> {
                            Log.v("PayNowRequestResponse", response.toString());
                            Log.v("PayNowRequestResponse", response.toString());
                            Gson gson = new Gson();
                            predictions = gson.fromJson(response.toString(), PlacePredictions.class);
                            if (mAutoCompleteAdapter == null) {
                                mAutoCompleteList.setVisibility(View.VISIBLE);
                                mAutoCompleteAdapter = new AutoCompleteAdapter(CustomGooglePlacesSearch.this, predictions.getPlaces(), CustomGooglePlacesSearch.this);
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

                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    } else {
                        handler = new Handler();
                    }
                    handler.postDelayed(run, 10);

                } else {
                    lnrFavorite.setVisibility(View.VISIBLE);
                    mAutoCompleteList.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imgDestClose.setVisibility(View.VISIBLE);
            }

        });

        txtaddressSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgSourceClose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strSelected = "source";
                if (txtaddressSource.getText().length() > 0) {
                    lnrFavorite.setVisibility(View.GONE);
                    txtPickLocation.setVisibility(View.VISIBLE);
                    imgSourceClose.setVisibility(View.VISIBLE);
                    txtPickLocation.setText(getString(R.string.pin_location));
                    if (mAutoCompleteAdapter == null)
                        mAutoCompleteList.setVisibility(View.VISIBLE);
                    Runnable run = () -> {
                        // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                        UserApplication.getInstance().cancelRequestInQueue(GETPLACESHIT);

                        JSONObject object = new JSONObject();
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(txtaddressSource.getText().toString()),
                                object, response -> {
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Gson gson = new Gson();
                                    predictions = gson.fromJson(response.toString(), PlacePredictions.class);
                                    if (mAutoCompleteAdapter == null) {
                                        mAutoCompleteAdapter = new AutoCompleteAdapter(CustomGooglePlacesSearch.this, predictions.getPlaces(), CustomGooglePlacesSearch.this);
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
                    handler.postDelayed(run, 100);

                } else {
                    lnrFavorite.setVisibility(View.VISIBLE);
                    mAutoCompleteList.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imgSourceClose.setVisibility(View.VISIBLE);
            }

        });

        txtDestination.setSelection(txtDestination.getText().length());

        mAutoCompleteList.setOnItemClickListener((parent, view, position, id) -> {
            if (txtaddressSource.getText().toString().equalsIgnoreCase("")) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                    builder.setMessage("Please choose pickup location")
                            .setTitle(thisActivity.getString(R.string.app_name))
                            .setCancelable(true)
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("OK", (dialog, id1) -> {
                                txtaddressSource.requestFocus();
                                txtDestination.setText("");
                                imgDestClose.setVisibility(View.GONE);
                                mAutoCompleteList.setVisibility(View.GONE);
                                dialog.dismiss();
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                setGoogleAddress(position);
            }
        });
        backArrow.setOnClickListener(v -> finish());

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setStatusBarColor(getResources().getColor(R.color.semiTransparentColor));
        }
    }

    private void getFavoriteLocations() {
        ApiInterface mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        Call<ResponseBody> call = mApiInterface.getFavoriteLocations("XMLHttpRequest",
                SharedHelper.getKey(CustomGooglePlacesSearch.this, "token_type") + " " + SharedHelper.getKey(CustomGooglePlacesSearch.this, "access_token"));
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
                                txtHomeLocation.setText(homeArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(CustomGooglePlacesSearch.this, "home", homeArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(CustomGooglePlacesSearch.this, "home_lat", homeArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(CustomGooglePlacesSearch.this, "home_lng", homeArray.optJSONObject(0).optString("longitude"));
                                SharedHelper.putKey(CustomGooglePlacesSearch.this, "home_id", homeArray.optJSONObject(0).optString("id"));
                            } else {
                                txtHomeLocation.setText(getResources().getString(R.string.add_home_location));
                            }
                            assert workArray != null;
                            if (workArray.length() > 0) {
                                Log.v("Work Address", "" + workArray);
                                txtWorkLocation.setText(workArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(CustomGooglePlacesSearch.this, "work", workArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(CustomGooglePlacesSearch.this, "work_lat", workArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(CustomGooglePlacesSearch.this, "work_lng", workArray.optJSONObject(0).optString("longitude"));
                                SharedHelper.putKey(CustomGooglePlacesSearch.this, "work_id", workArray.optJSONObject(0).optString("id"));
                            } else {
                                txtWorkLocation.setText(getResources().getString(R.string.add_work_location));
                            }
                            assert othersArray != null;
                            if (othersArray.length() > 0) {
                                Log.v("Others Address", "" + othersArray);
                            }
                            assert recentArray != null;
                            if (recentArray.length() > 0) {
                                for (int i = 0; i < recentArray.length(); i++) {
                                    RecentAddressData recentAddressData = new RecentAddressData();
                                    JSONObject jsonObject = recentArray.optJSONObject(i);
                                    recentAddressData.id = jsonObject.optInt("id");
                                    recentAddressData.userId = jsonObject.optInt("user_id");
                                    recentAddressData.address = jsonObject.optString("address");
                                    recentAddressData.type = jsonObject.optString("type");
                                    recentAddressData.latitude = jsonObject.optDouble("latitude");
                                    recentAddressData.longitude = jsonObject.optDouble("longitude");
                                    if (recentAddressData.address != null && !recentAddressData.address.equalsIgnoreCase("")) {
                                        lstRecentList.add(recentAddressData);
                                    }
                                }

                                HashSet hs = new HashSet(lstRecentList);
                                lstRecentList.clear();
                                lstRecentList.addAll(hs);
                                Log.v("Recent Address", "" + recentArray);
                                rvRecentResults.setVisibility(View.VISIBLE);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                rvRecentResults.setLayoutManager(mLayoutManager);
                                rvRecentResults.setItemAnimator(new DefaultItemAnimator());
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

    private void setGoogleAddress(int position) {
        if (mGoogleApiClient != null) {
            Utilities.print("GooglePlacesSearch", "Place ID == >" + predictions.getPlaces().get(position).getPlaceID());
            List<Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Field.LAT_LNG, Field.ADDRESS, Field.ADDRESS_COMPONENTS);
            FetchPlaceRequest request = FetchPlaceRequest.builder(predictions.getPlaces().get(position).getPlaceID(), placeFields)
                    .build();

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place myPlace = response.getPlace();
                Log.e("Place", "Place found: " + myPlace.getAddress());


                LatLng queriedLocation = myPlace.getLatLng();
                assert queriedLocation != null;
                Log.e("Latitude is", "" + queriedLocation.latitude);
                Log.e("Longitude is", "" + queriedLocation.longitude);
                if (strSelected.equalsIgnoreCase("destination")) {
                    placePredictions.strDestAddress =myPlace.getName() + ", "+myPlace.getAddress();
                    placePredictions.strDestLatLng = myPlace.getLatLng().toString();
                    placePredictions.strDestLatitude = myPlace.getLatLng().latitude + "";
                    placePredictions.strDestLongitude = myPlace.getLatLng().longitude + "";
                    txtDestination.setText(placePredictions.strDestAddress);
                    txtDestination.setSelection(0);
                } else {
                    placePredictions.strSourceAddress =myPlace.getName() + ", "+myPlace.getAddress();
                    placePredictions.strSourceLatLng = myPlace.getLatLng().toString();
                    placePredictions.strSourceLatitude = myPlace.getLatLng().latitude + "";
                    placePredictions.strSourceLongitude = myPlace.getLatLng().longitude + "";
                    txtaddressSource.setText(placePredictions.strSourceAddress);
                    txtaddressSource.setSelection(0);
                    txtDestination.requestFocus();
                    mAutoCompleteAdapter = null;
                }



                mAutoCompleteList.setVisibility(View.GONE);

                if (txtDestination.getText().toString().length() > 0) {
                    if (strSelected.equalsIgnoreCase("destination")) {
                        if (!placePredictions.strDestAddress.equalsIgnoreCase(placePredictions.strSourceAddress)) {
                            setAddress();
                        } else {
                            utils.showAlert(thisActivity, "Source and Destination address should not be same!");
                        }
                    }
                } else {
                    txtDestination.requestFocus();
                    txtDestination.setText("");
                    imgDestClose.setVisibility(View.GONE);
                    mAutoCompleteList.setVisibility(View.GONE);
                }


            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    utils.showAlert(thisActivity, "getPlaceById Error, " + apiException.getLocalizedMessage());

                }
            });
        }
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
                ActivityCompat.checkSelfPermission(CustomGooglePlacesSearch.this, Manifest.permission.ACCESS_FINE_LOCATION);
                return null;
            }
        };

    }


    public void fetchLocation() {
        //Build google API client to use fused location
        buildGoogleApiClient();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOC) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted!
                fetchLocation();
            } else {
                // permission denied!
                Toast.makeText(this, "Please grant permission for using this app!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setAddress();
        super.onBackPressed();
    }

    void setAddress() {
        utils.hideKeypad(thisActivity, getCurrentFocus());
        new Handler().postDelayed(() -> {
            Intent intent = new Intent();
            if (placePredictions != null) {
                intent.putExtra("Location Address", placePredictions);
                intent.putExtra("pick_location", "no");
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
            finish();
        }, 500);
    }




    private void gotoHomeWork(String strTag) {
        Intent intentHomeWork = new Intent(CustomGooglePlacesSearch.this, AddHomeWorkActivity.class);
        intentHomeWork.putExtra("tag", strTag);
        startActivityForResult(intentHomeWork, UPDATE_HOME_WORK);
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

}
