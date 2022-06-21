package com.kwendaapp.rideo.Fragments;

import static java.lang.Double.parseDouble;
import static java.lang.Math.abs;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

import android.location.LocationListener;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.kwendaapp.rideo.Activities.MainActivity;
import com.kwendaapp.rideo.Activities.SelectCityActivity;
import com.kwendaapp.rideo.Constants.Constants;
import com.kwendaapp.rideo.Models.City;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;
import com.kwendaapp.rideo.Activities.ActivitySettings;
import com.kwendaapp.rideo.Activities.ActivityWallet;
import com.kwendaapp.rideo.Activities.CustomGooglePlacesSearch;
import com.kwendaapp.rideo.Activities.HistoryActivity;
import com.kwendaapp.rideo.Activities.ShowProfile;
import com.kwendaapp.rideo.Activities.newlivechat;
import com.kwendaapp.rideo.Constants.AutoCompleteAdapter;
import com.kwendaapp.rideo.Helper.ConnectionHelper;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.DataParser;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Helper.SharedHelperJSONarray;
import com.kwendaapp.rideo.Helper.URLHelper;
import com.kwendaapp.rideo.Models.CardInfo;
import com.kwendaapp.rideo.Models.Driver;
import com.kwendaapp.rideo.Models.Errorresponse;
import com.kwendaapp.rideo.Models.PlacePredictions;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.Retrofit.ApiInterface;
import com.kwendaapp.rideo.Retrofit.ResponseListener;
import com.kwendaapp.rideo.Retrofit.RetrofitClient;
import com.kwendaapp.rideo.UserApplication;
import com.kwendaapp.rideo.Utils.MapAnimator;
import com.kwendaapp.rideo.Utils.MapRipple;
import com.kwendaapp.rideo.Utils.MyButton;
import com.kwendaapp.rideo.Utils.Utilities;
import com.wangsun.upi.payment.UpiPayment;
import com.wangsun.upi.payment.model.PaymentDetail;
import com.wangsun.upi.payment.model.TransactionDetails;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener, ResponseListener, GoogleMap.OnCameraMoveListener, PaymentResultListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static PayPalConfiguration config;
    private String PaymentType;
    private TabLayout tab_layout;
    private Activity activity;
    private Context context;
    View rootView;
    HomeFragmentListener listener;
    double wallet_balance;
    LayoutInflater inflater;
    AlertDialog reasonDialog;
    AlertDialog cancelRideDialog;
    String is_track = "";
    String strTimeTaken = "";
    long strTimeTakenValue;
    Button btnHome, btnWork;
    LinearLayout nocarlayout;
    EditText specialNote;
    CardView payment_method_card;
    ImageView payment_method_card_close;
    ImageView cancel_estimated, cancel_coupon, cancel_promo;
    PlacesClient placesClient = null;
    private PlacesClient mGoogleApiClient;
    int TABVAL = 0;
    private JSONArray serviceresponse = null;
    private JSONArray filteredserviceresponse = new JSONArray();
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback mlocationCallback;
    private boolean isParcel = false;
    DecimalFormat df = new DecimalFormat("#.##");

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private int chksTime = 3000;

    public boolean onBackPressed() {
        if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
            flowValue = 0;
            if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                LatLng myLocation = new LatLng(parseDouble(current_lat), parseDouble(current_lng));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
            flowValue = 1;
            layoutChanges();
            return false;
        } else if (applycouponlayout.getVisibility() == View.VISIBLE) {
            applycouponlayout.startAnimation(slide_down);
            return false;
        } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
            flowValue = 1;
            layoutChanges();
            return false;
        } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
            flowValue = 1;
            layoutChanges();
            return false;
        }
        if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
//                        ((MainActivity) context).refreshView();
            mAutoCompleteAdapter = null;
            flowValue = 0;
            layoutChanges();
            return false;
        } else if (rtlStaticMarker.getVisibility() == View.VISIBLE) {
            flowValue = 0;
            layoutChanges();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onCameraMove() {
        TextView SetLocationText = rootView.findViewById(R.id.SetLocationText);
        Utilities.print("Current marker", "Zoom Level " + mMap.getCameraPosition().zoom);
        cmPosition = mMap.getCameraPosition();
        if (marker != null) {
            if (!mMap.getProjection().getVisibleRegion().latLngBounds.contains(marker.getPosition())) {
                Utilities.print("Current marker", "Current Marker is not visible");
                if (mapfocus.getVisibility() == View.INVISIBLE) {
                    mapfocus.setVisibility(View.VISIBLE);
                }
            } else {
                Utilities.print("Current marker", "Current Marker is visible");
                if (mapfocus.getVisibility() == View.VISIBLE) {
                    mapfocus.setVisibility(View.INVISIBLE);
                }
                if (mMap.getCameraPosition().zoom < 16.0f) {
                    if (mapfocus.getVisibility() == View.INVISIBLE) {
                        mapfocus.setVisibility(View.VISIBLE);
                    }
                }
            }

//            String setlocationtext = Utilities.getAddressUsingLatLng("setlocationtext", SetLocationText, context, "" + cmPosition.target.latitude, "" + cmPosition.target.longitude);

        }
    }

    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public interface HomeFragmentListener {
    }

    private String isPaid = "", paymentMode = "";
    private int totalRideAmount = 0, walletAmountDetected = 0, couponAmountDetected = 0;
    private final Utilities utils = new Utilities();
    private int flowValue = 0;
    private DrawerLayout drawer;
    private int NAV_DRAWER = 0;
    private String reqStatus = "";
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST = 18945;
    private static final int REQUEST_LOCATION = 1450;
    private String feedBackRating;
    private final ArrayList<CardInfo> cardInfoArrayList = new ArrayList<>();
    double height;
    double width;
    public String PreviousStatus = "";
    public String CurrentStatus = "";
    private Handler handleCheckStatus;
    private String strPickLocation = "";
    private String strPickType = "";
    private Driver driver;
    private RelativeLayout bottom_sheet;
    private BottomSheetBehavior bottomSheetBehavior;

    private GoogleMap mMap;
    private int value;
    private Marker marker;
    private City city;

    //Places Search
    private String strSelected = "destination";
    private final String GETPLACESHIT = "places_hit";
    double Searchlatitude;
    double Searchlongitude;
    private ListView mAutoCompleteList;
    private ImageView imgSourceClose, imgDestinationClose;
    private EditText SearchSource, SearchDestination;
    private LinearLayout lnrFavorite, txtPickLocation;
    private PlacePredictions predictions = new PlacePredictions();
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private final PlacePredictions placePredictions = new PlacePredictions();

    //        <!-- Source and Destination Layout-->
    private LinearLayout sourceAndDestinationLayout;
    private LinearLayout main_card_layout, place_search_layout, lnrparcel;
    private FrameLayout frmDestination;
    private TextView destination;
    private ImageView imgMenu, mapfocus, imgBack, shadowBack;


    //ImageView destinationBorderImg;
    private TextView frmSource, frmDest;
    private CardView srcDestLayout;

    //<!--1. Request to providers -->
    LinearLayout lnrRequestProviders;
    RecyclerView rcvServiceTypes;
    ImageView imgPaymentType;
    ImageView imgSos;
    ImageView imgShareRide;
    TextView lblPaymentType, lblPaymentChange, booking_id;
    //TextView btnRequestRides;
    String scheduledDate = "";
    String scheduledTime = "";
    String cancalReason = "";

    // <!--1. Driver Details-->

    LinearLayout lnrHidePopup, lnrProviderPopup, lnrPriceBase, lnrPricemin, lnrPricekm;
    RelativeLayout lnrSearchAnimation;
    ImageView imgProviderPopup;
    TextView lblPriceMin, lblBasePricePopup, lblCapacity, lblServiceName, lblPriceKm, lblCalculationType, lblProviderDesc;
    TextView lbDistanceCovered;
    TextView lblTimeTakenPrice;
    MyButton btnDonePopup;

    LinearLayout applycouponlayout;

    //<!--2. Approximate Rate ...-->
    EditText coupon_et;
    Button apply_button;
    TextView coupon_text;

    LinearLayout lnrApproximate;
    Button btnRequestRideConfirm;
    ImageView imgSchedule;
    SwitchMaterial chkWallet, chkReturn;
    TextView lblEta;
    TextView lblType;
    TextView lblApproxAmount, surgeTxt, lblapproxtoal, lblapproxtax, lblapproxdistance;

    LinearLayout ScheduleLayout;
    TextView scheduleDate;
    TextView scheduleTime;
    MyButton scheduleBtn;
    DatePickerDialog datePickerDialog;

//         <!--3. Waiting For Providers ...-->

    RelativeLayout lnrWaitingForProviders;
    TextView lblNoMatch;
    MyButton btnCancelRide;
    TextView addwallet;
    private boolean mIsShowing;
    private boolean mIsHiding;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

//         <!--4. Driver Accepted ...-->

    LinearLayout lnrProviderAccepted, lnrAfterAcceptedStatus, AfterAcceptButtonLayout;
    ImageView imgProvider, imgServiceRequested;
    TextView lblProvider, lblStatus, lblServiceRequested, lblModelNumber, lblModelNumber2, lblSurgePrice, lblOTP;
    LinearLayout OTPLayout;
    RatingBar ratingProvider;
    ImageView btnCall, btnsms, btnCancelTrip;
    TextView btnCancelTriptext;

//          <!--5. Invoice Layout ...-->

    LinearLayout lnrInvoice;
    TextView lblBasePrice, lblExtraPrice, lblTimeTaken, lblDisPrice, lblDistancePrice, lblTaxPrice, lblTimePrice, lblTotalPrice, lblPaymentTypeInvoice, lblDiscountPrice, lblWalletPrice;
    ImageView imgPaymentTypeInvoice;
    MyButton btnPayNow, btnPayNow_online;
    MyButton btnPaymentDoneBtn;
    LinearLayout discountDetectionLayout, walletDetectionLayout;
    LinearLayout bookingIDLayout;

//          <!--6. Rate provider Layout ...-->

    LinearLayout lnrRateProvider;
    TextView lblProviderNameRate;
    ImageView imgProviderRate;
    RatingBar ratingProviderRate;
    EditText txtCommentsRate;
    Button btnSubmitReview;
    TextView dh_source, dh_destination;

//            <!-- Static marker-->

    RelativeLayout rtlStaticMarker;
    ImageView imgDestination;
    MyButton btnDone;
    CameraPosition cmPosition;


    String current_lat = "", current_lng = "", current_address = "", source_lat = "", source_lng = "", source_address = "",
            dest_lat = "", dest_lng = "", dest_address = "", extend_dest_lat = "", extend_dest_lng = "", extend_dest_address = "";

    //Internet
    ConnectionHelper helper;
    Boolean isInternet;
    //RecylerView
    int currentPostion = 0;
    CustomDialog customDialog;

    //MArkers
    Marker availableProviders;
    TextView greetingtext;
    private LatLng sourceLatLng;
    private LatLng destLatLng;
    private Marker sourceMarker;
    private Marker destinationMarker;
    private Marker providerMarker;

    String strDis = "";

    ArrayList<LatLng> points = new ArrayList<LatLng>();
    HashMap<String, Marker> lstProviderMarkers = new HashMap<String, Marker>();
    private final HashMap<Integer, Marker> mHashMap = new HashMap<Integer, Marker>();
    AlertDialog alert;
    String previousTag = "";
    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down;

    ParserTask parserTask;
    String notificationTxt;
    boolean scheduleTrip = false;

    LinearLayout lnrHomeWork, lnrHome, lnrWork;
    TextView lnrhometxt, lnrworktxt;


    MapRipple mapRipple;
    boolean showFromGeo = true;
    LinearLayout destinationLayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            notificationTxt = bundle.getString("Notification");
            Log.e("HomeFragment", "onCreate : Notification" + notificationTxt);
            placePredictions.strDestAddress = bundle.getString("query", "");
            placePredictions.strDestLatitude = bundle.getString("lat", "");
            placePredictions.strDestLongitude = bundle.getString("lng", "");
            if (!placePredictions.strDestAddress.equals(""))
                showFromGeo = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }

        this.inflater = inflater;
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) this);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mlocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    onLocationChanged(location);
                }
            }
        };

        SharedHelper.putKey(context, "isSchedule", "No");
        customDialog = new CustomDialog(context);

        if (customDialog != null)
            customDialog.show();

        new Handler().postDelayed(() -> {
            init(rootView);
            //permission to access location

            Permissions.check(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, null, new PermissionHandler() {
                @Override
                public void onGranted() {

                    initMap();
                    MapsInitializer.initialize(getActivity());

                    tryToGetlastKnowLocation();
                }
            });


        }, 100);

        reqStatus = SharedHelper.getKey(context, "req_status");
        hideSystemUI();
        Places.initialize(getActivity().getApplicationContext(), getString(R.string.google_map_api));
        placesClient = Places.createClient(context);
        mGoogleApiClient = Places.createClient(context);
        return rootView;
    }

    private void hideSystemUI() {
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decorView = window.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        } else {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void whitestatusbarcolor(float color) {


        Window window = getActivity().getWindow();
        window.setStatusBarColor(getColorWithAlpha(Color.WHITE, color));
    }

    private int getColorWithAlpha(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }


    @SuppressLint("MissingPermission")
    private void tryToGetlastKnowLocation() {

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    // Got last known location. In some rare situations this can be null.

                    Log.e("flow", "fusedLocationClient");

                    if (location != null) {
                        // Logic to handle location object

                        Log.e("flow", "fusedLocationClient location found, call onLocationChanged");

                        onLocationChanged(location);


                    }
                });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            listener = (HomeFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HomeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    private void init(View rootView) {

        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        statusCheck();

        //Places Search

        mAutoCompleteList = rootView.findViewById(R.id.searchResultLV);
        SearchSource = rootView.findViewById(R.id.searchSource);
        SearchDestination = rootView.findViewById(R.id.searchDestionation);
        lnrFavorite = rootView.findViewById(R.id.lnrFavorite);
        txtPickLocation = rootView.findViewById(R.id.SearchPickLocation);
        imgSourceClose = rootView.findViewById(R.id.imgSourceClose);
        imgDestinationClose = rootView.findViewById(R.id.imgDestClose);


        //        <!-- Map frame -->
        LinearLayout mapLayout = rootView.findViewById(R.id.mapLayout);
        drawer = rootView.findViewById(R.id.drawer_layout);
        drawer = activity.findViewById(R.id.drawer_layout);
        greetingtext = activity.findViewById(R.id.greetingtext);
        specialNote = activity.findViewById(R.id.specialNote);
        payment_method_card = activity.findViewById(R.id.payment_method_card);
        payment_method_card_close = activity.findViewById(R.id.payment_method_card_close);

        payment_method_card_close.setOnClickListener(v -> {
            payment_method_card.startAnimation(slide_down);
            payment_method_card.setVisibility(View.GONE);
        });

        bottom_sheet = rootView.findViewById(R.id.bottom_sheet_main);
        ImageView view_search_close_btn = rootView.findViewById(R.id.view_search_close_btn);
        TextView view_search_done_btn = rootView.findViewById(R.id.view_search_done_btn);

        view_search_close_btn.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));

        view_search_done_btn.setOnClickListener(v -> {
            mMap.clear();
            flowValue = 1;
            layoutChanges();
            strPickLocation = "";
            strPickType = "";
            getServiceList();

            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(cmPosition.target.latitude,
                    cmPosition.target.longitude));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
            mMap.moveCamera(center);
            mMap.moveCamera(zoom);
        });

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    main_card_layout.setVisibility(View.GONE);
                    place_search_layout.setVisibility(View.VISIBLE);
                    lnrparcel.setVisibility(View.GONE);

                    //search location layout
                    setupSearchPart();

                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    Utilities.hideKeyboard(getActivity());
                    hideSystemUI();
                    main_card_layout.setVisibility(View.VISIBLE);
                    place_search_layout.setVisibility(View.GONE);
                    lnrparcel.setVisibility(View.VISIBLE);
                    isParcel = false;
                    //search location layout
                    SearchDestination.setText("");
                    imgDestinationClose.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                whitestatusbarcolor(slideOffset);
                main_card_layout.setVisibility(View.VISIBLE);
                place_search_layout.setVisibility(View.VISIBLE);

                float bottomtotop = Math.min(slideOffset * 1.6f, 1);
                float newslideoffset = slideOffset > 0.6f ? slideOffset : 0;
                float valup = newslideoffset != 0 ? 1 - (1 - newslideoffset) * 2.5f : 0;


                main_card_layout.setAlpha(1 - bottomtotop);
                place_search_layout.setAlpha(valup);
                lnrparcel.setAlpha(1 - bottomtotop);
            }
        });


        String statuso;
        if (Utilities.ismorning()) {
            statuso = getResources().getString(R.string.good_morning) + ", ";
        } else if (Utilities.isafternoon()) {
            statuso = getResources().getString(R.string.good_afternoon) + ", ";
        } else if (Utilities.isnight()) {
            statuso = getResources().getString(R.string.good_evening) + ", ";
        } else {
            statuso = getResources().getString(R.string.hello) + ", ";
        }
        greetingtext.setText(statuso + SharedHelper.getKey(context, "first_name") + " " + SharedHelper.getKey(context, ""));

        tab_layout = rootView.findViewById(R.id.tab_layout);
        nocarlayout = rootView.findViewById(R.id.nocarlayout);

        if (SharedHelper.getKey(context, "cat_app_ecomony").equals("1")) {
            tab_layout.addTab(tab_layout.newTab().setText("Transportation"));
        }

        if (SharedHelper.getKey(context, "cat_app_lux").equals("1")) {
            tab_layout.addTab(tab_layout.newTab().setText("Delivery"));
        }

        if (SharedHelper.getKey(context, "cat_app_ext").equals("1")) {
            tab_layout.addTab(tab_layout.newTab().setText("Truck"));
        }

        if (SharedHelper.getKey(context, "cat_app_out").equals("1")) {
            tab_layout.addTab(tab_layout.newTab().setText("OUTSTATION"));
        }

        tab_layout.setTabGravity(TabLayout.GRAVITY_FILL);

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getselectetedtab(tab.getText().toString());
                        break;
                    case 1:
                        getselectetedtab(tab.getText().toString());
                        break;
                    case 2:
                        getselectetedtab(tab.getText().toString());
                        break;
                    case 3:
                        getselectetedtab(tab.getText().toString());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        <!-- Source and Destination Layout-->
        sourceAndDestinationLayout = rootView.findViewById(R.id.sourceAndDestinationLayout);
        place_search_layout = rootView.findViewById(R.id.place_search_layout);
        main_card_layout = rootView.findViewById(R.id.main_card_layout);
        srcDestLayout = rootView.findViewById(R.id.sourceDestLayout);
        frmSource = rootView.findViewById(R.id.frmSource);
        frmDest = rootView.findViewById(R.id.frmDest);

        coupon_et = rootView.findViewById(R.id.coupon_et);
        apply_button = rootView.findViewById(R.id.apply_button);
        coupon_text = rootView.findViewById(R.id.coupon_text);
        cancel_estimated = rootView.findViewById(R.id.cancel_estimated);
        cancel_coupon = rootView.findViewById(R.id.cancel_coupon);
        cancel_promo = rootView.findViewById(R.id.cancel_promo);
        frmDestination = rootView.findViewById(R.id.frmDestination);
        destinationLayer = rootView.findViewById(R.id.destinationLayer);
        lnrparcel = rootView.findViewById(R.id.lnrparcel);
        destination = rootView.findViewById(R.id.destination);
        imgMenu = rootView.findViewById(R.id.imgMenu);
        imgSos = rootView.findViewById(R.id.imgSos);
        imgShareRide = rootView.findViewById(R.id.imgShareRide);
        mapfocus = rootView.findViewById(R.id.mapfocus);
        imgBack = rootView.findViewById(R.id.imgBack);
        shadowBack = rootView.findViewById(R.id.shadowBack);

//        <!-- Request to providers-->

        lnrRequestProviders = rootView.findViewById(R.id.lnrRequestProviders);
        rcvServiceTypes = rootView.findViewById(R.id.rcvServiceTypes);
        imgPaymentType = rootView.findViewById(R.id.imgPaymentType);
        lblPaymentType = rootView.findViewById(R.id.lblPaymentType);
        lblPaymentChange = rootView.findViewById(R.id.lblPaymentChange);
        booking_id = rootView.findViewById(R.id.booking_id);
//        <!--  Driver and service type Details-->

        lnrSearchAnimation = rootView.findViewById(R.id.lnrSearch);
        lnrProviderPopup = rootView.findViewById(R.id.lnrProviderPopup);
        lnrPriceBase = rootView.findViewById(R.id.lnrPriceBase);
        lnrPricekm = rootView.findViewById(R.id.lnrPricekm);
        lnrPricemin = rootView.findViewById(R.id.lnrPricemin);
        lnrHidePopup = rootView.findViewById(R.id.lnrHidePopup);
        imgProviderPopup = rootView.findViewById(R.id.imgProviderPopup);
        lblServiceName = rootView.findViewById(R.id.lblServiceName);
        lblCapacity = rootView.findViewById(R.id.lblCapacity);
        lblPriceKm = rootView.findViewById(R.id.lblPriceKm);
        lblPriceMin = rootView.findViewById(R.id.lblPriceMin);
        lblCalculationType = rootView.findViewById(R.id.lblCalculationType);
        lblBasePricePopup = rootView.findViewById(R.id.lblBasePricePopup);
        lblProviderDesc = rootView.findViewById(R.id.lblProviderDesc);
        btnDonePopup = rootView.findViewById(R.id.btnDonePopup);


//         <!--2. Approximate Rate ...-->

        lnrApproximate = rootView.findViewById(R.id.lnrApproximate);
        applycouponlayout = rootView.findViewById(R.id.applycouponlayout);
        imgSchedule = rootView.findViewById(R.id.imgSchedule);
        chkWallet = rootView.findViewById(R.id.chkWallet);
        chkReturn = rootView.findViewById(R.id.chkReturn);
        addwallet = rootView.findViewById(R.id.aWallet);
        LinearLayout layout_coupon = rootView.findViewById(R.id.layout_coupon);
        lblEta = rootView.findViewById(R.id.lblEta);
        lblType = rootView.findViewById(R.id.lblType);
        lblApproxAmount = rootView.findViewById(R.id.lblApproxAmount);
        lblapproxtax = rootView.findViewById(R.id.lblapproxtax);
        lblapproxdistance = rootView.findViewById(R.id.lblapproxdistance);
        lblapproxtoal = rootView.findViewById(R.id.lblapproxtotal);

        surgeTxt = rootView.findViewById(R.id.surge_txt);
        btnRequestRideConfirm = rootView.findViewById(R.id.btnRequestRideConfirm);
        dh_source = rootView.findViewById(R.id.dh_source);
        dh_destination = rootView.findViewById(R.id.dh_destination);

        addwallet.setOnClickListener(view -> startActivity(new Intent(getActivity(), ActivityWallet.class)));
        layout_coupon.setOnClickListener(view -> {
            flowValue = 11;
            layoutChanges();
        });

        cancel_estimated.setOnClickListener(v -> {
            flowValue = 1;
            layoutChanges();
        });

        cancel_coupon.setOnClickListener(v -> {
            flowValue = 1;
            layoutChanges();
        });
        cancel_promo.setOnClickListener(v -> {
            coupon_text.setText(getResources().getString(R.string.add_promo_code));
            coupon_text.setTextColor(getResources().getColor(R.color.grey_60));
            cancel_promo.setVisibility(View.GONE);
            SharedHelper.putKey(context, "PromoOnRide", "0");
        });


        apply_button.setOnClickListener(v -> {
            Utilities.hideKeyboard(getActivity());
            if (coupon_et.getText().toString().isEmpty()) {
                coupon_et.setError("Enter a coupon");
            } else {
                sendToServer();
            }
        });

//       Schedule Layout
        ScheduleLayout = rootView.findViewById(R.id.ScheduleLayout);
        scheduleDate = rootView.findViewById(R.id.scheduleDate);
        scheduleTime = rootView.findViewById(R.id.scheduleTime);
        scheduleBtn = rootView.findViewById(R.id.scheduleBtn);

//         <!--3. Waiting For Providers ...-->

        lnrWaitingForProviders = rootView.findViewById(R.id.lnrWaitingForProviders);
        lblNoMatch = rootView.findViewById(R.id.lblNoMatch);
        btnCancelRide = rootView.findViewById(R.id.btnCancelRide);

//          <!--4. Driver Accepted ...-->

        lnrProviderAccepted = rootView.findViewById(R.id.lnrProviderAccepted);
        lnrAfterAcceptedStatus = rootView.findViewById(R.id.lnrAfterAcceptedStatus);
        AfterAcceptButtonLayout = rootView.findViewById(R.id.AfterAcceptButtonLayout);
        imgProvider = rootView.findViewById(R.id.imgProvider);
        imgServiceRequested = rootView.findViewById(R.id.imgServiceRequested);
        lblProvider = rootView.findViewById(R.id.lblProvider);
        lblStatus = rootView.findViewById(R.id.lblStatus);
        lblSurgePrice = rootView.findViewById(R.id.lblSurgePrice);
        lblServiceRequested = rootView.findViewById(R.id.lblServiceRequested);
        lblModelNumber = rootView.findViewById(R.id.lblModelNumber);
        lblModelNumber2 = rootView.findViewById(R.id.lblModelNumber2);
        lblOTP = rootView.findViewById(R.id.lblOTP);
        OTPLayout = rootView.findViewById(R.id.OTPLayout);
        ratingProvider = rootView.findViewById(R.id.ratingProvider);
        btnCall = rootView.findViewById(R.id.btnCall);
        btnsms = rootView.findViewById(R.id.btnsms);
        btnCancelTrip = rootView.findViewById(R.id.btnCancelTrip);
        btnCancelTriptext = rootView.findViewById(R.id.btnCancelTriptext);


//           <!--5. Invoice Layout ...-->

        lnrInvoice = rootView.findViewById(R.id.lnrInvoice);
        lblBasePrice = rootView.findViewById(R.id.lblBasePrice);
        lblExtraPrice = rootView.findViewById(R.id.lblExtraPrice);
        lblDistancePrice = rootView.findViewById(R.id.lblDistancePrice);
        lbDistanceCovered = rootView.findViewById(R.id.lblDistanceCovered);

        lblDisPrice = rootView.findViewById(R.id.lblDisPrice);
        lblTimeTaken = rootView.findViewById(R.id.lblTimeTaken);
        lblTimeTakenPrice = rootView.findViewById(R.id.lblTimeTakenprice);

        lblTaxPrice = rootView.findViewById(R.id.lblTaxPrice);
        lblTimePrice = rootView.findViewById(R.id.lblTimePrice);
        lblTotalPrice = rootView.findViewById(R.id.lblTotalPrice);
        lblPaymentTypeInvoice = rootView.findViewById(R.id.lblPaymentTypeInvoice);
        imgPaymentTypeInvoice = rootView.findViewById(R.id.imgPaymentTypeInvoice);
        btnPayNow = rootView.findViewById(R.id.btnPayNow);
        btnPayNow_online = rootView.findViewById(R.id.btnPayNow_online);
        btnPaymentDoneBtn = rootView.findViewById(R.id.btnPaymentDoneBtn);
        bookingIDLayout = rootView.findViewById(R.id.bookingIDLayout);
        walletDetectionLayout = rootView.findViewById(R.id.walletDetectionLayout);
        discountDetectionLayout = rootView.findViewById(R.id.discountDetectionLayout);
        lblWalletPrice = rootView.findViewById(R.id.lblWalletPrice);
        lblDiscountPrice = rootView.findViewById(R.id.lblDiscountPrice);


//          <!--6. Rate provider Layout ...-->

        lnrHomeWork = rootView.findViewById(R.id.lnrHomeWork);
        lnrHome = rootView.findViewById(R.id.lnrHome);
        lnrhometxt = rootView.findViewById(R.id.lnrhometxt);
        lnrworktxt = rootView.findViewById(R.id.lnrworktxt);
        lnrWork = rootView.findViewById(R.id.lnrWork);
        lnrRateProvider = rootView.findViewById(R.id.lnrRateProvider);
        lblProviderNameRate = rootView.findViewById(R.id.lblProviderName);
        imgProviderRate = rootView.findViewById(R.id.imgProviderRate);
        txtCommentsRate = rootView.findViewById(R.id.txtComments);
        ratingProviderRate = rootView.findViewById(R.id.ratingProviderRate);
        btnSubmitReview = (MyButton) rootView.findViewById(R.id.btnSubmitReview);

//            <!--Static marker-->

        rtlStaticMarker = rootView.findViewById(R.id.rtlStaticMarker);
        imgDestination = rootView.findViewById(R.id.imgDestination);
        btnDone = rootView.findViewById(R.id.btnDone);
        btnHome = rootView.findViewById(R.id.btnHome);
        btnWork = rootView.findViewById(R.id.btnWork);

        getCards();

        checkStatus();

        handleCheckStatus = new Handler();
        //check status every 3 sec
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper.isConnectingToInternet()) {
                    if (!isAdded()) {
                        return;
                    }
                    checkStatus();
                    Utilities.print("Handler", "Called");
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                        alert = null;
                    }
                } else {
                    showDialog();
                }
                handleCheckStatus.postDelayed(this, chksTime);
            }
        }, chksTime);


        btnDonePopup.setOnClickListener(new OnClick());
        lnrHidePopup.setOnClickListener(new OnClick());
        btnRequestRideConfirm.setOnClickListener(new OnClick());
        btnCancelRide.setOnClickListener(new OnClick());
        btnCancelTrip.setOnClickListener(new OnClick());
        btnCall.setOnClickListener(new OnClick());
        btnsms.setOnClickListener(new OnClick());
        btnPayNow.setOnClickListener(new OnClick());
        btnPaymentDoneBtn.setOnClickListener(new OnClick());
        btnSubmitReview.setOnClickListener(new OnClick());
        btnHome.setOnClickListener(new OnClick());
        btnWork.setOnClickListener(new OnClick());
        btnDone.setOnClickListener(new OnClick());
        destinationLayer.setOnClickListener(new OnClick());
        lnrparcel.setOnClickListener(new OnClick());
        frmDestination.setOnClickListener(new OnClick());
        frmDest.setOnClickListener(new OnClick());
        lblPaymentChange.setOnClickListener(new OnClick());
        frmSource.setOnClickListener(new OnClick());
        imgMenu.setOnClickListener(new OnClick());
        mapfocus.setOnClickListener(new OnClick());
        imgSchedule.setOnClickListener(new OnClick());
        imgBack.setOnClickListener(new OnClick());
        scheduleBtn.setOnClickListener(new OnClick());
        scheduleDate.setOnClickListener(new OnClick());
        scheduleTime.setOnClickListener(new OnClick());
        imgProvider.setOnClickListener(new OnClick());
        imgProviderRate.setOnClickListener(new OnClick());
        imgSos.setOnClickListener(new OnClick());
        imgShareRide.setOnClickListener(new OnClick());

        lnrRequestProviders.setOnClickListener(new OnClick());
        lnrProviderPopup.setOnClickListener(new OnClick());
        ScheduleLayout.setOnClickListener(new OnClick());
        lnrApproximate.setOnClickListener(new OnClick());
        lnrProviderAccepted.setOnClickListener(new OnClick());
        lnrInvoice.setOnClickListener(new OnClick());
        lnrRateProvider.setOnClickListener(new OnClick());
        lnrWaitingForProviders.setOnClickListener(new OnClick());

        flowValue = 0;
        layoutChanges();

        //Load animation
        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_top = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_top);
        slide_up_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_down);

//        rootView.setFocusableInTouchMode(true);
//        rootView.requestFocus();
//        rootView.setOnKeyListener((v, keyCode, event) -> {
//            if (event.getAction() != KeyEvent.ACTION_DOWN)
//                return true;
//
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//                if (!reqStatus.equalsIgnoreCase("SEARCHING")) {
//                    Utilities.print("", "Back key pressed!");
//                    if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
//                        flowValue = 0;
//                        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
//                            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
//                            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
//                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                        }
//                    } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
//                        flowValue = 1;
//                    } else if (applycouponlayout.getVisibility() == View.VISIBLE) {
//                        applycouponlayout.startAnimation(slide_down);
//                    } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
//                        flowValue = 1;
//                    } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
//                        flowValue = 1;
//                    }
//                    if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
////                        ((MainActivity) context).refreshView();
//                        flowValue = 0;
//                    } else if (rtlStaticMarker.getVisibility() == View.VISIBLE) {
//                        flowValue = 0;
//                    } else {
//                        getActivity().finish();
//                    }
//                    layoutChanges();
//                    return true;
//                }
//            }
//            return false;
//        });

    }

    private void setupSearchPart() {
        mAutoCompleteAdapter = null;
        SearchSource.setText("");
        imgSourceClose.setVisibility(View.GONE);
        if (!isParcel) {
            String s_address = frmSource.getText().toString();
            SearchSource.setText(s_address);
            if (s_address.trim().equals("") || s_address == null) {
                if (SearchSource.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager)
                            context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(SearchSource, InputMethodManager.SHOW_IMPLICIT);
                }
//            SearchSource.requestFocus();
            } else {
                if (SearchDestination.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager)
                            context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(SearchDestination, InputMethodManager.SHOW_IMPLICIT);
                }
//            SearchDestination.requestFocus();
            }
        } else if (SearchSource.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(SearchSource, InputMethodManager.SHOW_IMPLICIT);
        }


        imgDestinationClose.setOnClickListener(v -> {
            mAutoCompleteAdapter = null;
            SearchDestination.setText("");
            imgDestinationClose.setVisibility(View.GONE);
            SearchDestination.requestFocus();

        });

        imgSourceClose.setOnClickListener(v -> {
            mAutoCompleteAdapter = null;
            SearchSource.setText("");
            imgSourceClose.setVisibility(View.GONE);
            SearchSource.requestFocus();
        });

        SearchDestination.setText("");
        imgDestinationClose.setVisibility(View.GONE);

        SearchSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strSelected = "source";
                if (SearchSource.getText().length() > 0) {
                    mAutoCompleteList.setVisibility(View.VISIBLE);
                    lnrFavorite.setVisibility(View.GONE);
                    txtPickLocation.setVisibility(View.VISIBLE);
                    imgSourceClose.setVisibility(View.VISIBLE);
//                    txtPickLocation.setText(getString(R.string.pin_location));
                    Runnable run = () -> {
                        // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                        UserApplication.getInstance().cancelRequestInQueue(GETPLACESHIT);

                        JSONObject object = new JSONObject();
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(SearchSource.getText().toString()),
                                object, response -> {
                            Log.v("SearchPlacesResponse", response.toString());
                            Gson gson = new Gson();
                            predictions = gson.fromJson(response.toString(), PlacePredictions.class);
                            if (mAutoCompleteAdapter == null) {
                                mAutoCompleteAdapter = new AutoCompleteAdapter(context, predictions.getPlaces(), activity);
                                mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
                            } else {
                                mAutoCompleteList.setVisibility(View.VISIBLE);
                                mAutoCompleteAdapter.clear();
                                mAutoCompleteAdapter.addAll(predictions.getPlaces());
                                mAutoCompleteAdapter.notifyDataSetChanged();
                                mAutoCompleteList.invalidate();
                            }
                        }, error -> Log.v("SearchPlacesResponse", error.toString()));
                        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);

                    };
                    // only canceling the network calls will not help, you need to remove all callbacks as well
                    // otherwise the pending callbacks and messages will again invoke the handler and will send the request

                    Handler handler = new Handler();
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(run, 100);

                } else {
                    imgSourceClose.setVisibility(View.VISIBLE);
                    lnrFavorite.setVisibility(View.VISIBLE);
                    mAutoCompleteList.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        txtPickLocation.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("Location Address", placePredictions);
            intent.putExtra("pick_location", "yes");
            intent.putExtra("type", strSelected);
            SearchDataCompelete(Activity.RESULT_OK, intent);
        });

        SearchDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strSelected = "destination";
                if (SearchDestination.getText().toString().trim().length() > 0) {
                    lnrFavorite.setVisibility(View.GONE);
                    txtPickLocation.setVisibility(View.VISIBLE);
                    imgDestinationClose.setVisibility(View.VISIBLE);
                    mAutoCompleteList.setVisibility(View.VISIBLE);

                    if (mAutoCompleteAdapter == null)
                        mAutoCompleteList.setVisibility(View.VISIBLE);
//                    txtPickLocation.setText(getString(R.string.pin_location));
                    Runnable run = () -> {
                        UserApplication.getInstance().cancelRequestInQueue(GETPLACESHIT);
                        JSONObject object = new JSONObject();
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(SearchDestination.getText().toString()), object, response -> {
                            Log.v("SearchPlacesResponse", response.toString());
                            Gson gson = new Gson();
                            predictions = gson.fromJson(response.toString(), PlacePredictions.class);
                            if (mAutoCompleteAdapter == null) {
                                mAutoCompleteList.setVisibility(View.VISIBLE);
                                mAutoCompleteAdapter = new AutoCompleteAdapter(context, predictions.getPlaces(), activity);
                                mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
                            } else {
                                mAutoCompleteList.setVisibility(View.VISIBLE);
                                mAutoCompleteAdapter.clear();
                                mAutoCompleteAdapter.addAll(predictions.getPlaces());
                                mAutoCompleteAdapter.notifyDataSetChanged();
                                mAutoCompleteList.invalidate();
                            }
                        }, error -> Log.v("SearchPlacesResponse", error.toString()));
                        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);

                    };
                    Handler handler = new Handler();
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(run, 10);

                } else {
                    imgDestinationClose.setVisibility(View.VISIBLE);
                    lnrFavorite.setVisibility(View.VISIBLE);
                    mAutoCompleteList.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        mAutoCompleteList.setOnItemClickListener((parent, view, position, id) -> {
            if (SearchSource.getText().toString().equalsIgnoreCase("")) {
                try {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                    builder.setMessage("Please choose pickup location")
                            .setTitle(getActivity().getString(R.string.app_name))
                            .setCancelable(true)
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("OK", (dialog, id1) -> {
                                SearchDestination.requestFocus();
                                SearchDestination.setText("");
                                imgDestinationClose.setVisibility(View.GONE);
                                mAutoCompleteList.setVisibility(View.GONE);
                                dialog.dismiss();
                            });
                    androidx.appcompat.app.AlertDialog alert = builder.create();
                    alert.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                setGoogleAddress(position);
            }
        });

    }

    private void setGoogleAddress(int position) {
        if (mGoogleApiClient != null) {
            Utilities.print("GooglePlacesSearch", "Place ID == >" + predictions.getPlaces().get(position).getPlaceID());
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS);
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
                    placePredictions.strDestAddress = myPlace.getName() + ", " + myPlace.getAddress();
                    placePredictions.strDestLatLng = myPlace.getLatLng().toString();
                    placePredictions.strDestLatitude = myPlace.getLatLng().latitude + "";
                    placePredictions.strDestLongitude = myPlace.getLatLng().longitude + "";
                    SearchDestination.setText(placePredictions.strDestAddress);
                    SearchDestination.setSelection(0);
                    Utilities.hideKeyboard(activity);
                    mAutoCompleteList.setAdapter(null);
                    mAutoCompleteList.setVisibility(View.GONE);
                    dest_address = myPlace.getName() + ", " + myPlace.getAddress();
                } else {
                    placePredictions.strSourceAddress = myPlace.getName() + ", " + myPlace.getAddress();
                    placePredictions.strSourceLatLng = myPlace.getLatLng().toString();
                    placePredictions.strSourceLatitude = myPlace.getLatLng().latitude + "";
                    placePredictions.strSourceLongitude = myPlace.getLatLng().longitude + "";
                    SearchSource.setText(placePredictions.strSourceAddress);
                    SearchSource.setSelection(0);
                    SearchDestination.requestFocus();
                    mAutoCompleteAdapter = null;
                    source_address = myPlace.getName() + ", " + myPlace.getAddress();
                }


                mAutoCompleteList.setVisibility(View.GONE);

                if (SearchDestination.getText().toString().length() > 0) {
                    if (strSelected.equalsIgnoreCase("destination")) {
                        if (!placePredictions.strDestAddress.equalsIgnoreCase(placePredictions.strSourceAddress)) {
                            Intent intent = new Intent();
                            intent.putExtra("Location Address", placePredictions);
                            intent.putExtra("pick_location", "no");
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            SearchDataCompelete(Activity.RESULT_OK, intent);
//                            setAddress();
                        } else {
                            utils.showAlert(getActivity(), "Source and Destination address should not be same!");
                        }
                    }
                } else {
                    SearchDestination.requestFocus();
                    SearchDestination.setText("");
                    imgDestinationClose.setVisibility(View.GONE);
                    mAutoCompleteList.setVisibility(View.GONE);
                }


            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    utils.showAlert(getActivity(), "getPlaceById Error, " + apiException.getLocalizedMessage());

                }
            });
        }
    }

    private void getselectetedtab(String text) {
        switch (text) {
            case "Transportation":
                TABVAL = 0;
                break;
            case "Delivery":
                TABVAL = 1;
                break;
            case "Truck":
                TABVAL = 2;
                break;
            case "OUTSTATION":
                TABVAL = 3;
                break;
        }

        String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
        FetchUrl fetchUrl = new FetchUrl();
        fetchUrl.execute(url);
        getFLTServiceList();
    }

    private void sendToServer() {
        customDialog = new CustomDialog(context);
        if (customDialog != null)
            customDialog.show();
        JsonObject json = new JsonObject();
        json.addProperty("promocode", coupon_et.getText().toString());
        Ion.with(this)
                .load(URLHelper.ADD_COUPON_API)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(getActivity(), "token_type") + " " + SharedHelper.getKey(context, "access_token"))
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
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.oops_connect_your_internet));
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
                                    coupon_text.setText(coupon_et.getText().toString() + "-" + jsonObject.optString("amount") + "%");
                                    coupon_text.setTextColor(getResources().getColor(R.color.green_700));
                                    cancel_promo.setVisibility(View.VISIBLE);
                                    SharedHelper.putKey(context, "PromoOnRide", "1");
                                    Toast.makeText(getActivity(), context.getResources().getString(R.string.coupon_added), Toast.LENGTH_SHORT).show();
                                } else if (jsonObject.optString("code").equals("promocode_expired")) {
                                    Toast.makeText(getActivity(), context.getResources().getString(R.string.expired_coupon), Toast.LENGTH_SHORT).show();
                                } else if (jsonObject.optString("code").equals("promocode_already_in_use")) {
                                    Toast.makeText(getActivity(), context.getResources().getString(R.string.already_in_use_coupon), Toast.LENGTH_SHORT).show();
                                } else if (jsonObject.optString("code").equals("Max Users Used")) {
                                    Toast.makeText(getActivity(), context.getResources().getString(R.string.max_users_used), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), context.getResources().getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                                }
                                coupon_et.setText("");
                                flowValue = 1;
                                layoutChanges();
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
                                Toast.makeText(getActivity(), context.getResources().getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                });
    }

    private void getFLTServiceList() {
        chkReturn.setChecked(false);
        chkReturn.setVisibility(View.GONE);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(SharedHelper.getKey(context, "serviceresponse"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        filteredserviceresponse = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject obj = null;
            try {
                obj = jsonArray.getJSONObject(i);
                if (TABVAL == 0) {
                    if (obj.getString("type").equals("economy")) {
                        filteredserviceresponse.put(obj);
                    }
                } else if (TABVAL == 1) {
                    if (obj.getString("type").equals("luxury")) {
                        filteredserviceresponse.put(obj);
                    }
                } else if (TABVAL == 2) {
                    if (obj.getString("type").equals("extra_seat")) {
                        filteredserviceresponse.put(obj);
                    }
                } else if (TABVAL == 3) {
                    chkReturn.setVisibility(View.VISIBLE);
                    if (obj.getString("type").equals("outstation")) {
                        filteredserviceresponse.put(obj);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (filteredserviceresponse.length() > 0) {
            nocarlayout.setVisibility(View.GONE);
            currentPostion = 0;
            ServiceListAdapter serviceListAdapter = new ServiceListAdapter(filteredserviceresponse);
            rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity));
            rcvServiceTypes.setAdapter(serviceListAdapter);
            serviceListAdapter.notifyDataSetChanged();
            getProvidersList(SharedHelper.getKey(context, "service_type"));
        } else {
            ServiceListAdapter serviceListAdapter = new ServiceListAdapter(filteredserviceresponse);
            rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity));
            rcvServiceTypes.setAdapter(serviceListAdapter);
            serviceListAdapter.notifyDataSetChanged();
            nocarlayout.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("MissingPermission")
    void initMap() {
        if (mMap == null) {
            FragmentManager fm = getChildFragmentManager();
            SupportMapFragment mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.provider_map));
            mapFragment.getMapAsync(this);
        }

        if (mMap != null) {
            setupMap();
        }
    }


    @SuppressWarnings("MissingPermission")
    void setupMap() {
        if (mMap != null) {
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setMyLocationEnabled(false);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnCameraMoveListener(this);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    // Getting view from the layout file infowindowlayout.xml
                    View v = activity.getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView lblAddress = v.findViewById(R.id.lblAddress);
                    TextView lblTime = v.findViewById(R.id.txtTime);

                    lblAddress.setText(marker.getSnippet());

                    if (strTimeTaken.length() > 0) {
                        lblTime.setText(strTimeTaken);
                    }

                    if (marker.getTitle() == null) {
                        return null;
                    }
                    if (marker.getTitle().equalsIgnoreCase("source") || marker.getTitle().equalsIgnoreCase("destination")) {
                        return v;
                    } else {
                        return null;
                    }
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });

//            View locationButton = ((View) rootView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
//            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
//// position on right bottom
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//            rlp.setMargins(0, 180, 180, 0);
        }
    }

    /**
     * calculates the distance between two locations in MILES
     */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng oldlatLng = null;
        try {
            if (marker != null) {
                oldlatLng = marker.getPosition();
                marker.remove();
            }
            if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .anchor(0.5f, 0.75f)
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
                marker = mMap.addMarker(markerOptions);

                current_lat = "" + location.getLatitude();
                current_lng = "" + location.getLongitude();

                if (source_lat.equalsIgnoreCase("") || source_lat.length() < 0) {
                    source_lat = current_lat;
                }
                if (source_lng.equalsIgnoreCase("") || source_lng.length() < 0) {
                    source_lng = current_lng;
                }

                if (value == 0) {
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.setPadding(0, 0, 0, 0);
                    mMap.getUiSettings().setZoomControlsEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    mMap.getUiSettings().setCompassEnabled(false);

                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();
                    String currentAddress = utils.getCompleteAddressString(context, latitude, longitude);
                    Utilities.getAddressUsingLatLng("source", frmSource, context, "" + latitude, "" + longitude);
                    source_lat = "" + latitude;
                    source_lng = "" + longitude;
                    source_address = currentAddress;
                    current_address = currentAddress;
                    String[] separated = city.cname.split(",");
                    if (!currentAddress.toLowerCase().contains(separated[0].toLowerCase())) {
                        showFromGeo = true;
                        showCityDialog();
                    } else {
                        if (!showFromGeo) {
                            if (is_track.equalsIgnoreCase("YES") &&
                                    (CurrentStatus.equalsIgnoreCase("STARTED") || CurrentStatus.equalsIgnoreCase("PICKEDUP")
                                            || CurrentStatus.equalsIgnoreCase("ARRIVED"))) {
                                showFromGeo = true;
                            } else {
                                if (!placePredictions.strDestAddress.equalsIgnoreCase(source_address)) {
                                    dest_lat = placePredictions.strDestLatitude;
                                    dest_lng = placePredictions.strDestLongitude;
                                    dest_address = placePredictions.strDestAddress;
                                    placePredictions.strDestLatLng = new LatLng(parseDouble(placePredictions.strDestLatitude), parseDouble(placePredictions.strDestLongitude)).toString();
                                    Intent intent = new Intent();
                                    intent.putExtra("Location Address", placePredictions);
                                    intent.putExtra("pick_location", "no");
                                    SearchDataCompelete(Activity.RESULT_OK, intent);
                                    showFromGeo = true;
                                } else {
                                    utils.showAlert(getActivity(), "Source and Destination address should not be same!");
                                }

                            }

                        }
                    }
                    value++;
                    if ((customDialog != null) && (customDialog.isShowing())) {
                        customDialog.dismiss();
                    }
                } else if (oldlatLng != null) {
                    // lat1 and lng1 are the values of a previously stored location
                    if (distance(oldlatLng.latitude, oldlatLng.longitude, location.getLatitude(), location.getLongitude()) > 0.1) { // if distance < 0.1 miles we take locations as equal
                        //do what you want to do...
                        LatLng myLocation1 = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraPosition cameraPosition1 = new CameraPosition.Builder().target(myLocation1).zoom(16).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                    }
                }
//                if (!showFromGeo) {
//                    if (is_track.equalsIgnoreCase("YES") &&
//                            (CurrentStatus.equalsIgnoreCase("STARTED") || CurrentStatus.equalsIgnoreCase("PICKEDUP")
//                                    || CurrentStatus.equalsIgnoreCase("ARRIVED"))) {
//                        showFromGeo = true;
//                    } else {
//                        if (!placePredictions.strDestAddress.equalsIgnoreCase(source_address)) {
//                            dest_lat = placePredictions.strDestLatitude;
//                            dest_lng = placePredictions.strDestLongitude;
//                            dest_address = placePredictions.strDestAddress;
//                            placePredictions.strDestLatLng = new LatLng(parseDouble(placePredictions.strDestLatitude), parseDouble(placePredictions.strDestLongitude)).toString();
//                            Intent intent = new Intent();
//                            intent.putExtra("Location Address", placePredictions);
//                            intent.putExtra("pick_location", "no");
//                            SearchDataCompelete(Activity.RESULT_OK, intent);
//                            showFromGeo = true;
//                        } else {
//                            utils.showAlert(getActivity(), "Source and Destination address should not be same!");
//                        }
//
//                    }
//
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void showCityDialog() {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage("You are outside of the service area.")
                .setPositiveButton("Change City", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        ((MainActivity) activity).GoToCityActivity();
                    }
                }).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                String currentAddress = Utilities.getAddressUsingLatLng("source", frmSource, context, "" + source_lat, "" + source_lng);
                current_address = currentAddress;
                String[] separated = city.cname.split(",");
                if (!currentAddress.toLowerCase().contains(separated[0].toLowerCase())) {
                    showCityDialog();
                }
            }
        }).create().show();
    }


    class OnClick implements View.OnClickListener {

        @SuppressLint("WrongConstant")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnHome:
                    if (!SharedHelper.getKey(context, "home").equalsIgnoreCase("")) {
                        whitestatusbarcolor(0);
                        setHomeWorkAddress("home");
                    } else {
                        startActivity(new Intent(getActivity(), ActivitySettings.class));
                    }
                    break;
                case R.id.btnWork:
                    if (!SharedHelper.getKey(context, "work").equalsIgnoreCase("")) {
                        whitestatusbarcolor(0);
                        setHomeWorkAddress("work");
                    } else {
                        startActivity(new Intent(getActivity(), ActivitySettings.class));
                    }
                    break;
                case R.id.frmSource:
                    Intent intent = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent.putExtra("cursor", "source");
                    intent.putExtra("s_address", frmSource.getText().toString());
                    intent.putExtra("d_address", frmDest.getText().toString());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;
                case R.id.destinationLayer:
                    if (CurrentStatus.equalsIgnoreCase("")) {
                        isParcel = false;
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                        Intent intent2 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
//                        intent2.putExtra("cursor", "destination");
//                        intent2.putExtra("s_address", frmSource.getText().toString());
//                        intent2.putExtra("d_address", frmDest.getText().toString());
//                        startActivityForResult(intent2, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    }
                    break;
//                case R.id.txtChange:
//                    Intent intent4 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
//                    intent4.putExtra("cursor", "destination");
//                    intent4.putExtra("s_address", frmSource.getText().toString());
//                    intent4.putExtra("d_address", frmDest.getText().toString());
//                    startActivityForResult(intent4, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
//                    break;
                case R.id.lnrparcel:
                    if (CurrentStatus.equalsIgnoreCase("")) {
                        SearchSource.setText("");
                        isParcel = true;
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                        Intent intent2 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
//                        intent2.putExtra("cursor", "destination");
//                        intent2.putExtra("s_address", frmSource.getText().toString());
//                        intent2.putExtra("d_address", frmDest.getText().toString());
//                        startActivityForResult(intent2, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    }
                    break;
//                case R.id.txtChange:
//                    Intent intent4 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
//                    intent4.putExtra("cursor", "destination");
//                    intent4.putExtra("s_address", frmSource.getText().toString());
//                    intent4.putExtra("d_address", frmDest.getText().toString());
//                    startActivityForResult(intent4, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
//                    break;

                case R.id.frmDest:
                    Intent intent3 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent3.putExtra("cursor", "destination");
                    intent3.putExtra("s_address", frmSource.getText().toString());
                    intent3.putExtra("d_address", destination.getText().toString());
                    intent3.putExtra("d_address", frmDest.getText().toString());
                    startActivityForResult(intent3, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;
                case R.id.lblPaymentChange:
                    showChooser();
                    break;
//                case R.id.btnRequestRides:
//                    scheduledDate = "";
//                    scheduledTime = "";
//                    if (!frmSource.getText().toString().equalsIgnoreCase("") &&
//                            !frmDest.getText().toString().equalsIgnoreCase("")) {
//                        getApproximateFare();
//                        frmDest.setOnClickListener(null);
//                        frmSource.setOnClickListener(null);
//                        srcDestLayout.setOnClickListener(new OnClick());
//                    } else {
//                        Toast.makeText(context, context.getResources().getString(R.string.pickup_drop), Toast.LENGTH_SHORT).show();
//                    }
//                    break;
                case R.id.btnRequestRideConfirm:
                    SharedHelper.putKey(context, "name", "");
                    scheduledDate = "";
                    scheduledTime = "";
                    sendRequest();
                    break;
                case R.id.btnPayNow:
                    payNow();
                    break;
                case R.id.btnPaymentDoneBtn:
                    btnPayNow.setVisibility(View.GONE);
                    btnPaymentDoneBtn.setVisibility(View.GONE);
                    flowValue = 6;
                    layoutChanges();
                    break;
                case R.id.btnSubmitReview:
                    submitReviewCall();
                    break;
                case R.id.lnrHidePopup:
                case R.id.btnDonePopup:
                    lnrHidePopup.setVisibility(View.GONE);
                    flowValue = 1;
                    layoutChanges();
                    int click = 1;
                    break;
                case R.id.btnCancelRide:
                    showCancelRideDialog();
                    break;
                case R.id.btnCancelTrip:
                    if (btnCancelTriptext.getText().toString().equals(context.getResources().getString(R.string.cancel_trip)))
                        showCancelRideDialog();
                    else {
                        String shareUrl = URLHelper.REDIRECT_SHARE_URL;
                        navigateToShareScreen(shareUrl);
                    }
                    break;
                case R.id.imgSos:
                    showSosPopUp();
                    break;
                case R.id.imgShareRide:
                    String url = "http://maps.google.com/maps?q=loc:";
                    navigateToShareScreen(url);
                    break;
                case R.id.imgProvider:
                    Intent intent1 = new Intent(activity, ShowProfile.class);
                    intent1.putExtra("driver", driver);
                    startActivity(intent1);
                    break;
                case R.id.imgProviderRate:
                    Intent intent5 = new Intent(activity, ShowProfile.class);
                    intent5.putExtra("driver", driver);
                    startActivity(intent5);
                    break;
                case R.id.btnCall:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
                    } else {
                        Intent intentCall = new Intent(Intent.ACTION_CALL);
                        intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                        startActivity(intentCall);
                    }
                    break;
                case R.id.btnsms:
                    Intent intentchat = new Intent(getActivity(), newlivechat.class);
                    intentchat.putExtra("name", lblProvider.getText().toString());
                    intentchat.putExtra("uid", SharedHelper.getKey(context, "uid"));
                    intentchat.putExtra("pid", SharedHelper.getKey(context, "pid"));
                    intentchat.putExtra("booking_id", SharedHelper.getKey(context, "booking_id"));
                    Log.e("btnsms", SharedHelper.getKey(context, "uid") + SharedHelper.getKey(context, "pid") + SharedHelper.getKey(context, "booking_id"));
                    startActivity(intentchat);
                    break;
                case R.id.btnDone:
                    if (is_track.equalsIgnoreCase("YES") &&
                            (CurrentStatus.equalsIgnoreCase("STARTED") || CurrentStatus.equalsIgnoreCase("PICKEDUP")
                                    || CurrentStatus.equalsIgnoreCase("ARRIVED"))) {
                        extend_dest_lat = "" + cmPosition.target.latitude;
                        extend_dest_lng = "" + cmPosition.target.longitude;
                        showTripExtendAlert(extend_dest_lat, extend_dest_lng);
                    } else {
                        try {

                            Utilities.print("centerLat", cmPosition.target.latitude + "");
                            Utilities.print("centerLong", cmPosition.target.longitude + "");

                            Geocoder geocoder = null;
                            List<Address> addresses;
                            geocoder = new Geocoder(getActivity(), Locale.getDefault());

                            String city = "", state = "", address = "";

                            try {
                                addresses = geocoder.getFromLocation(cmPosition.target.latitude, cmPosition.target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                address = addresses.get(0).getAddressLine(0);
                                city = addresses.get(0).getLocality();
                                state = addresses.get(0).getAdminArea();
                                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (strPickType.equalsIgnoreCase("source")) {
                                source_address = Utilities.getAddressUsingLatLng("source", SearchSource, context, "" + cmPosition.target.latitude, "" + cmPosition.target.longitude);
                                frmSource.setText(source_address);
                                source_lat = "" + cmPosition.target.latitude;
                                source_lng = "" + cmPosition.target.longitude;

                                rtlStaticMarker.setVisibility(View.GONE);
                                frmDestination.setVisibility(View.VISIBLE);
                                imgBack.setVisibility(View.GONE);
                                imgMenu.setVisibility(View.VISIBLE);
                                lnrHomeWork.setVisibility(View.VISIBLE);
//                                shadowBack.setVisibility(View.VISIBLE);
                                lnrFavorite.setVisibility(View.VISIBLE);
                                bottom_sheet.setVisibility(View.VISIBLE);
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            } else {
                                dest_lat = "" + cmPosition.target.latitude;
                                if (dest_lat.equalsIgnoreCase(source_lat)) {
                                    Toast.makeText(context, "Both source and destination are same", Toast.LENGTH_SHORT).show();
                                } else {
                                    dest_address = Utilities.getAddressUsingLatLng("destination", SearchDestination, context, "" + cmPosition.target.latitude, "" + cmPosition.target.longitude);
                                    frmDest.setText(dest_address);
                                    dest_lat = "" + cmPosition.target.latitude;
                                    dest_lng = "" + cmPosition.target.longitude;
                                    rtlStaticMarker.setVisibility(View.GONE);
                                    frmDestination.setVisibility(View.VISIBLE);
                                    imgBack.setVisibility(View.GONE);
                                    imgMenu.setVisibility(View.VISIBLE);
                                    lnrHomeWork.setVisibility(View.VISIBLE);
//                                    shadowBack.setVisibility(View.VISIBLE);
                                    lnrFavorite.setVisibility(View.VISIBLE);
                                    bottom_sheet.setVisibility(View.VISIBLE);
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                                    // set on map changes to show prices start
                                    placePredictions.strDestLatitude = dest_lat;
                                    placePredictions.strDestLongitude = dest_lng;
                                    placePredictions.strDestAddress = dest_address;
                                    placePredictions.strDestLatLng = new LatLng(parseDouble(placePredictions.strDestLatitude), parseDouble(placePredictions.strDestLongitude)).toString();

                                    Intent intent2 = new Intent();
                                    intent2.putExtra("Location Address", placePredictions);
                                    intent2.putExtra("pick_location", "no");
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    SearchDataCompelete(Activity.RESULT_OK, intent2);
                                    // set on map changes to show prices end
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Can't able to get the address!.Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.imgBack:
                    if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                        ((MainActivity) context).refreshView();
                        break;
//                        flowValue = 0;
//                        srcDestLayout.setVisibility(View.GONE);
//                        frmSource.setOnClickListener(new OnClick());
//                        frmDest.setOnClickListener(new OnClick());
//                        srcDestLayout.setOnClickListener(null);
//                        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
//                            //   destinationBorderImg.setVisibility(View.VISIBLE);
//                            //verticalView.setVisibility(View.GONE);
//                            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
//                            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
//                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                            srcDestLayout.setVisibility(View.GONE);
//                        }
                    } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
                        frmSource.setOnClickListener(new OnClick());
                        frmDest.setOnClickListener(new OnClick());
                        srcDestLayout.setOnClickListener(null);
                        flowValue = 1;
                    } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
                        flowValue = 1;
                    } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                        flowValue = 1;
                    } else if (rtlStaticMarker.getVisibility() == View.VISIBLE) {
                        flowValue = 0;
                    }

                    layoutChanges();
                    break;
                case R.id.imgMenu:
                    try {
                        if (NAV_DRAWER == 0) {
                            if (drawer != null)
                                drawer.openDrawer(Gravity.START);
                        } else {
                            NAV_DRAWER = 0;
                            if (drawer != null)
                                drawer.closeDrawers();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.mapfocus:
                    Double crtLat, crtLng;
                    if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                        crtLat = parseDouble(current_lat);
                        crtLng = parseDouble(current_lng);

                        if (crtLat != null && crtLng != null) {
                            LatLng loc = new LatLng(crtLat, crtLng);
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            mapfocus.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
                case R.id.imgSchedule:
                    flowValue = 7;
                    layoutChanges();
                    break;
                case R.id.scheduleBtn:
                    SharedHelper.putKey(context, "isSchedule", "Yes");
                    if (!scheduledDate.equals("") && !scheduledTime.equals("")) {
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long milliseconds = date.getTime();
                        if (!DateUtils.isToday(milliseconds)) {
                            sendRequest();
                        } else {
                            if (utils.checktimings(scheduledTime)) {
                                sendRequest();
                            } else {
                                Toast.makeText(activity, context.getResources().getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(activity, context.getResources().getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.scheduleDate:
                    // calender class's instance and get current date , month and year from calender
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR); // current year
                    int mMonth = c.get(Calendar.MONTH); // current month
                    int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                    // date picker dialog
                    datePickerDialog = new DatePickerDialog(activity,
                            (view, year, monthOfYear, dayOfMonth) -> {

                                // set day of month , month and year value in the edit text
                                String choosedMonth = "";
                                String choosedDate = "";
                                String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                scheduledDate = choosedDateFormat;
                                try {
                                    choosedMonth = Utilities.getMonth(choosedDateFormat);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (dayOfMonth < 10) {
                                    choosedDate = "0" + dayOfMonth;
                                } else {
                                    choosedDate = "" + dayOfMonth;
                                }
                                scheduleDate.setText(choosedDate + " " + choosedMonth + " " + year);
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 7));
                    datePickerDialog.show();
                    break;
                case R.id.scheduleTime:
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        int callCount = 0;   //To track number of calls to onTimeSet()

                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                            if (callCount == 0) {
                                String choosedHour = "";
                                String choosedMinute = "";
                                String choosedTimeZone = "";
                                String choosedTime = "";

                                scheduledTime = selectedHour + ":" + selectedMinute;

                                if (selectedHour > 12) {
                                    choosedTimeZone = "PM";
                                    selectedHour = selectedHour - 12;
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                } else {
                                    choosedTimeZone = "AM";
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                }

                                if (selectedMinute < 10) {
                                    choosedMinute = "0" + selectedMinute;
                                } else {
                                    choosedMinute = "" + selectedMinute;
                                }
                                choosedTime = choosedHour + ":" + choosedMinute + " " + choosedTimeZone;

                                if (!scheduledDate.equals("") && !scheduledTime.equals("")) {
                                    Date date = null;
                                    try {
                                        date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    long milliseconds = date.getTime();
                                    if (!DateUtils.isToday(milliseconds)) {
                                        scheduleTime.setText(choosedTime);
                                    } else {
                                        if (utils.checktimings(scheduledTime)) {
                                            scheduleTime.setText(choosedTime);
                                        } else {
                                            Toast toast = new Toast(activity);
                                            Toast.makeText(activity, context.getResources().getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(activity, context.getResources().getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                                }
                            }
                            callCount++;
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                    break;
            }
        }
    }

    public void navigateToShareScreen(String shareUrl) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String name = SharedHelper.getKey(context, "first_name") + " " + SharedHelper.getKey(context, "last_name");
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + "-" + "Mr/Mrs." + name + " would like to share a rideo with you at " +
                    shareUrl + current_lat + "," + current_lng);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSosPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(context.getResources().getString(R.string.emaergeny_call))
                .setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.yes), (dialog, which) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    sosCall();
                } else
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
            } else {
                sosCall();
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sosCall() {
        Intent intentCall = new Intent(Intent.ACTION_CALL);
        intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sosnum")));
        startActivity(intentCall);
    }

    private void showCancelRideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(context.getResources().getString(R.string.cancel_ride_alert));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.yes), (dialog, which) -> showreasonDialog());
        builder.setNegativeButton(context.getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss());
        cancelRideDialog = builder.create();
        cancelRideDialog.show();
    }

    private void showreasonDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = view.findViewById(R.id.reason_etxt);
        Button submitBtn = view.findViewById(R.id.submit_btn);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setView(view)
                .setCancelable(true);
        reasonDialog = builder.create();
        submitBtn.setOnClickListener(v -> {
            cancalReason = reasonEtxt.getText().toString();
            cancelRequest();
            reasonDialog.dismiss();
        });
        reasonDialog.show();
    }

    void layoutChanges() {
        try {
            tryToGetlastKnowLocation();
            utils.hideKeypad(getActivity(), getActivity().getCurrentFocus());
            if (lnrApproximate.getVisibility() == View.VISIBLE) {
                lnrApproximate.startAnimation(slide_down);
            } else if (applycouponlayout.getVisibility() == View.VISIBLE) {
                applycouponlayout.startAnimation(slide_down);
            } else if (rtlStaticMarker.getVisibility() == View.VISIBLE) {
                rtlStaticMarker.startAnimation(slide_down);
            } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                ScheduleLayout.startAnimation(slide_down);
            } else if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                lnrRequestProviders.startAnimation(slide_down);
            } else if (lnrProviderPopup.getVisibility() == View.VISIBLE) {
                lnrProviderPopup.startAnimation(slide_down);
                lnrSearchAnimation.startAnimation(slide_up_down);
                lnrSearchAnimation.setVisibility(View.VISIBLE);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            } else if (lnrRateProvider.getVisibility() == View.VISIBLE) {
                lnrRateProvider.startAnimation(slide_down);
            }

            lnrRequestProviders.setVisibility(View.GONE);
            lnrProviderPopup.setVisibility(View.GONE);
            lnrApproximate.setVisibility(View.GONE);
            applycouponlayout.setVisibility(View.GONE);
            lnrWaitingForProviders.setVisibility(View.GONE);
            lnrProviderAccepted.setVisibility(View.GONE);
            lnrInvoice.setVisibility(View.GONE);
            lnrRateProvider.setVisibility(View.GONE);
            ScheduleLayout.setVisibility(View.GONE);
            rtlStaticMarker.setVisibility(View.GONE);
            frmDestination.setVisibility(View.GONE);
            bottom_sheet.setVisibility(View.GONE);
            imgMenu.setVisibility(View.GONE);
            imgBack.setVisibility(View.GONE);
            lnrHomeWork.setVisibility(View.GONE);
            shadowBack.setVisibility(View.GONE);
            txtCommentsRate.setText("");
            scheduleDate.setText(context.getResources().getString(R.string.sample_date));
            scheduleTime.setText(context.getResources().getString(R.string.sample_time));
            if (flowValue == 0) {
                if (imgMenu.getVisibility() == View.GONE) {
                    srcDestLayout.setVisibility(View.GONE);
                    //txtChange.setVisibility(View.GONE);
                    frmSource.setOnClickListener(new OnClick());
                    frmDest.setOnClickListener(new OnClick());
                    srcDestLayout.setOnClickListener(null);
                    if (mMap != null) {
                        mMap.clear();
                        stopAnim();
                        setupMap();
                    }
                    setCurrentAddress();
                }
                frmDestination.setVisibility(View.VISIBLE);
                bottom_sheet.setVisibility(View.VISIBLE);

                lnrHomeWork.setVisibility(View.VISIBLE);


                imgMenu.setVisibility(View.VISIBLE);
                destination.setText("");
                destination.setHint(context.getResources().getString(R.string.what_s_your_destination));
                frmDest.setText("");
                dest_address = "";
                dest_lat = "";
                dest_lng = "";
                source_lat = "" + current_lat;
                source_lng = "" + current_lng;
                source_address = "" + current_address;
                sourceAndDestinationLayout.setVisibility(View.VISIBLE);
                getservicelisticons();
                getProvidersList("");
                coupon_text.setText(getResources().getString(R.string.add_promo_code));
                coupon_text.setTextColor(getResources().getColor(R.color.grey_60));
                cancel_promo.setVisibility(View.GONE);
                SharedHelper.putKey(context, "PromoOnRide", "0");
            } else if (flowValue == 1) {
                frmSource.setVisibility(View.VISIBLE);
                // destinationBorderImg.setVisibility(View.GONE);
                // frmDestination.setVisibility(View.VISIBLE);
                imgBack.setVisibility(View.VISIBLE);
                lnrRequestProviders.startAnimation(slide_up);
                lnrRequestProviders.setVisibility(View.VISIBLE);
                TabLayout.Tab tab;
                if (isParcel)
                    tab = tab_layout.getTabAt(1);
                else
                    tab = tab_layout.getTabAt(0);
                tab_layout.selectTab(tab);
                if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                    if (chkWallet != null) {
                        chkWallet.setVisibility(View.VISIBLE);
                        addwallet.setVisibility(View.GONE);
                    }
                } else {
                    if (chkWallet != null) {
                        chkWallet.setVisibility(View.GONE);
                        addwallet.setVisibility(View.VISIBLE);
                    }
                }

                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(true);
                    destinationMarker.setDraggable(true);
                }
            } else if (flowValue == 2) {
                imgBack.setVisibility(View.VISIBLE);
                lnrApproximate.startAnimation(slide_up);
                lnrApproximate.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 3) {
                imgBack.setVisibility(View.VISIBLE);
                lnrWaitingForProviders.setVisibility(View.VISIBLE);
                srcDestLayout.setVisibility(View.GONE);
                //sourceAndDestinationLayout.setVisibility(View.GONE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 4) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrProviderAccepted.startAnimation(slide_up);
                lnrProviderAccepted.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 5) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrInvoice.startAnimation(slide_up);
                imgSos.setVisibility(View.GONE);
                lnrInvoice.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 6) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrRateProvider.startAnimation(slide_up);
                lnrRateProvider.setVisibility(View.VISIBLE);
                LayerDrawable drawable = (LayerDrawable) ratingProviderRate.getProgressDrawable();
                drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                ratingProviderRate.setRating(5.0f);
                feedBackRating = "5";
                ratingProviderRate.setOnRatingBarChangeListener((ratingBar, rating, b) -> {
                    if (rating < 1.0f) {
                        ratingProviderRate.setRating(1.0f);
                        feedBackRating = "1";
                    }
                    feedBackRating = String.valueOf((int) rating);
                });
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 7) {
                imgBack.setVisibility(View.VISIBLE);
                ScheduleLayout.startAnimation(slide_up);
                ScheduleLayout.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 8) {
                // clear all views
                shadowBack.setVisibility(View.GONE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 9) {
                imgBack.setVisibility(View.VISIBLE);
                srcDestLayout.setVisibility(View.GONE);
                rtlStaticMarker.setVisibility(View.VISIBLE);
                shadowBack.setVisibility(View.GONE);
                frmDestination.setVisibility(View.GONE);
                bottom_sheet.setVisibility(View.GONE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 10) {
                destination.setHint(context.getResources().getString(R.string.extend_trip));
                //  frmDestination.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.VISIBLE);
                if (lnrProviderAccepted.getVisibility() == View.GONE) {
                    lnrProviderAccepted.startAnimation(slide_up);
                    lnrProviderAccepted.setVisibility(View.VISIBLE);
                }
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(true);
                }

                Utilities.getAddressUsingLatLng("destination", destination, context, "" + dest_lat,
                        "" + dest_lng);
            } else if (flowValue == 11) {
                imgBack.setVisibility(View.VISIBLE);
                applycouponlayout.startAnimation(slide_up);
                applycouponlayout.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));

            if (!success) {
                Utilities.print("Map:Style", "Style parsing failed.");
            } else {
                Utilities.print("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            Utilities.print("Map:Style", "Can't find style. Error: ");
        }

        mMap = googleMap;

        setupMap();

        customDialog.dismiss();

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
//                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    1);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }

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
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                return null;
            }
        };
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (is_track.equalsIgnoreCase("YES") && CurrentStatus.equalsIgnoreCase("PICKEDUP")) {
            LatLng markerLocation = destinationMarker.getPosition();
            extend_dest_lat = "" + markerLocation.latitude;
            extend_dest_lng = "" + markerLocation.longitude;
            showTripExtendAlert(extend_dest_lat, extend_dest_lng);
        } else {
            String title = "";
            if (marker != null && marker.getTitle() != null) {
                title = marker.getTitle();
                if (sourceMarker != null && title.equalsIgnoreCase("Source")) {
                    LatLng markerLocation = sourceMarker.getPosition();
                    source_lat = markerLocation.latitude + "";
                    source_lng = markerLocation.longitude + "";
                    source_address = Utilities.getAddressUsingLatLng("source", frmSource, context, "" + source_lat,
                            "" + source_lng);

                } else if (destinationMarker != null && title.equalsIgnoreCase("Destination")) {

                    LatLng markerLocation = destinationMarker.getPosition();

                    dest_lat = "" + markerLocation.latitude;
                    dest_lng = "" + markerLocation.longitude;

                    dest_address = Utilities.getAddressUsingLatLng("destination", frmDest, context, "" + dest_lat,
                            "" + dest_lng);
                }
                mMap.clear();
                setValuesForSourceAndDestination();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    initMap();
                    MapsInitializer.initialize(getActivity());
                    tryToGetlastKnowLocation();
                } /*else {
                    showPermissionReqDialog();
                }*/
                break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            case 3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showDialogForGPSIntent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("GPS is disabled in your device. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        (dialog, id) -> {
                            Intent callGPSSettingIntent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivity(callGPSSettingIntent);
                        });
        builder.setNegativeButton("Cancel",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert1 = builder.create();
        alert1.show();
        showDialogForGPSIntent();
    }

    private void SearchDataCompelete(int resultCode, Intent data) {
        if (parserTask != null) {
            parserTask = null;
        }
        if (resultCode == Activity.RESULT_OK) {
            if (marker != null) {
                marker.remove();
            }
            PlacePredictions placePredictions;
            placePredictions = (PlacePredictions) data.getSerializableExtra("Location Address");
            strPickLocation = data.getExtras().getString("pick_location");
            strPickType = data.getExtras().getString("type");
            if (strPickLocation.equalsIgnoreCase("yes")) {
                mMap.clear();
                flowValue = 9;
                layoutChanges();
                float zoomLevel = 16.0f; //This goes up to 21
                stopAnim();
            } else {
                frmSource.setText(source_address);
                frmDest.setText(dest_address);
                View view;
                TextView frmSource0 = rootView.findViewById(R.id.frmSource0);
                if (placePredictions != null) {
                    if (is_track.equalsIgnoreCase("YES") && CurrentStatus.equalsIgnoreCase("PICKEDUP")) {
                        extend_dest_lat = "" + placePredictions.strDestLatitude;
                        extend_dest_lng = "" + placePredictions.strDestLongitude;
                        showTripExtendAlert(extend_dest_lat, extend_dest_lng);
                    } else {
                        if (!placePredictions.strSourceAddress.equalsIgnoreCase("")) {
                            try {
                                source_lat = "" + placePredictions.strSourceLatitude;
                                source_lng = "" + placePredictions.strSourceLongitude;
                                Utilities.getAddressUsingLatLng("setlocationtext", frmSource0, context, "" + source_lat,
                                        "" + source_lng);


                                if (!placePredictions.strSourceLatitude.equalsIgnoreCase("")
                                        && !placePredictions.strSourceLongitude.equalsIgnoreCase("")) {
                                    double latitude = parseDouble(placePredictions.strSourceLatitude);
                                    double longitude = parseDouble(placePredictions.strSourceLongitude);

                                    LatLng location = new LatLng(latitude, longitude);

                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(location)
                                            .snippet(source_address)
                                            .title("source")
                                            .icon(bitmapDescriptorFromVector(R.drawable.user_marker));
                                    marker = mMap.addMarker(markerOptions);
                                    sourceMarker = mMap.addMarker(markerOptions);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (!placePredictions.strDestAddress.equalsIgnoreCase("")) {
                            dest_lat = "" + placePredictions.strDestLatitude;
                            dest_lng = "" + placePredictions.strDestLongitude;
                            Utilities.getAddressUsingLatLng("setlocationtext", frmSource0, context, "" + dest_lat,
                                    "" + dest_lng);
                            SharedHelper.putKey(context, "current_status", "2");
                            if (source_lat != null && source_lng != null && !source_lng.equalsIgnoreCase("")
                                    && !source_lat.equalsIgnoreCase("")) {
                                try {
                                    String url = getUrl(parseDouble(source_lat), parseDouble(source_lng)
                                            , parseDouble(dest_lat), parseDouble(dest_lng));
                                    FetchUrl fetchUrl = new FetchUrl();
                                    fetchUrl.execute(url);
                                    LatLng location = new LatLng(parseDouble(current_lat), parseDouble(current_lng));

                                    if (sourceMarker != null)
                                        sourceMarker.remove();
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(location)
                                            .snippet(source_address)
                                            .title("source")
                                            .icon(bitmapDescriptorFromVector(R.drawable.user_marker));
                                    marker = mMap.addMarker(markerOptions);
                                    sourceMarker = mMap.addMarker(markerOptions);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                                try {
                                    destLatLng = new LatLng(parseDouble(dest_lat), parseDouble(dest_lng));
                                    if (destinationMarker != null)
                                        destinationMarker.remove();
                                    MarkerOptions destMarker = new MarkerOptions()
                                            .position(destLatLng).title("destination").snippet(dest_address)
                                            .icon(bitmapDescriptorFromVector(R.drawable.provider_marker));
                                    destinationMarker = mMap.addMarker(destMarker);
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    builder.include(sourceMarker.getPosition());
                                    builder.include(destinationMarker.getPosition());
                                    LatLngBounds bounds = builder.build();
                                    int padding = 150; // offset from edges of the map in pixels
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                    mMap.moveCamera(cu);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (dest_address.equalsIgnoreCase("")) {
                            flowValue = 1;
//                            frmSource.setText(source_address);
                            getServiceList();
                        } else {
                            flowValue = 1;
                            if (cardInfoArrayList.size() > 0) {
                                getCardDetailsForPayment(cardInfoArrayList.get(0));
                            }
                            getServiceList();
                        }
                        layoutChanges();
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST) {
            SearchDataCompelete(resultCode, data);
        }
        if (requestCode == MainActivity.UPDATE_CITY) {
            initCity();
        }
        int ADD_CARD_CODE = 435;
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards();
                }
            }
        }
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                onlinepaymentdone();
            }
        } else if (requestCode == 7001) {
            if (resultCode == -1) {
                assert data != null;
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    onlinepaymentdone();
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(context, "Invalid", Toast.LENGTH_SHORT).show();

        if (requestCode == 5555) {
            if (resultCode == Activity.RESULT_OK) {
                CardInfo cardInfo = data.getParcelableExtra("card_info");
                getCardDetailsForPayment(cardInfo);
            }
        }


        if (requestCode == REQUEST_LOCATION) {
            Log.e("GPS Result Status", "onActivityResult: " + requestCode);
            Log.e("GPS Result Status", "onActivityResult: " + data);
        } else {
            Log.e("GPS Result Status else", "onActivityResult: " + requestCode);
            Log.e("GPS Result Status else", "onActivityResult: " + data);
        }
    }


    private void showTripExtendAlert(final String latitude, final String longitude) {
        Utilities.getAddressUsingLatLng("destination", frmDest, context, latitude, longitude);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.extend_trip_alert));
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            Utilities.getAddressUsingLatLng("destination", destination, context, latitude, longitude);
            extendTripAPI(latitude, longitude);
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> {
            //Reset to previous seletion menu in navigation
            dialog.dismiss();
        });
        builder.setCancelable(false);
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setOnShowListener(arg -> {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        });
        dialog.show();
    }

    private void extendTripAPI(final String latitude, final String longitude) {
        ApiInterface mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        Call<ResponseBody> call = mApiInterface.extendTrip("XMLHttpRequest", SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"),
                SharedHelper.getKey(context, "request_id"), latitude, longitude, SharedHelper.getKey(context, "extend_address"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("sUCESS", "bodyString" + bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            Toast.makeText(context, jsonObj.optString("message"), Toast.LENGTH_SHORT).show();
                            dest_lat = latitude;
                            dest_lng = longitude;
                            dest_address = SharedHelper.getKey(context, "extend_address");
                            mMap.clear();
                            setValuesForSourceAndDestination();
                            flowValue = 10;
                            layoutChanges();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    void showProviderPopup(JSONObject jsonObject) {
        lnrSearchAnimation.startAnimation(slide_up_top);
        lnrSearchAnimation.setVisibility(View.GONE);
        lnrProviderPopup.setVisibility(View.VISIBLE);
        lnrRequestProviders.setVisibility(View.GONE);

        Glide.with(activity).load(jsonObject.optString("image")).apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontAnimate()).into(imgProviderPopup);

        lnrPriceBase.setVisibility(View.GONE);
        lnrPricemin.setVisibility(View.GONE);
        lnrPricekm.setVisibility(View.GONE);

        if (jsonObject.optString("calculator").equalsIgnoreCase("MIN")
                || jsonObject.optString("calculator").equalsIgnoreCase("HOUR")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricemin.setVisibility(View.VISIBLE);
            if (jsonObject.optString("calculator").equalsIgnoreCase("MIN")) {
                lblCalculationType.setText("Minutes");
            } else {
                lblCalculationType.setText("Hours");
            }
        } else if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCE")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricekm.setVisibility(View.VISIBLE);
            lblCalculationType.setText("Distance");
        } else if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEMIN")
                || jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEHOUR")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricemin.setVisibility(View.VISIBLE);
            lnrPricekm.setVisibility(View.VISIBLE);
            if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEMIN")) {
                lblCalculationType.setText("Distance and Minutes");
            } else {
                lblCalculationType.setText("Distance and Hours");
            }
        }

        if (!jsonObject.optString("capacity").equalsIgnoreCase("null")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lblCapacity.setText(jsonObject.optString("capacity"));
            } else {
                lblCapacity.setText(jsonObject.optString("capacity") + " peoples");
            }
        } else {
            lblCapacity.setVisibility(View.GONE);
        }

        lblServiceName.setText("" + jsonObject.optString("name"));
        lblBasePricePopup.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("fixed"));
        lblPriceKm.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("price"));
        lblPriceMin.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("minute"));
        if (jsonObject.optString("description").equalsIgnoreCase("null")) {
            lblProviderDesc.setVisibility(View.GONE);
        } else {
            lblProviderDesc.setVisibility(View.VISIBLE);
            lblProviderDesc.setText("" + jsonObject.optString("description"));
        }
    }

    public void setValuesForApproximateLayout() {
        if (isInternet) {
            String surge = SharedHelper.getKey(context, "surge");
            String isextraprice = SharedHelper.getKey(context, "isextraprice");
            double totalprice = parseDouble(SharedHelper.getKey(context, "eta_total"));

            if (surge.equalsIgnoreCase("1")) {
                //  surgeDiscount.setVisibility(View.VISIBLE);
                surgeTxt.setVisibility(View.VISIBLE);
                // surgeDiscount.setText(SharedHelper.getKey(context, "surge_value"));
            } else if (isextraprice.equalsIgnoreCase("1")) {
                // surgeDiscount.setVisibility(View.GONE);
                surgeTxt.setVisibility(View.VISIBLE);
                totalprice += parseDouble(SharedHelper.getKey(context, "eta_total")) * parseDouble(SharedHelper.getKey(context, "extraprice")) / 100;
            } else {
                // surgeDiscount.setVisibility(View.GONE);
                surgeTxt.setVisibility(View.GONE);
            }

            lblApproxAmount.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "estimated_fare"));
            lblapproxtoal.setText(SharedHelper.getKey(context, "currency") + "" + df.format(totalprice));
            lblapproxtax.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "eta_tax"));
            lblapproxdistance.setText(SharedHelper.getKey(context, "distance") + " Km");
            lblEta.setText(SharedHelper.getKey(context, "eta_time"));
            if (!SharedHelper.getKey(context, "name").equalsIgnoreCase("")
                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase(null)
                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase("null")) {
                lblType.setText(SharedHelper.getKey(context, "name"));
            } else {
                lblType.setText("" + "Sedan");
            }

            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
        }
    }

    private void getCards() {
        Ion.with(this)
                .load(URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"))
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    // response contains both the headers and the string result
                    try {
                        if (response.getHeaders().code() == 200) {
                            try {
                                JSONArray jsonArray = new JSONArray(response.getResult());
                                if (jsonArray.length() > 0) {
                                    CardInfo cardInfo = new CardInfo();
                                    cardInfo.setCardId("CASH");
                                    cardInfo.setCardType("CASH");
                                    cardInfo.setLastFour("CASH");
                                    cardInfoArrayList.add(cardInfo);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject cardObj = jsonArray.getJSONObject(i);
                                        cardInfo = new CardInfo();
                                        cardInfo.setCardId(cardObj.optString("card_id"));
                                        cardInfo.setCardType(cardObj.optString("brand"));
                                        cardInfo.setLastFour(cardObj.optString("last_four"));
                                        cardInfoArrayList.add(cardInfo);
                                    }
                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        CardInfo cardInfo = new CardInfo();
                        cardInfo.setCardId("CASH");
                        cardInfo.setCardType("CASH");
                        cardInfo.setLastFour("CASH");
                        cardInfoArrayList.add(cardInfo);
                    }
                });

    }

    public void getservicelisticons() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_SERVICE_LIST_API + "?city_id=" + city.id, response -> {
            try {
                Utilities.print("GetServices", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.getJSONObject(i);
                    String key = "servictypeicon" + obj.getString("id");
                    String value = obj.getString("map_icon");
                    Log.v(key, value);
                    SharedHelper.putKey(context, key, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            try {
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
                                utils.displayMessage(getView(), context, errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                            }
                            flowValue = 1;
                            layoutChanges();
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("SERVICE_LIST");
                        } else if (response.statusCode == 422) {

                            json = UserApplication.trimMessage(new String(response.data));
                            if (!json.equals("") && json != null) {
                                utils.displayMessage(getView(), context, json);
                            } else {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                            }
                            flowValue = 1;
                            layoutChanges();
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));
                            flowValue = 1;
                            layoutChanges();
                        } else {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                            flowValue = 1;
                            layoutChanges();
                        }

                    } catch (Exception e) {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                        flowValue = 1;
                        layoutChanges();
                    }

                } else {
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                    flowValue = 1;
                    layoutChanges();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
                        + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        UserApplication.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    public void getServiceList() {
        TabLayout.Tab tab = tab_layout.getTabAt(0);
        tab_layout.selectTab(tab);
        rcvServiceTypes.setAdapter(null);
        SharedHelperJSONarray.putKey(context, "serviceresponse", null);
        try {
            customDialog = new CustomDialog(context);

            if (customDialog != null) {
                customDialog.show();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_SERVICE_LIST_API + "?city_id=" + city.id, response -> {

                try {
                    Utilities.print("GetServices", response.toString());
                    if (SharedHelper.getKey(context, "service_type").equalsIgnoreCase("")) {
                        SharedHelper.putKey(context, "service_type", "" + response.optJSONObject(0).optString("id"));
                    }
                    SharedHelperJSONarray.putKey(context, "serviceresponse", String.valueOf(response));
                    serviceresponse = response;
                    filteredserviceresponse = new JSONArray();
                    for (int i = 0; i < serviceresponse.length(); i++) {
                        JSONObject obj = null;
                        try {
                            obj = serviceresponse.getJSONObject(i);
                            if (TABVAL == 0) {
                                if (obj.getString("type").equals("economy")) {
                                    filteredserviceresponse.put(obj);
                                }
                            } else if (TABVAL == 1) {
                                if (obj.getString("type").equals("luxury")) {
                                    filteredserviceresponse.put(obj);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    if (response.length() > 0) {
                        currentPostion = 0;
                        ServiceListAdapter serviceListAdapter = new ServiceListAdapter(filteredserviceresponse);
                        rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity));
                        rcvServiceTypes.setAdapter(serviceListAdapter);
                        getProvidersList(SharedHelper.getKey(context, "service_type"));
                    } else {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.no_service));
                    }
                    if (mMap != null) {
                        mMap.clear();
                    }
                    setValuesForSourceAndDestination();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> {
                try {
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
                                    utils.displayMessage(getView(), context, errorObj.optString("message"));
                                } catch (Exception e) {
                                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                                }
                                flowValue = 1;
                                layoutChanges();
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("SERVICE_LIST");
                            } else if (response.statusCode == 422) {

                                json = UserApplication.trimMessage(new String(response.data));
                                if (!json.equals("") && json != null) {
                                    utils.displayMessage(getView(), context, json);
                                } else {
                                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                                }
                                flowValue = 1;
                                layoutChanges();
                            } else if (response.statusCode == 503) {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));
                                flowValue = 1;
                                layoutChanges();
                            } else {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                                flowValue = 1;
                                layoutChanges();
                            }

                        } catch (Exception e) {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                            flowValue = 1;
                            layoutChanges();
                        }

                    } else {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                        flowValue = 1;
                        layoutChanges();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
                            + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            UserApplication.getInstance().addToRequestQueue(jsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getApproximateFare() {
        try {
            customDialog = new CustomDialog(context);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            String constructedURL = URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                    "?s_latitude=" + source_lat
                    + "&s_longitude=" + source_lng
                    + "&d_latitude=" + dest_lat
                    + "&d_longitude=" + dest_lng
                    + "&service_type=" + SharedHelper.getKey(context, "service_type");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, constructedURL, object, response -> {
                if (response != null) {
                    if (!response.optString("estimated_fare").equalsIgnoreCase("")) {
                        Utilities.print("ApproximateResponse", response.toString());
                        SharedHelper.putKey(context, "estimated_fare", response.optString("estimated_fare"));
                        SharedHelper.putKey(context, "distance", response.optString("distance"));
                        SharedHelper.putKey(context, "eta_tax", response.optString("round_off"));
                        SharedHelper.putKey(context, "eta_total", response.optString("eta_total"));
                        SharedHelper.putKey(context, "eta_time", response.optString("time"));
                        SharedHelper.putKey(context, "surge", response.optString("surge"));
                        SharedHelper.putKey(context, "isextraprice", response.optString("isextraprice"));
                        SharedHelper.putKey(context, "extraprice", response.optString("extraprice"));
                        SharedHelper.putKey(context, "surge_value", response.optString("surge_value"));
                        setValuesForApproximateLayout();
                        double wallet_balance = response.optDouble("wallet_balance");
                        SharedHelper.putKey(context, "wallet_balance", "" + response.optDouble("wallet_balance"));

                        if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                            chkWallet.setVisibility(View.VISIBLE);
                        } else {
                            chkWallet.setVisibility(View.GONE);
                        }
                        flowValue = 2;
                        layoutChanges();
                    }
                }
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
                                utils.showAlert(context, errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.showAlert(context, context.getResources().getString(R.string.something_went_wrong) + e.toString());
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("APPROXIMATE_RATE");
                        } else if (response.statusCode == 422) {
                            json = UserApplication.trimMessage(new String(response.data));
                            if (!json.equals("") && json != null) {
                                utils.showAlert(context, json);
                            } else {
                                utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.showAlert(context, context.getResources().getString(R.string.server_down));
                        } else {
                            utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        utils.showAlert(context, context.getResources().getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
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

            UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void getProvidersList(final String strTag) {
        String providers_request;
        if (strTag.equals("")) {
            providers_request = URLHelper.GET_PROVIDERS_LIST_API + "?" +
                    "latitude=" + current_lat +
                    "&longitude=" + current_lng;

        } else {
            providers_request = URLHelper.GET_PROVIDERS_LIST_API + "?" +
                    "latitude=" + current_lat +
                    "&longitude=" + current_lng +
                    "&service=" + strTag;

        }

        Utilities.print("Get all providers", "" + providers_request);
        Utilities.print("service_type", "" + SharedHelper.getKey(context, "service_type"));
        Utilities.print("token", "" + SharedHelper.getKey(context, "access_token"));


//        for (int i = 0; i < lstProviderMarkers.size(); i++) {
//            lstProviderMarkers.get(i).remove();
//        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(providers_request, response -> {
//            Utilities.print("GetProvidersList", response.toString());
            Utilities.print("GetProvidersList", String.valueOf(response.length()));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < response.length(); i++) {

                try {
                    JSONObject jsonObj = response.getJSONObject(i);
                    Utilities.print("GetProvidersList", jsonObj.getString("latitude") + "," + jsonObj.getString("longitude"));
                    if (!jsonObj.getString("latitude").equalsIgnoreCase("") && !jsonObj.getString("longitude").equalsIgnoreCase("")) {

                        JSONObject serviceobj = jsonObj.getJSONObject("service");
                        Double proLat = parseDouble(jsonObj.getString("latitude"));
                        Double proLng = parseDouble(jsonObj.getString("longitude"));

                        if (mHashMap.containsKey(jsonObj.optInt("id"))) {
                            Marker marker = mHashMap.get(jsonObj.optInt("id"));
                            LatLng startPosition = marker.getPosition();
                            LatLng newPos = new LatLng(proLat, proLng);

                            marker.setPosition(newPos);
                            marker.setRotation(getBearing(startPosition, newPos));
                        } else {
                            String imgurl = SharedHelper.getKey(context, "servictypeicon" + serviceobj.getString("service_type_id"));
                            Log.v("Service_Icon : ", serviceobj.getString("service_type_id") + " URL: " + imgurl);
                            Activity activity = getActivity();
                            if (activity != null && !activity.isFinishing()) {
                                Glide.with(getActivity())
                                        .asBitmap()
                                        .load(imgurl)
                                        .fitCenter()
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                try {
                                                    if (!lstProviderMarkers.containsKey(jsonObj.getString("id"))) {
                                                        lstProviderMarkers.put(jsonObj.getString("id"), mMap.addMarker(
                                                                new MarkerOptions().anchor(0.5f, 0.75f).position((new LatLng(proLat, proLng))).rotation(0.0f)
                                                                        .snippet(jsonObj.getString("id")).icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(resource, 150, 150, false)))
                                                        ));
                                                    } else {
                                                        Marker oldMarker = lstProviderMarkers.get(jsonObj.getString("id"));
                                                        if (oldMarker != null) {
                                                            List<LatLng> latLngs = new ArrayList<>();
                                                            latLngs.add(oldMarker.getPosition());
                                                            latLngs.add(new LatLng(proLat, proLng));
                                                            if (calculateLocationDifference(latLngs.get(0), latLngs.get(1)) > 0)
                                                                animateCarOnMap(oldMarker, latLngs);
//                                                        MarkerAnimation.animateMarkerToGB(oldMarker, new LatLng(proLat, proLng), new LatLngInterpolator.Spherical());
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            }
//                            MarkerOptions markerOptions = new MarkerOptions()
//                                    .anchor(0.5f, 0.75f)
//                                    .position(new LatLng(proLat, proLng))
//                                    .rotation(0.0f)
//                                    .snippet(jsonObj.getString("id"))
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));


//                                mHashMap.put(jsonObj.optInt("id"), mMap.addMarker(markerOptions));

                            builder.include(new LatLng(proLat, proLng));
//                            }
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Utilities.print("GetProvidersList", String.valueOf(lstProviderMarkers.size()));
        }, error -> {
            String json = null;
            String Message;
            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {

                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));

                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            utils.showAlert(context, errorObj.optString("message"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response.statusCode == 401) {
                        refreshAccessToken("PROVIDERS_LIST");
                    } else if (response.statusCode == 422) {
                        json = UserApplication.trimMessage(new String(response.data));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        UserApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Take the emissions from the Rx Relay as a pair of LatLng and starts the animation of
     * car on map by taking the 2 pair of LatLng's.
     *
     * @param latLngs List of LatLng emitted by Rx Relay with size two.
     */
    private void animateCarOnMap(final Marker mrk, final List<LatLng> latLngs) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        mMap.animateCamera(mCameraUpdate);
        mrk.setPosition(latLngs.get(0));
        Float rotation = getBearing(latLngs.get(0), latLngs.get(1));
        if (!rotation.isNaN())
            mrk.setRotation(rotation);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                v = valueAnimator.getAnimatedFraction();
                double lng = v * latLngs.get(1).longitude + (1 - v)
                        * latLngs.get(0).longitude;
                double lat = v * latLngs.get(1).latitude + (1 - v)
                        * latLngs.get(0).latitude;
                LatLng newPos = new LatLng(lat, lng);
                mrk.setPosition(newPos);
                mrk.setAnchor(0.5f, 0.5f);
//                Float rotation = getBearing(latLngs.get(0), newPos);
//                if (!rotation.isNaN())
//                    mrk.setRotation(rotation);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition
                        (new CameraPosition.Builder().target(newPos)
                                .zoom(15.5f).build()));
            }
        });
        valueAnimator.start();
    }

    public void getApproximateFareAuto() {
        try {
            JSONObject object = new JSONObject();
            String constructedURL = URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                    "?s_latitude=" + source_lat
                    + "&s_longitude=" + source_lng
                    + "&d_latitude=" + dest_lat
                    + "&d_longitude=" + dest_lng
                    + "&service_type=" + SharedHelper.getKey(context, "service_type");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, constructedURL, object, response -> {
                if (response != null) {
                    if (!response.optString("estimated_fare").equalsIgnoreCase("")) {
                        Utilities.print("ApproximateResponse", response.toString());
                        SharedHelper.putKey(context, "estimated_fare", response.optString("estimated_fare"));
                        SharedHelper.putKey(context, "distance", response.optString("distance"));
                        SharedHelper.putKey(context, "eta_tax", response.optString("round_off"));
                        SharedHelper.putKey(context, "eta_total", response.optString("eta_total"));
                        SharedHelper.putKey(context, "eta_time", response.optString("time"));
                        SharedHelper.putKey(context, "surge", response.optString("surge"));
                        SharedHelper.putKey(context, "surge_value", response.optString("surge_value"));
                        setValuesForApproximateLayout();
                        double wallet_balance = response.optDouble("wallet_balance");
                        SharedHelper.putKey(context, "wallet_balance", "" + response.optDouble("wallet_balance"));

                        if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                            chkWallet.setVisibility(View.VISIBLE);
                        } else {
                            chkWallet.setVisibility(View.GONE);
                        }

                    }
                }
            }, error -> {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.showAlert(context, errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.showAlert(context, context.getResources().getString(R.string.something_went_wrong) + e.toString());
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("APPROXIMATE_RATE");
                        } else if (response.statusCode == 422) {
                            json = UserApplication.trimMessage(new String(response.data));
                            if (!json.equals("") && json != null) {
                                utils.showAlert(context, json);
                            } else {
                                utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.showAlert(context, context.getResources().getString(R.string.server_down));
                        } else {
                            utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        utils.showAlert(context, context.getResources().getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
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

            UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRequest() {
        customDialog.show();
        getApproximateFareAuto();
        new Handler().postDelayed(this::sendRequestAuto, 3000);
    }

    private void sendRequestAuto() {
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("s_latitude", source_lat);
            object.put("s_longitude", source_lng);
            object.put("d_latitude", dest_lat);
            object.put("d_longitude", dest_lng);
            object.put("s_address", source_address);
            object.put("d_address", dest_address);
            object.put("service_type", SharedHelper.getKey(context, "service_type"));
            object.put("distance", SharedHelper.getKey(context, "distance"));
            object.put("eta_total", SharedHelper.getKey(context, "eta_totals"));
            object.put("specialNote", specialNote.getText().toString().trim());
            if (SharedHelper.getKey(context, "isSchedule").equals("Yes")) {
                object.put("schedule_date", scheduledDate);
                object.put("schedule_time", scheduledTime);
            }

            Log.e("Schedule Request", "sendRequest: " + object);

            if (chkWallet.isChecked()) {
                object.put("use_wallet", 1);
            } else {
                object.put("use_wallet", 0);
            }

            if (chkReturn.isChecked()) {
                object.put("return_trip", 1);
            } else {
                object.put("return_trip", 0);
            }

            if (SharedHelper.getKey(context, "payment_mode").equals("CASH")) {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
            } else {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                object.put("card_id", SharedHelper.getKey(context, "card_id"));
            }
            Utilities.print("SendRequestInput", "" + object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserApplication.getInstance().cancelRequestInQueue("send_request");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.SEND_REQUEST_API, object, response -> {
            if (response != null) {
                Utilities.print("SendRequestResponse", response.toString());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                if (response.optString("request_id", "").equals("")) {
                    utils.displayMessage(getView(), context, response.optString("message"));
                } else {
                    SharedHelper.putKey(context, "current_status", "");
                    SharedHelper.putKey(context, "request_id", "" + response.optString("request_id"));
                    scheduleTrip = !scheduledDate.equalsIgnoreCase("") && !scheduledTime.equalsIgnoreCase("");
                    flowValue = 3;
                    layoutChanges();
                }
            }
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json = null;
            String Message;
            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));
                    Log.e("SendREquest", "onErrorResponse: " + errorObj.optString("message"));
                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            utils.showAlert(context, errorObj.optString("error"));
                        } catch (Exception e) {
                            utils.showAlert(context, context.getResources().getString(R.string.something_went_wrong));
                        }
                    } else if (response.statusCode == 401) {
                        refreshAccessToken("SEND_REQUEST");
                    } else if (response.statusCode == 422) {
                        json = UserApplication.trimMessage(new String(response.data));
                        if (!json.equals("") && json != null) {
                            utils.showAlert(context, json);
                        } else {
                            utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
                        }
                    } else if (response.statusCode == 503) {
                        utils.showAlert(context, context.getResources().getString(R.string.server_down));
                    } else {
                        utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
                    }
                } catch (Exception e) {
                    utils.showAlert(context, context.getResources().getString(R.string.something_went_wrong));
                }
            } else {
                utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
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

        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void cancelRequest() {
        customDialog = new CustomDialog(context);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("cancel_reason", cancalReason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, response -> {
            Utilities.print("CancelRequestResponse", response.toString());
            Toast.makeText(context, context.getResources().getString(R.string.request_cancel), Toast.LENGTH_SHORT).show();
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            mapClear();
            SharedHelper.putKey(context, "request_id", "");
            flowValue = 0;
            PreviousStatus = "";
            layoutChanges();
            setupMap();
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json = null;
            String Message;
            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                flowValue = 4;
                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));

                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            utils.displayMessage(getView(), context, errorObj.optString("message"));
                        } catch (Exception e) {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                        }
                        layoutChanges();
                    } else if (response.statusCode == 401) {
                        refreshAccessToken("CANCEL_REQUEST");
                    } else if (response.statusCode == 422) {

                        json = UserApplication.trimMessage(new String(response.data));
                        if (!json.equals("") && json != null) {
                            utils.displayMessage(getView(), context, json);
                        } else {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                        }
                        layoutChanges();
                    } else if (response.statusCode == 503) {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));
                        layoutChanges();
                    } else {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                        layoutChanges();
                    }

                } catch (Exception e) {
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                    layoutChanges();
                }

            } else {
                utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                layoutChanges();
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

        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void setValuesForSourceAndDestination() {
        if (isInternet) {
            if (!source_lat.equalsIgnoreCase("")) {
                if (!source_address.equalsIgnoreCase("")) {
//                    frmSource.setText(source_address);
                } else {
                    Utilities.getAddressUsingLatLng("source", frmSource, context, "" + source_lat, "" + source_lng);
                }
            } else {
                Utilities.getAddressUsingLatLng("source", frmSource, context, "" + current_lat, "" + current_lng);
                Utilities.getAddressUsingLatLng("source", frmSource, context, "" + current_lat, "" + current_lng);
            }

            if (!dest_lat.equalsIgnoreCase("")) {
                if (is_track.equalsIgnoreCase("YES") &&
                        (CurrentStatus.equalsIgnoreCase("STARTED") || CurrentStatus.equalsIgnoreCase("PICKEDUP")
                                || CurrentStatus.equalsIgnoreCase("ARRIVED"))) {
                    // Source Destination should not visible at the track
//                    destination.setText(SharedHelper.getKey(context, "extend_address"));
                } else {
                    destination.setText(SharedHelper.getKey(context, "destination"));
                    srcDestLayout.setVisibility(View.VISIBLE);
                }
            }


            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(parseDouble(source_lat), parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(parseDouble(dest_lat), parseDouble(dest_lng));
            }

            if (sourceLatLng != null && destLatLng != null) {
                Utilities.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
                String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.execute(url);
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
            Utilities.print("SignUpResponse", response.toString());
            SharedHelper.putKey(context, "access_token", response.optString("access_token"));
            SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
            SharedHelper.putKey(context, "token_type", response.optString("token_type"));
            if (tag.equalsIgnoreCase("SERVICE_LIST")) {
                getServiceList();
            } else if (tag.equalsIgnoreCase("APPROXIMATE_RATE")) {
                getApproximateFare();
            } else if (tag.equalsIgnoreCase("SEND_REQUEST")) {
                sendRequest();
            } else if (tag.equalsIgnoreCase("CANCEL_REQUEST")) {
                cancelRequest();
            } else if (tag.equalsIgnoreCase("PROVIDERS_LIST")) {
                getProvidersList("");
            } else if (tag.equalsIgnoreCase("SUBMIT_REVIEW")) {
                submitReviewCall();
            } else if (tag.equalsIgnoreCase("PAY_NOW")) {
                payNow();
            }
        }, error -> {
            String json = "";
            NetworkResponse response = error.networkResponse;

            if (response != null && response.data != null) {
                SharedHelper.putKey(context, "loggedIn", context.getResources().getString(R.string.False));
                utils.GoToBeginActivity(getActivity());
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


    private void showChooser() {
        Intent intent = new Intent(getActivity(), Payment.class);
        startActivityForResult(intent, 5555);
    }

    private void getCardDetailsForPayment(CardInfo cardInfo) {
        if (cardInfo.getLastFour().equals("CASH")) {
            SharedHelper.putKey(context, "payment_mode", "CASH");
            imgPaymentType.setImageResource(R.drawable.money_icon);
            lblPaymentType.setText("CASH");
        } else {
            SharedHelper.putKey(context, "card_id", cardInfo.getCardId());
            SharedHelper.putKey(context, "payment_mode", "CARD");
            imgPaymentType.setImageResource(R.drawable.visa);
            lblPaymentType.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
        }
    }

    public void payNow() {
        if (!SharedHelper.getKey(context, "payableamount").trim().equals("0") && !SharedHelper.getKey(context, "payableamount").trim().equals("0.00") && !SharedHelper.getKey(context, "payableamount").trim().equals("")) {
            getpaymentcard();
        }
    }

    public void payonline() {

        customDialog = new CustomDialog(context);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("payment_mode", paymentMode);
            object.put("is_paid", isPaid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.PAY_NOW_API, object, response -> {
            Utilities.print("PayNowRequestResponse", response.toString());
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            flowValue = 6;
            layoutChanges();
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json = "";
            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));

                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            utils.displayMessage(getView(), context, errorObj.optString("message"));
                        } catch (Exception e) {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                        }
                    } else if (response.statusCode == 401) {
                        refreshAccessToken("PAY_NOW");
                    } else if (response.statusCode == 422) {

                        json = UserApplication.trimMessage(new String(response.data));
                        if (!json.equals("") && json != null) {
                            utils.displayMessage(getView(), context, json);
                        } else {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                        }
                    } else if (response.statusCode == 503) {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));
                    } else {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                }

            } else {
                utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
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
        urlString.append(Searchlatitude).append(",").append(Searchlongitude); // append lat long of current location to show nearby results.
        urlString.append("&radius=500&language=en");
        urlString.append("&components=country:zm");
        urlString.append("&key=").append(getResources().getString(R.string.google_map_api));

        Log.d("FINAL URL:::   ", urlString.toString());
        return urlString.toString();
    }

    private void getpaymentcard() {
        RadioGroup paymentgroup;
        RadioButton stripe, paypal, flutterwave, upi, razorpay;
        ImageView stripeimg, paypalimg, flutterwaveimg, upiimg, razorpayimg;

        payment_method_card.startAnimation(slide_up);
        payment_method_card.setVisibility(View.VISIBLE);
        stripe = activity.findViewById(R.id.stripe);
        paypal = activity.findViewById(R.id.paypal);
        upi = activity.findViewById(R.id.upi);
        razorpay = activity.findViewById(R.id.razorpay);
        flutterwave = activity.findViewById(R.id.flutterwave);
        paymentgroup = activity.findViewById(R.id.paymentgroup);
        paypalimg = activity.findViewById(R.id.paypalimg);
        stripeimg = activity.findViewById(R.id.stripeimg);
        razorpayimg = activity.findViewById(R.id.razorpayimg);
        upiimg = activity.findViewById(R.id.upiimg);
        flutterwaveimg = activity.findViewById(R.id.flutterwaveimg);

        if (SharedHelper.getKey(context, "CARD").equalsIgnoreCase("0")) {
            stripe.setVisibility(View.GONE);
            stripeimg.setVisibility(View.GONE);
            stripe.setChecked(false);
            paypal.setChecked(true);
        }


        if (SharedHelper.getKey(context, "paypal").equalsIgnoreCase("0")) {
            paypal.setVisibility(View.GONE);
            paypalimg.setVisibility(View.GONE);
            paypal.setChecked(false);
            flutterwave.setChecked(true);
        }


        if (SharedHelper.getKey(context, "rave").equalsIgnoreCase("0")) {
            flutterwave.setVisibility(View.GONE);
            flutterwaveimg.setVisibility(View.GONE);
            flutterwave.setChecked(false);
            if (paymentgroup.getChildCount() == 0) {
                Toast.makeText(context, "No Payment Method Available ! Pay By Cash", Toast.LENGTH_SHORT).show();
                payment_method_card.setVisibility(View.GONE);
            }
        }


        if (SharedHelper.getKey(context, "UPI").equalsIgnoreCase("0")) {
            upi.setVisibility(View.GONE);
            upiimg.setVisibility(View.GONE);
            upi.setChecked(false);
            razorpay.setChecked(true);
        }

        if (SharedHelper.getKey(context, "razor").equalsIgnoreCase("0")) {
            razorpay.setVisibility(View.GONE);
            razorpayimg.setVisibility(View.GONE);
            if (paymentgroup.getChildCount() == 0) {
                Toast.makeText(context, "No Payment Method Available !", Toast.LENGTH_SHORT).show();
                btnPayNow_online.setEnabled(false);
            }
        }

        btnPayNow_online.setOnClickListener(v -> {
            int selectedid = paymentgroup.getCheckedRadioButtonId();
            @SuppressLint("CutPasteId") RadioButton rbtn = activity.findViewById(R.id.stripe);
            @SuppressLint("CutPasteId") RadioButton rbtn2 = activity.findViewById(R.id.paypal);
            @SuppressLint("CutPasteId") RadioButton rbtn3 = activity.findViewById(R.id.flutterwave);
            @SuppressLint("CutPasteId") RadioButton rbtn4 = activity.findViewById(R.id.upi);
            @SuppressLint("CutPasteId") RadioButton rbtn5 = activity.findViewById(R.id.razorpay);
            getActivity().runOnUiThread(() -> {
                Double amount = Double.valueOf(SharedHelper.getKey(context, "payableamount"));
                if (rbtn.getId() == selectedid) {
                    PaymentType = "CARD";
                    stripepayment(amount);
                } else if (rbtn2.getId() == selectedid) {
                    PaymentType = "PAYPAL";
                    paypalpayment(amount);
                } else if (rbtn3.getId() == selectedid) {
                    PaymentType = "CARD";
                    makeflutterwavePayment();
                } else if (rbtn4.getId() == selectedid) {
                    PaymentType = "UPI";
                    allUPIPayments(amount);
                } else if (rbtn5.getId() == selectedid) {
                    PaymentType = "ONLINE";
                    startrazorpaypayment(amount);
                }
            });
        });

    }

    public void startrazorpaypayment(Double amount) {

        Checkout checkout = new Checkout();
        checkout.setKeyID(SharedHelper.getKey(context, "razor_key"));
        checkout.setImage(R.mipmap.ic_launcher);
        try {
            JSONObject options = new JSONObject();

            options.put("name", getResources().getString(R.string.app_name));
            options.put("description", "Add Wallet Amount");
            options.put("currency", "INR");
            options.put("prefill.email", SharedHelper.getKey(context, "email"));
            options.put("prefill.contact", SharedHelper.getKey(context, "mobile"));
            options.put("amount", Integer.parseInt(String.valueOf(amount)) * 100);
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    private void stripepayment(Double amount) {
//        update_amount = Double.parseDouble(money_et.getText().toString().trim());
//        lnrAddmoney.setVisibility(View.VISIBLE);
    }

    private void onlinepaymentdone() {
        customDialog.show();
        Call<Errorresponse> call = com.kwendaapp.rideo.API.RetrofitClient.getInstance().getApi().paymentdone(SharedHelper.getKey(context, "request_id"));
        call.enqueue(new Callback<Errorresponse>() {
            @Override
            public void onResponse(Call<Errorresponse> call, retrofit2.Response<Errorresponse> response) {
                if (response.isSuccessful()) {
                    if (!response.body().isError()) {
                        Toast.makeText(context, "Payment successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Some error occurred ! Pay by CASH", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Some error occurred ! Pay by CASH", Toast.LENGTH_SHORT).show();
                }
                customDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Errorresponse> call, Throwable t) {
                Toast.makeText(context, "Some error occurred ! Pay by CASH", Toast.LENGTH_SHORT).show();
                customDialog.dismiss();
            }
        });
    }

    private void paypalpayment(Double amount) {
        final int PAYPAL_REQUEST_CODE = 7001;
        String clientid = SharedHelper.getKey(context, "paypal_client_id");
        if (clientid != null) {
            config = new PayPalConfiguration()
                    .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                    .clientId(clientid);
            Intent intent = new Intent(getActivity(), PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            getActivity().startService(intent);
        }

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(amount), "USD",
                "Add Wallet Amount", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent2 = new Intent(getActivity(), PaymentActivity.class);
        intent2.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent2.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent2, PAYPAL_REQUEST_CODE);
    }

    private void allUPIPayments(Double amount) {
        PaymentDetail payment = new PaymentDetail(
                SharedHelper.getKey(getActivity(), "UPI_key"),
                SharedHelper.getKey(getActivity(), "first_name"),
                "",
                generateString(),
                getResources().getString(R.string.booking_id) + "Ride Payment Booking ID: " + booking_id,
                String.valueOf(amount));

        new UpiPayment(getActivity())
                .setPaymentDetail(payment)
                .setUpiApps(UpiPayment.getUPI_APPS())
                .setCallBackListener(new UpiPayment.OnUpiPaymentListener() {
                    @Override
                    public void onSuccess(@NotNull TransactionDetails transactionDetails) {
                        onlinepaymentdone();
                    }

                    @Override
                    public void onSubmitted(@NotNull TransactionDetails transactionDetails) {

                    }


                    @Override
                    public void onError(@NotNull String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }

                }).pay();

    }

    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }


    public void makeflutterwavePayment() {
        String email = SharedHelper.getKey(context, "email");
        String fName = SharedHelper.getKey(context, "first_name");
        String lName = SharedHelper.getKey(context, "last_name");
        String txRef = email + " " + UUID.randomUUID().toString();
        String publicKey = SharedHelper.getKey(context, "rave_publicKey");
        String encryptionKey = SharedHelper.getKey(context, "rave_encryptionKey");



                /*
                Create instance of RavePayManager
                 */
        Double amount = Double.valueOf(SharedHelper.getKey(context, "payableamount"));
        new RaveUiManager(this).setAmount(amount)
                .setCountry(SharedHelper.getKey(context, "rave_country"))
                .setCurrency("ZMW")
                .setEmail(email)
                .setfName(fName)
                .setlName(lName)
                .setPublicKey(publicKey)
                .setEncryptionKey(encryptionKey)
                .setTxRef(txRef)
//                .acceptFrancMobileMoneyPayments(true)
                .acceptGHMobileMoneyPayments(true)
                .acceptRwfMobileMoneyPayments(true)
                .acceptUgMobileMoneyPayments(true)
                .acceptZmMobileMoneyPayments(true)
                .acceptAccountPayments(true)
                .acceptAchPayments(true)
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .acceptBankTransferPayments(true)
                .acceptMpesaPayments(true)
                .acceptUssdPayments(true)
                .acceptGHMobileMoneyPayments(true)
                .onStagingEnv(false).
                allowSaveCardFeature(true)
                .initialize();
    }


    //RazorPayResponse
    @Override
    public void onPaymentSuccess(String s) {
        onlinepaymentdone();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(activity, "" + s, Toast.LENGTH_SHORT).show();
    }


    private void checkStatus() {
        try {

            Utilities.print("Handler", "Inside");
            if (isInternet) {
                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        URLHelper.REQUEST_STATUS_CHECK_API, null, response -> {
                    SharedHelper.putKey(context, "req_status", "");
                    try {
                        if (customDialog != null && customDialog.isShowing()) {
                            customDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    reqStatus = "";
                    Utilities.print("Response", "" + response.toString());

                    if (response.optJSONArray("data") != null && response.optJSONArray("data").length() > 0) {
                        Utilities.print("response", "not null");
                        try {
                            JSONArray requestStatusCheck = response.optJSONArray("data");
                            JSONObject requestStatusCheckObject = requestStatusCheck.getJSONObject(0);
                            //Driver Detail
                            if (requestStatusCheckObject.optJSONObject("provider") != null) {
                                driver = new Driver();
                                driver.setFname(requestStatusCheckObject.optJSONObject("provider").optString("first_name"));
                                driver.setLname(requestStatusCheckObject.optJSONObject("provider").optString("last_name"));
                                driver.setEmail(requestStatusCheckObject.optJSONObject("provider").optString("email"));
                                driver.setMobile(requestStatusCheckObject.optJSONObject("provider").optString("mobile"));
                                driver.setImg(requestStatusCheckObject.optJSONObject("provider").optString("avatar"));
                                driver.setRating(requestStatusCheckObject.optJSONObject("provider").optString("rating"));
                            }
                            String status = requestStatusCheckObject.optString("status");
                            is_track = requestStatusCheckObject.optString("is_track");
                            SharedHelper.putKey(context, "track_status", is_track);
                            reqStatus = requestStatusCheckObject.optString("status");
                            SharedHelper.putKey(context, "req_status", requestStatusCheckObject.optString("status"));
                            String wallet = requestStatusCheckObject.optString("use_wallet");
                            source_lat = requestStatusCheckObject.optString("s_latitude");
                            source_lng = requestStatusCheckObject.optString("s_longitude");
                            dest_lat = requestStatusCheckObject.optString("d_latitude");
                            dest_lng = requestStatusCheckObject.optString("d_longitude");
                            SharedHelper.putKey(context, "booking_id", requestStatusCheckObject.optString("booking_id"));
                            SharedHelper.putKey(context, "uid", requestStatusCheckObject.optString("user_id"));
                            SharedHelper.putKey(context, "pid", requestStatusCheckObject.optString("provider_id"));

                            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                                LatLng myLocation = new LatLng(parseDouble(source_lat), parseDouble(source_lng));
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                            }

                            // surge price
                            if (requestStatusCheckObject.optString("surge").equalsIgnoreCase("1")) {
                                lblSurgePrice.setVisibility(View.VISIBLE);
                            } else {
                                lblSurgePrice.setVisibility(View.GONE);
                            }

                            setTrackStatus();

                            Utilities.print("PreviousStatus", "" + PreviousStatus);

                            if (!PreviousStatus.equals(status)) {
                                mMap.clear();
                                PreviousStatus = status;
                                flowValue = 8;
                                layoutChanges();
                                SharedHelper.putKey(context, "request_id", "" + requestStatusCheckObject.optString("id"));
                                reCreateMap();
                                CurrentStatus = status;
                                Utilities.print("ResponseStatus", "SavedCurrentStatus: " + CurrentStatus + " Status: " + status);
                                switch (status) {
                                    case "SEARCHING":
                                        show(lnrWaitingForProviders);
                                        break;
                                    case "CANCELLED":
                                        if (reasonDialog != null) {
                                            if (reasonDialog.isShowing()) {
                                                reasonDialog.dismiss();
                                            }
                                        }
                                        if (cancelRideDialog != null) {
                                            if (cancelRideDialog.isShowing()) {
                                                cancelRideDialog.dismiss();
                                            }
                                        }
                                        imgSos.setVisibility(View.GONE);
                                        break;
                                    case "ACCEPTED":
                                        try {
                                            JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                            JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                            JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                            SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                            lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                            if (provider.optString("avatar").startsWith("http"))
                                                Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                            else
                                                Picasso.get().load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                            lblServiceRequested.setText(service_type.optString("name"));
                                            lblModelNumber.setText(provider_service.optString("service_model"));
                                            lblModelNumber2.setText(provider_service.optString("service_number"));
                                            Picasso.get().load(service_type.optString("image"))
                                                    .placeholder(R.drawable.car_select).error(R.drawable.car_select)
                                                    .into(imgServiceRequested);
                                            ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                            //lnrAfterAcceptedStatus.setVisibility(View.GONE);
                                            lblStatus.setText(context.getResources().getString(R.string.arriving));
                                            AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                            btnCancelTriptext.setText(context.getResources().getString(R.string.cancel_trip));
                                            show(lnrProviderAccepted);
                                            flowValue = 9;
                                            layoutChanges();
                                            if (is_track.equalsIgnoreCase("YES")) {
                                                flowValue = 10;
                                                // txtChange.setVisibility(View.GONE);
                                            } else {
                                                flowValue = 4;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case "STARTED":
                                        try {
                                            JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                            JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                            JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                            SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                            lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                            lblOTP.setText(requestStatusCheckObject.optString("otp"));
                                            if (provider.optString("avatar").startsWith("http"))
                                                Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                            else
                                                Picasso.get().load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                            lblServiceRequested.setText(service_type.optString("name"));
                                            lblModelNumber.setText(provider_service.optString("service_model"));
                                            lblModelNumber2.setText(provider_service.optString("service_number"));
                                            Picasso.get().load(service_type.optString("image")).placeholder(R.drawable.car_select)
                                                    .error(R.drawable.car_select).into(imgServiceRequested);
                                            ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                            //lnrAfterAcceptedStatus.setVisibility(View.GONE);
                                            lblStatus.setText(context.getResources().getString(R.string.arriving));
                                            btnCancelTriptext.setText(context.getResources().getString(R.string.cancel_trip));
                                            AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                            if (is_track.equalsIgnoreCase("YES")) {
                                                flowValue = 10;
                                                //txtChange.setVisibility(View.GONE);
                                            } else {
                                                flowValue = 4;
                                            }
                                            layoutChanges();
                                            if (!requestStatusCheckObject.optString("schedule_at").equalsIgnoreCase("null")) {
                                                SharedHelper.putKey(context, "current_status", "");
                                                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                                                intent.putExtra("tag", "upcoming");
                                                startActivity(intent);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case "ARRIVED":
                                        Utilities.print("MyTest", "ARRIVED");
                                        try {
                                            Utilities.print("MyTest", "ARRIVED TRY");
                                            JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                            JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                            JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                            lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                            lblOTP.setText(requestStatusCheckObject.optString("otp"));
                                            if (provider.optString("avatar").startsWith("http"))
                                                Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                            else
                                                Picasso.get().load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                            lblServiceRequested.setText(service_type.optString("name"));
                                            lblModelNumber.setText(provider_service.optString("service_model"));
                                            lblModelNumber2.setText(provider_service.optString("service_number"));
                                            Picasso.get().load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                            ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                            lnrAfterAcceptedStatus.setVisibility(View.VISIBLE);

                                            lblStatus.setText(context.getResources().getString(R.string.arrived));
                                            btnCancelTriptext.setText(context.getResources().getString(R.string.cancel_trip));
                                            AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                            if (is_track.equalsIgnoreCase("YES")) {
                                                flowValue = 10;
                                                //  txtChange.setVisibility(View.GONE);
                                            } else {
                                                flowValue = 4;
                                            }
                                            layoutChanges();
                                        } catch (Exception e) {
                                            Utilities.print("MyTest", "ARRIVED CATCH");
                                            e.printStackTrace();
                                        }
                                        break;

                                    case "PICKEDUP":
                                        try {
                                            JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                            JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                            JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                            lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                            lblOTP.setText(requestStatusCheckObject.optString("otp"));
                                            OTPLayout.setVisibility(View.GONE);
                                            if (provider.optString("avatar").startsWith("http"))
                                                Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                            else
                                                Picasso.get().load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                            lblServiceRequested.setText(service_type.optString("name"));
                                            lblModelNumber.setText(provider_service.optString("service_model"));
                                            lblModelNumber2.setText(provider_service.optString("service_number"));
                                            Picasso.get().load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                            ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                            lnrAfterAcceptedStatus.setVisibility(View.VISIBLE);
                                            imgSos.setVisibility(View.VISIBLE);
                                            lblStatus.setText(context.getResources().getString(R.string.picked_up));
                                            btnCancelTrip.setImageDrawable(getResources().getDrawable(R.drawable.ic_share_black_24dp));
                                            btnCancelTriptext.setText(context.getResources().getString(R.string.share));
                                            AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                            if (is_track.equalsIgnoreCase("YES")) {
                                                flowValue = 10;
                                                // txtChange.setVisibility(View.VISIBLE);
                                            } else {
                                                flowValue = 4;
                                                // txtChange.setVisibility(View.GONE);
                                            }
                                            layoutChanges();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case "DROPPED":
                                        imgSos.setVisibility(View.VISIBLE);
                                        try {
                                            JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                            if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                isPaid = requestStatusCheckObject.optString("paid");
                                                totalRideAmount = payment.optInt("payable");
                                                walletAmountDetected = payment.optInt("wallet");
                                                couponAmountDetected = payment.optInt("discount");
                                                paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                lblBasePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("fixed"));
                                                lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("tax"));
                                                lbDistanceCovered.setText(requestStatusCheckObject.optString("distance") + " KM");

                                                lblDistancePrice.setText(SharedHelper.getKey(context, "currency")
                                                        + "" + payment.optString("distance"));
                                                lblTimeTaken.setText(requestStatusCheckObject.optString("travel_time") + " Min");


                                                dh_source.setText(requestStatusCheckObject.optString("s_address"));
                                                dh_destination.setText(requestStatusCheckObject.optString("d_address"));
                                                lblTimePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("t_price"));
                                                lblDisPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("discount"));

                                                lblDiscountPrice.setText(SharedHelper.getKey(context, "currency") + "" + couponAmountDetected);
                                                lblWalletPrice.setText(SharedHelper.getKey(context, "currency") + "" + walletAmountDetected + ".00");
                                                lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("payable"));

                                                SharedHelper.putKey(context, "payableamount", payment.optString("payable"));

                                                //Review values set
                                                lblProviderNameRate.setText(context.getResources().getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http")) {
                                                    Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                } else {
                                                    Picasso.get().load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                }

                                                if (requestStatusCheckObject.optString("booking_id") != null &&
                                                        !requestStatusCheckObject.optString("booking_id").equalsIgnoreCase("")) {
                                                    booking_id.setText(requestStatusCheckObject.optString("booking_id"));
                                                } else {
                                                    bookingIDLayout.setVisibility(View.GONE);
                                                    booking_id.setVisibility(View.GONE);
                                                }

                                                if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH") && walletAmountDetected > 0
                                                        && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected == 0 &&
                                                        totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 &&
                                                        totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 &&
                                                        totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected == 0 &&
                                                        totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH") && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case "COMPLETED":
                                        try {
                                            if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                isPaid = requestStatusCheckObject.optString("paid");
                                                paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                imgSos.setVisibility(View.GONE);
                                                totalRideAmount = payment.optInt("payable");
                                                walletAmountDetected = payment.optInt("wallet");
                                                couponAmountDetected = payment.optInt("discount");

                                                lblBasePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                        + payment.optString("fixed"));
                                                lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                        + payment.optString("tax"));
                                                lblDistancePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                        + payment.optString("distance"));
                                                lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                        + payment.optString("payable"));
                                                lblDiscountPrice.setText(SharedHelper.getKey(context, "currency") + "" + couponAmountDetected);

                                                lblTimeTaken.setText(requestStatusCheckObject.optString("travel_time") + " Min");

                                                lbDistanceCovered.setText(requestStatusCheckObject.optString("distance") + " KM");

                                                lblWalletPrice.setText(SharedHelper.getKey(context, "currency") + "" + walletAmountDetected + ".00");

                                                dh_source.setText(requestStatusCheckObject.optString("s_address"));
                                                dh_destination.setText(requestStatusCheckObject.optString("d_address"));
                                                lblTimePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("t_price"));
                                                lblDisPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("discount"));

                                                //Review values set
                                                lblProviderNameRate.setText(context.getResources().getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http")) {
                                                    Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                } else {
                                                    Picasso.get().load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                }

                                                if (requestStatusCheckObject.optString("booking_id") != null &&
                                                        !requestStatusCheckObject.optString("booking_id").equalsIgnoreCase("")) {
                                                    booking_id.setText(requestStatusCheckObject.optString("booking_id"));
                                                } else {
                                                    bookingIDLayout.setVisibility(View.GONE);
                                                    booking_id.setVisibility(View.GONE);
                                                }

                                                if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0
                                                        && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("ONLINE");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    btnPayNow.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();

                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    //  btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                }

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                }
                            }
                            if ("ACCEPTED".equals(status) || "STARTED".equals(status) ||
                                    "ARRIVED".equals(status) || "PICKEDUP".equals(status) || "DROPPED".equals(status)) {
                                Utilities.print("Livenavigation", "" + status);
                                Utilities.print("Destination Current Lat", "" + requestStatusCheckObject.getJSONObject("provider").optString("latitude"));
                                Utilities.print("Destination Current Lng", "" + requestStatusCheckObject.getJSONObject("provider").optString("longitude"));
                                liveNavigation(status, requestStatusCheckObject.getJSONObject("provider").optString("latitude"),
                                        requestStatusCheckObject.getJSONObject("provider").optString("longitude"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                        }
                    } else if (PreviousStatus.equalsIgnoreCase("SEARCHING")) {
                        SharedHelper.putKey(context, "current_status", "");
                        if (!(scheduledDate != null && scheduledTime != null && !scheduledDate.equalsIgnoreCase("")
                                && !scheduledTime.equalsIgnoreCase(""))) {
                            Toast.makeText(context, context.getResources().getString(R.string.no_drivers_found), Toast.LENGTH_SHORT).show();
                        }
                        PreviousStatus = "";
                        flowValue = 0;
                        layoutChanges();
                        if (reasonDialog != null) {
                            if (reasonDialog.isShowing()) {
                                reasonDialog.dismiss();
                            }
                        }
                        if (cancelRideDialog != null) {
                            if (cancelRideDialog.isShowing()) {
                                cancelRideDialog.dismiss();
                            }
                        }
                        CurrentStatus = "";
                        mMap.clear();
                        mapClear();
                    } else if (PreviousStatus.equalsIgnoreCase("STARTED")) {
                        SharedHelper.putKey(context, "current_status", "");
                        Toast.makeText(context, context.getResources().getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                        PreviousStatus = "";
                        flowValue = 0;
                        layoutChanges();
                        if (reasonDialog != null) {
                            if (reasonDialog.isShowing()) {
                                reasonDialog.dismiss();
                            }
                        }
                        if (cancelRideDialog != null) {
                            if (cancelRideDialog.isShowing()) {
                                cancelRideDialog.dismiss();
                            }
                        }
                        CurrentStatus = "";
                        mMap.clear();
                        mapClear();
                    } else if (PreviousStatus.equalsIgnoreCase("ARRIVED")) {
                        SharedHelper.putKey(context, "current_status", "");
                        Toast.makeText(context, context.getResources().getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                        PreviousStatus = "";
                        flowValue = 0;
                        layoutChanges();
                        if (reasonDialog != null) {
                            if (reasonDialog.isShowing()) {
                                reasonDialog.dismiss();
                            }
                        }
                        if (cancelRideDialog != null) {
                            if (cancelRideDialog.isShowing()) {
                                cancelRideDialog.dismiss();
                            }
                        }
                        CurrentStatus = "";
                        mMap.clear();
                        mapClear();
                    } else {
                        if (flowValue == 0) {
                            getProvidersList("");
                        } else if (flowValue == 1) {
                            getProvidersList(SharedHelper.getKey(context, "service_type"));
                        }
                        CurrentStatus = "";
                    }
                }, error -> {
                    Utilities.print("Error", error.toString());
                    try {
                        if (customDialog != null && customDialog.isShowing()) {
                            customDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reqStatus = "";
                    SharedHelper.putKey(context, "req_status", "");
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };

                UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);

            } else {
                utils.displayMessage(getView(), context, context.getResources().getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTrackStatus() {

    }

    private void mapClear() {
        if (parserTask != null)
            parserTask.cancel(true);
        mMap.clear();
        source_lat = "";
        source_lng = "";
        dest_lat = "";
        dest_lng = "";
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            LatLng myLocation = new LatLng(parseDouble(current_lat), parseDouble(current_lng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void reCreateMap() {
        if (mMap != null) {
            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(parseDouble(source_lat), parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(parseDouble(dest_lat), parseDouble(dest_lng));
            }
            Utilities.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
            //String url = getDirectionsUrl(sourceLatLng, destLatLng);
            String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
            FetchUrl fetchUrl = new FetchUrl();
            fetchUrl.execute(url);
           /* DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);*/
        }
    }


    private void show(final View view) {
        mIsShowing = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(0)
                .setInterpolator(INTERPOLATOR)
                .setDuration(500);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a show should hide the view
                mIsShowing = false;
                if (!mIsHiding) {
                    hide(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    private void hide(final View view) {
        mIsHiding = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Prevent drawing the View after it is gone
                mIsHiding = false;
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a hide should show the view
                mIsHiding = false;
                if (!mIsShowing) {
                    show(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }


    public void liveNavigation(String status, String lat, String lng) {
        Log.e("Livenavigation", "ProLat" + lat + " ProLng" + lng);
        if (!lat.equalsIgnoreCase("") && !lng.equalsIgnoreCase("")) {
            List<LatLng> latLngs = new ArrayList<>();
            if (providerMarker != null) {
                latLngs.add(providerMarker.getPosition());
                latLngs.add(new LatLng(parseDouble(lat), parseDouble(lng)));
                if (calculateLocationDifference(latLngs.get(0), latLngs.get(1)) > 0)
                    animateCarOnMap(latLngs);
            } else {

                Double proLat = parseDouble(lat);
                Double proLng = parseDouble(lng);

                Float rotation = 0.0f;

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(proLat, proLng))
                        .rotation(rotation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));

                if (providerMarker != null) {
                    rotation = getBearing(providerMarker.getPosition(), markerOptions.getPosition());
                    markerOptions.rotation(rotation * (180.0f / (float) Math.PI));
                    providerMarker.remove();
                }

                providerMarker = mMap.addMarker(markerOptions);
            }
        }

    }

    private float calculateLocationDifference(LatLng oldLatLng, LatLng newLatLng) {
        float[] dist = new float[1];
        Location.distanceBetween(oldLatLng.latitude, oldLatLng.longitude, newLatLng.latitude, newLatLng.longitude, dist);
        return dist[0];
    }

    private float v;

    /**
     * Take the emissions from the Rx Relay as a pair of LatLng and starts the animation of
     * car on map by taking the 2 pair of LatLng's.
     *
     * @param latLngs List of LatLng emitted by Rx Relay with size two.
     */
    private void animateCarOnMap(final List<LatLng> latLngs) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        mMap.animateCamera(mCameraUpdate);
        if (providerMarker == null) {
            providerMarker = mMap.addMarker(new MarkerOptions().position(latLngs.get(0))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon)));
        }
        providerMarker.setPosition(latLngs.get(0));
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                v = valueAnimator.getAnimatedFraction();
                double lng = v * latLngs.get(1).longitude + (1 - v)
                        * latLngs.get(0).longitude;
                double lat = v * latLngs.get(1).latitude + (1 - v)
                        * latLngs.get(0).latitude;
                LatLng newPos = new LatLng(lat, lng);
                providerMarker.setPosition(newPos);
                providerMarker.setAnchor(0.5f, 0.5f);
                providerMarker.setRotation(getBearing(latLngs.get(0), newPos));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition
                        (new CameraPosition.Builder().target(newPos)
                                .zoom(15.5f).build()));
            }
        });
        valueAnimator.start();
    }

    /**
     * Bearing between two LatLng pair
     *
     * @param begin First LatLng Pair
     * @param end   Second LatLng Pair
     * @return The bearing or the angle at which the marker should rotate for going to {@code end} LAtLng.
     */
    private float getBearing(LatLng begin, LatLng end) {
        double lat = abs(begin.latitude - end.latitude);
        double lng = abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
//    public float getBearing(LatLng oldPosition, LatLng newPosition) {
//        double deltaLongitude = newPosition.longitude - oldPosition.longitude;
//        double deltaLatitude = newPosition.latitude - oldPosition.latitude;
//        double angle = (Math.PI * .5f) - Math.atan(deltaLatitude / deltaLongitude);
//
//        if (deltaLongitude > 0) {
//            return (float) angle;
//        } else if (deltaLongitude < 0) {
//            return (float) (angle + Math.PI);
//        } else if (deltaLatitude < 0) {
//            return (float) Math.PI;
//        }
//
//        return 0.0f;
//    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc();
        }

    }

//    private void enableLoc() {
//        buildGoogleApiClient();
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(30 * 1000);
//        locationRequest.setFastestInterval(5 * 1000);
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);
//
//        builder.setAlwaysShow(true);
//        OnLocationUpdatedListener locationListener = new OnLocationUpdatedListener() {
//            @Override
//            public void onLocationUpdated(Location location) {
//                onLocationChanged(location);
//            }
//        };
//
//        SmartLocation.with(context).location().start(locationListener);
//
//        Runnable locationRunnable = new Runnable() {
//            @Override
//            public void run() {
//                SmartLocation.with(context).location().start(locationListener);
//            }
//        };
//    }


    private void enableLoc() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);

                        } catch (NullPointerException | IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            try {
                                status.startResolutionForResult(activity, REQUEST_LOCATION);
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                        }
                        break;
                }
            }
        });
//	        }

    }

    public void submitReviewCall() {

        customDialog = new CustomDialog(context);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("rating", feedBackRating);
            object.put("comment", "" + txtCommentsRate.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.RATE_PROVIDER_API, object, response -> {
            Utilities.print("SubmitRequestResponse", response.toString());
            ((MainActivity) context).refreshView();
//            utils.hideKeypad(context, activity.getCurrentFocus());
//            if ((customDialog != null) && (customDialog.isShowing()))
//                customDialog.dismiss();
//            destination.setText("");
//            frmDest.setText("");
//            mapClear();
//            flowValue = 0;
//            layoutChanges();
//            if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
////                    LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
////                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
////                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                builder.include(sourceMarker.getPosition());
//                builder.include(destinationMarker.getPosition());
//                LatLngBounds bounds = builder.build();
//                int padding = 150; // offset from edges of the map in pixels
//
////                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 300, 300, 5);
////                    mMap.animateCamera(cu);
//
//            }
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
                            utils.displayMessage(getView(), context, errorObj.optString("message"));
                        } catch (Exception e) {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                        }

                    } else if (response.statusCode == 401) {
                        refreshAccessToken("SUBMIT_REVIEW");
                    } else if (response.statusCode == 422) {

                        json = UserApplication.trimMessage(new String(response.data));
                        if (!json.equals("") && json != null) {
                            utils.displayMessage(getView(), context, json);
                        } else {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                        }
                    } else if (response.statusCode == 503) {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));
                    } else {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                    }

                } catch (Exception e) {
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                }

            } else {
                utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
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

        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    private class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {
        JSONArray jsonArray;
        private SparseBooleanArray selectedItems;
        int selectedPosition;
        double[] finalprice = new double[100];

        public ServiceListAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        public void filterList(JSONArray array) {
            this.jsonArray = array;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity()).inflate(R.layout.service_type_list_item, parent, false);
            return new MyViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            Utilities.print("ServiceResponse", jsonArray.toString());

            String calculation_type = jsonArray.optJSONObject(position).optString("calculator");
            double c_hour = (double) strTimeTakenValue / 3600;
            double c_minutes = (double) strTimeTakenValue / 60;
            double c_base_price = parseDouble(jsonArray.optJSONObject(position).optString("fixed"));
            double c_distance_price = parseDouble(jsonArray.optJSONObject(position).optString("price"));
            double c_time_price = parseDouble(jsonArray.optJSONObject(position).optString("minute"));
            double c_base_km = parseDouble(jsonArray.optJSONObject(position).optString("distance"));

            double $apply_after_1 = parseDouble(jsonArray.optJSONObject(position).optString("apply_after_1"));
            double $apply_after_2 = parseDouble(jsonArray.optJSONObject(position).optString("apply_after_2"));
            double $apply_after_3 = parseDouble(jsonArray.optJSONObject(position).optString("apply_after_3"));
            double $after_2_price = parseDouble(jsonArray.optJSONObject(position).optString("after_2_price"));
            double $after_3_price = parseDouble(jsonArray.optJSONObject(position).optString("after_3_price"));

            double c_distance = parseDouble(strDis) / 1000;

            if (c_distance < c_base_km) c_distance = c_base_km;

            double $kilometer, $kilometer1, $kilometer3, $kilometer2;

            $kilometer = c_distance;

            $kilometer3 = $kilometer - $apply_after_3;

            if ($apply_after_3 > $apply_after_2) {
                $kilometer2 = $apply_after_3 - $apply_after_2;
            } else {
                $kilometer2 = $kilometer - $apply_after_2;
                $kilometer3 = 0;
            }

            if ($apply_after_2 > $apply_after_1) {
                $kilometer1 = $apply_after_2 - $apply_after_1;
            } else if ($apply_after_3 > $apply_after_1) {
                $kilometer1 = $apply_after_3 - $apply_after_1;
            } else {
                $kilometer1 = $kilometer - $apply_after_1;
                $kilometer2 = 0;
                $kilometer3 = 0;
            }

            if (!strDis.equals("")) {
                if (!strDis.equals(" ")) {
                    switch (calculation_type) {
                        case "MIN":
                            finalprice[position] = c_time_price * c_minutes;
                            break;
                        case "HOUR":
                            finalprice[position] = c_time_price * c_hour;
                            break;
                        case "DISTANCEMIN":
                            finalprice[position] = c_distance_price * $kilometer1 + $after_2_price * $kilometer2 + $after_3_price * $kilometer3 + c_time_price * c_minutes;
                            break;
                        case "DISTANCEHOUR":
                            finalprice[position] = c_distance_price * $kilometer1 + $after_2_price * $kilometer2 + $after_3_price * $kilometer3 + c_time_price * c_hour;
                            break;
                        default:
                            finalprice[position] = c_distance_price * $kilometer1 + $after_2_price * $kilometer2 + $after_3_price * $kilometer3;
                            break;
                    }
                }
            }
            if (source_address.contains("Kenneth Kaunda International Airport")) {
                finalprice[position] += finalprice[position] * (Constants.extra_amount_percentage / 100);
            } else if (dest_address.contains("Kenneth Kaunda International Airport")) {
                finalprice[position] += finalprice[position] * (Constants.extra_amount_percentage / 100);
            } else if (source_address.contains("University Teaching Hospital")) {
                finalprice[position] += finalprice[position] * (Constants.extra_amount_percentage / 100);
            } else if (dest_address.contains("University Teaching Hospital")) {
                finalprice[position] += finalprice[position] * (Constants.extra_amount_percentage / 100);
            }

            finalprice[position] = Math.max(finalprice[position], c_base_price);

            if (currentPostion == 0) {
                SharedHelper.putKey(context, "eta_totals", String.valueOf(finalprice[0]));
            }
            holder.serviceprice.setText(SharedHelper.getKey(context, "currency") + df.format(finalprice[position]));


            holder.serviceTitle.setText(jsonArray.optJSONObject(position).optString("name"));
            holder.serviceCapacityTv.setText(jsonArray.optJSONObject(position).optString("capacity"));
            if (position == currentPostion) {
                SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(position).optString("id"));
                Glide.with(activity).load(jsonArray.optJSONObject(position).optString("image"))
                        .apply(new RequestOptions().dontAnimate().placeholder(getResources().getDrawable(R.drawable.car_select))).into(holder.serviceImg);
                holder.service_layout.setBackground(getResources().getDrawable(R.drawable.ic_home_frag_view_1));
                btnRequestRideConfirm.setText(getResources().getString(R.string.select) + " " + jsonArray.optJSONObject(position).optString("name"));
            } else {
                Glide.with(activity).load(jsonArray.optJSONObject(position).optString("image"))
                        .apply(new RequestOptions().placeholder(getResources().getDrawable(R.drawable.car_select))).into(holder.serviceImg);
                holder.service_layout.setBackground(null);

            }

            holder.getestimate.setTag(position);
            holder.getestimate.setOnClickListener(view -> {
                currentPostion = Integer.parseInt(view.getTag().toString());
                SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(Integer.parseInt(view.getTag().toString())).optString("id"));
                SharedHelper.putKey(context, "name", "" + jsonArray.optJSONObject(currentPostion).optString("name"));
                notifyDataSetChanged();
                Utilities.print("service_type", "" + SharedHelper.getKey(context, "service_type"));
                Utilities.print("Service name", "" + SharedHelper.getKey(context, "name"));
                getProvidersList(SharedHelper.getKey(context, "service_type"));
                getApproximateFare();
            });

            holder.linearLayoutOfList.setTag(position);

            holder.linearLayoutOfList.setOnClickListener(view -> {
                if (position == currentPostion) {
                    try {
                        lnrHidePopup.setVisibility(View.VISIBLE);
                        showProviderPopup(jsonArray.getJSONObject(position));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                currentPostion = Integer.parseInt(view.getTag().toString());
                SharedHelper.putKey(context, "eta_totals", String.valueOf(finalprice[currentPostion]));
                SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(Integer.parseInt(view.getTag().toString())).optString("id"));
                SharedHelper.putKey(context, "name", "" + jsonArray.optJSONObject(currentPostion).optString("name"));
                notifyDataSetChanged();
                Utilities.print("service_type", "" + SharedHelper.getKey(context, "service_type"));
                Utilities.print("Service name", "" + SharedHelper.getKey(context, "name"));
                getProvidersList(SharedHelper.getKey(context, "service_type"));
            });

        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView serviceTitle, serviceprice, serviceCapacityTv, getestimate;
            ImageView serviceImg, serviceCapacityItemIv;
            LinearLayout linearLayoutOfList, service_layout;
            FrameLayout selector_background;

            public MyViewHolder(View itemView) {
                super(itemView);
                getestimate = itemView.findViewById(R.id.btnRequestRides);
                serviceTitle = itemView.findViewById(R.id.serviceItem);
                serviceprice = itemView.findViewById(R.id.serviceprice);
                serviceImg = itemView.findViewById(R.id.serviceImg);
                linearLayoutOfList = itemView.findViewById(R.id.LinearLayoutOfList);
                service_layout = itemView.findViewById(R.id.service_layout);
                //selector_background = (FrameLayout) itemView.findViewById(R.id.selector_background);
                serviceCapacityTv = itemView.findViewById(R.id.serviceCapacityItemTv);
                // serviceCapacityItemIv = itemView.findViewById(R.id.serviceCapacityItemIv);
                height = itemView.getHeight();
                width = itemView.getWidth();
            }
        }
    }


    private void startAnim(ArrayList<LatLng> routeList) {
        if (mMap != null && routeList.size() > 1) {
            MapAnimator.getInstance().animateRoute(context, mMap, routeList);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        handleCheckStatus.removeCallbacksAndMessages(null);
        if (mapRipple != null && mapRipple.isAnimationRunning()) {
            mapRipple.stopRippleMapAnimation();
        }
        super.onDestroy();
    }

    private void stopAnim() {
        if (mMap != null) {
            MapAnimator.getInstance().stopAnim();
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(context.getResources().getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.connect_to_wifi), (dialog, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

//    @SuppressLint("RestrictedApi")
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        if (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result);
                if (!jsonObj.optString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                    ParserTask parserTask = new ParserTask();
                    // Invokes the thread for parsing the JSON data
                    parserTask.execute(result);
                } else {
                    mMap.clear();
                    stopAnim();
                    flowValue = 0;
                    layoutChanges();
                    gotoCurrentPosition();
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.no_service));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        DataParser parser;

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            if (result != null) {
                // Traversing through all the routes
                if (result.size() > 0) {
                    for (int i = 0; i < result.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = result.get(i);

                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = parseDouble(point.get("lat"));
                            double lng = parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }

                        if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                            LatLng location = new LatLng(parseDouble(source_lat), parseDouble(source_lng));
                            //mMap.clear();
                            if (sourceMarker != null)
                                sourceMarker.remove();
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(location).snippet(source_address)
                                    .title("source").draggable(true)
                                    .icon(bitmapDescriptorFromVector(R.drawable.ic_start_point_onmap));
                            marker = mMap.addMarker(markerOptions);
                            sourceMarker = mMap.addMarker(markerOptions);
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(18).build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                        if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                            destLatLng = new LatLng(parseDouble(dest_lat), parseDouble(dest_lng));
                            if (destinationMarker != null)
                                destinationMarker.remove();
                            MarkerOptions destMarker = new MarkerOptions()
                                    .position(destLatLng).title("destination").snippet(dest_address).draggable(true)
                                    .icon(bitmapDescriptorFromVector(R.drawable.ic_end_point_onmap));
                            destinationMarker = mMap.addMarker(destMarker);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(sourceMarker.getPosition());
                            builder.include(destinationMarker.getPosition());
                            LatLngBounds bounds = builder.build();
                            int padding = 150; // offset from edges of the map in pixels

                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 300, 230, 5);
//                            mMap.moveCamera(cu);
                        }

                        if (flowValue == 1) {
                            if (sourceMarker != null && destinationMarker != null) {
                                sourceMarker.setDraggable(true);
                                destinationMarker.setDraggable(true);
                            }
                        } else {
                            if (is_track.equalsIgnoreCase("YES") &&
                                    (CurrentStatus.equalsIgnoreCase("STARTED") || CurrentStatus.equalsIgnoreCase("PICKEDUP")
                                            || CurrentStatus.equalsIgnoreCase("ARRIVED"))) {
                                if (sourceMarker != null && destinationMarker != null) {
                                    sourceMarker.setDraggable(false);
                                    destinationMarker.setDraggable(true);
                                }
                            } else {
                                if (sourceMarker != null && destinationMarker != null) {
                                    sourceMarker.setDraggable(false);
                                    destinationMarker.setDraggable(false);
                                }
                            }
                        }


                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(10);
                        lineOptions.color(getResources().getColor(R.color.colorPrimaryDark));
                        Log.d("onPostExecute", "onPostExecute lineoptions decoded");

                    }

                    strTimeTaken = parser.getEstimatedTime();
                    strTimeTakenValue = Long.parseLong(parser.getEstimatedTimeValue());
                    strDis = parser.getEstimatedkm();

                } else {
                    mMap.clear();
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.no_service));
                }

            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null && points != null) {
                mMap.addPolyline(lineOptions);
                startAnim(points);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }


    private String getUrl(double source_latitude, double source_longitude, double dest_latitude, double dest_longitude) {

        // Origin of route
        String str_origin = "origin=" + source_latitude + "," + source_longitude;

        // Destination of route
        String str_dest = "destination=" + dest_latitude + "," + dest_longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_map_api) + "&format=png&maptype=roadmap&style=visibility:on";


        return url;
    }

    private void initCity() {
        try {
            city = new City(new JSONObject(SharedHelper.getKey(context, "current_city")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initCity();
        if (!SharedHelper.getKey(context, "wallet_balance").equalsIgnoreCase("")) {
            wallet_balance = parseDouble(SharedHelper.getKey(context, "wallet_balance"));
        }

        if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
            if (chkWallet != null) {
                chkWallet.setVisibility(View.VISIBLE);
            }
        } else {
            if (chkWallet != null) {
                chkWallet.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getJSONArrayResult(String strTag, JSONArray response) {
        if (strTag.equalsIgnoreCase("Get Services")) {
            Utilities.print("GetServices", response.toString());
            if (SharedHelper.getKey(context, "service_type").equalsIgnoreCase("")) {
                SharedHelper.putKey(context, "service_type", "" + response.optJSONObject(0).optString("id"));
            }
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            if (response.length() > 0) {
                currentPostion = 0;
                ServiceListAdapter serviceListAdapter = new ServiceListAdapter(response);
                rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity));
                rcvServiceTypes.setAdapter(serviceListAdapter);
                getProvidersList(SharedHelper.getKey(context, "service_type"));
            } else {
                utils.displayMessage(getView(), context, context.getResources().getString(R.string.no_service));
            }
            if (mMap != null) {
                mMap.clear();
            }
            setValuesForSourceAndDestination();
        }
    }


    private void setCurrentAddress() {
        Utilities.getAddressUsingLatLng("source", frmSource, context, "" + current_lat, "" + current_lng);


    }

    public void setHomeWorkAddress(String strTag) {
        if (strTag.equalsIgnoreCase("home")) {
            dest_lat = "" + SharedHelper.getKey(activity, "home_lat");
            dest_lng = "" + SharedHelper.getKey(activity, "home_lng");
            dest_address = "" + SharedHelper.getKey(activity, "home");
        } else {
            dest_lat = "" + SharedHelper.getKey(activity, "work_lat");
            dest_lng = "" + SharedHelper.getKey(activity, "work_lng");
            dest_address = "" + SharedHelper.getKey(activity, "work");
        }
        frmDest.setText(dest_address);
        SharedHelper.putKey(context, "destination", dest_address);
        SharedHelper.putKey(context, "current_status", "2");
        if (source_lat != null && source_lng != null && !source_lng.equalsIgnoreCase("")
                && !source_lat.equalsIgnoreCase("")) {
            try {
                String url = getUrl(parseDouble(source_lat), parseDouble(source_lng)
                        , parseDouble(dest_lat), parseDouble(dest_lng));
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.execute(url);
                LatLng location = new LatLng(parseDouble(current_lat), parseDouble(current_lng));

                if (sourceMarker != null)
                    sourceMarker.remove();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(location).snippet(source_address)
                        .title("source")
                        .icon(bitmapDescriptorFromVector(R.drawable.user_marker));
                marker = mMap.addMarker(markerOptions);
                sourceMarker = mMap.addMarker(markerOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
            try {
                destLatLng = new LatLng(parseDouble(dest_lat), parseDouble(dest_lng));
                if (destinationMarker != null)
                    destinationMarker.remove();
                MarkerOptions destMarker = new MarkerOptions()
                        .position(destLatLng).title("destination").snippet(dest_address)
                        .icon(bitmapDescriptorFromVector(R.drawable.provider_marker));
                destinationMarker = mMap.addMarker(destMarker);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(sourceMarker.getPosition());
                builder.include(destinationMarker.getPosition());
                LatLngBounds bounds = builder.build();
                int padding = 150; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (strDis.isEmpty()) {
            if (dest_address.equalsIgnoreCase("")) {
                flowValue = 1;
                frmSource.setText(source_address);
                getServiceList();
            } else {
                flowValue = 1;
                if (cardInfoArrayList.size() > 0) {
                    getCardDetailsForPayment(cardInfoArrayList.get(0));
                }
                getServiceList();
            }
            layoutChanges();
        } else {
            if (dest_address.equalsIgnoreCase("")) {
                flowValue = 1;
                frmSource.setText(source_address);
                getServiceList();
            } else {
                flowValue = 1;
                if (cardInfoArrayList.size() > 0) {
                    getCardDetailsForPayment(cardInfoArrayList.get(0));
                }
                getServiceList();
            }
            layoutChanges();
        }

    }

    public void gotoCurrentPosition() {
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            LatLng myLocation = new LatLng(parseDouble(current_lat), parseDouble(current_lng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon)));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}