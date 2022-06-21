package com.kwendaapp.rideo.Utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.snackbar.Snackbar;
import com.kwendaapp.rideo.Activities.WelcomeScreenActivity;
import com.kwendaapp.rideo.BuildConfig;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.Retrofit.ApiInterface;
import com.kwendaapp.rideo.Retrofit.RetrofitClient;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Utilities {

    private static ApiInterface mApiInterface;


    public static String formatted_address = "";

    public void displayMessage(View view, Context context, String toastString) {
        try {
            Snackbar.make(view, toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    public static void print(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }

    public static boolean ismorning() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
        String currentTim = sdf.format(new Date());
        int currentTime = Integer.parseInt(currentTim);
        return currentTime > 5 && currentTime < 12;

    }

    public static boolean isafternoon() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
        String currentTim = sdf.format(new Date());
        int currentTime = Integer.parseInt(currentTim);
        return (currentTime == 12 || currentTime > 12) && currentTime < 18;

    }

    public static boolean isnight() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
        String currentTim = sdf.format(new Date());
        int currentTime = Integer.parseInt(currentTim);
        return (currentTime == 18 || currentTime > 18) && currentTime < 22;

    }

    public void GoToBeginActivity(Activity activity) {
        Intent mainIntent = new Intent(activity, WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(mainIntent);
        activity.finish();
    }

    public void rotateMarker(final Marker marker, final float toRotation) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }

            }
        });
    }

    public boolean checktimings(String time) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            String currentTime = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(currentTime);

            return date1.after(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    public void hideKeypad(Context context, View view) {
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showAlert(Context context, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setMessage(message)
                    .setTitle(context.getResources().getString(R.string.app_name))
                    .setCancelable(true)
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isValidPassword(final String password) {
//        Pattern pattern;
//        Matcher matcher;
//
//        String PASSWORD_PATTERN = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
//
//        pattern = Pattern.compile(PASSWORD_PATTERN);
//        matcher = pattern.matcher(password);
//
//        boolean status = matcher.matches();

        return true;
    }

    public static boolean isAfterToday(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        Calendar myDate = Calendar.getInstance();

        myDate.set(year, month, day);

        return !myDate.before(today);
    }

    public void animateMarker(GoogleMap map, final Location destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            float bearing = (float) calculateBearing(startPosition.latitude, startPosition.longitude, destination.getLatitude(), destination.getLongitude());

//            CameraPosition cameraPosition =
//                    new CameraPosition.Builder()
//                            .target(new LatLng(destination.getLatitude(),destination.getLongitude()))
//                            .bearing(bearing)
//                            .tilt(90)
//                            .zoom(16.0f)
//                            .build();

//            map.animateCamera(
//                    CameraUpdateFactory.newCameraPosition(cameraPosition),
//                    1000,
//                    null
//            );

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(animation -> {
                try {
                    float v = animation.getAnimatedFraction();
                    LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                    marker.setPosition(newPosition);
//                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
                } catch (Exception ex) {
                    // I don't care atm..
                }
            });

            valueAnimator.start();
        }
    }


    private static double calculateBearing(double lat1, double lng1, double lat2, double lng2) {
        double dLon = (lng2 - lng1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        return 0;
    }

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }


//    private static float computeRotation(float fraction, float start, float end) {
//        float normalizeEnd = end - start; // rotate start to 0
//        float normalizedEndAbs = (normalizeEnd + 360) % 360;
//
//        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
//        float rotation;
//        if (direction > 0) {
//            rotation = normalizedEndAbs;
//        } else {
//            rotation = normalizedEndAbs - 360;
//        }
//
//        float result = fraction * rotation + start;
//        return (result + 360) % 360;
//    }
//

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Log.e("Utilis", "My Current: " + addresses.toString());
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                if (returnedAddress.getMaxAddressLineIndex() > 0) {

                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                    }
                } else {
                    strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Utilis My Current", "" + strReturnedAddress.toString());
            } else {
                Log.w("Utilis My Current", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Utilis My Current", "Canont get Address!");
        }
        return strAdd;
    }

    public static String getAddressLocal(
            final String strType, final TextView txtView, final Context context, String latitude, String longitude) {
        formatted_address = "Fetching Address...";
        if (latitude.isEmpty() || longitude.isEmpty()) {
            formatted_address = "";
            SharedHelper.putKey(context, "source", "");
            SharedHelper.putKey(context, "destination", "");
            txtView.setText("");
        } else {

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> list = geocoder.getFromLocation(
                        Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                if (list != null && list.size() > 0) {
                    Address address = list.get(0);
                    // sending back first address line and locality
                    formatted_address = address.getAddressLine(0) + ", " + address.getLocality();
                    txtView.setText("" + formatted_address);
                    if (SharedHelper.getKey(context, "track_status").equalsIgnoreCase("YES") &&
                            (SharedHelper.getKey(context, "req_status").equalsIgnoreCase("STARTED") || SharedHelper.getKey(context, "req_status").equalsIgnoreCase("PICKEDUP")
                                    || SharedHelper.getKey(context, "req_status").equalsIgnoreCase("ARRIVED"))) {
                        SharedHelper.putKey(context, "extend_address", "" + formatted_address);
                    } else {
                        if (strType.equalsIgnoreCase("source")) {
                            SharedHelper.putKey(context, "source", "" + formatted_address);
                        } else if (strType.equalsIgnoreCase("setlocationtext")) {
                            SharedHelper.putKey(context, "dsdsd", "" + formatted_address);
                        } else {
                            SharedHelper.putKey(context, "destination", "" + formatted_address);
                        }
                    }
                } else
                    return getAddressOnline(strType, txtView, context, latitude, longitude);
            } catch (IOException e) {
                Log.e("TAG", "Impossible to connect to Geocoder", e);
                return getAddressOnline(strType, txtView, context, latitude, longitude);
            }
        }
        return formatted_address;
    }

    public static String getAddressOnline(
            final String strType, final TextView txtView, final Context context, String latitude, String longitude) {
        formatted_address = "Fetching Address...";
        mApiInterface = RetrofitClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = mApiInterface.getResponse(latitude + "," + longitude,
                context.getResources().getString(R.string.google_map_api));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                // Log.e("sUCESS","SUCESS"+response.body());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        // Log.e("sUCESS","bodyString"+bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            JSONArray jsonArray = jsonObj.optJSONArray("results");
                            if (jsonArray.length() > 0) {
                                formatted_address = jsonArray.optJSONObject(0).optString("formatted_address");
                                // Log.v("Formatted Address", ""+formatted_address);
                                txtView.setText("" + formatted_address);
                                if (SharedHelper.getKey(context, "track_status").equalsIgnoreCase("YES") &&
                                        (SharedHelper.getKey(context, "req_status").equalsIgnoreCase("STARTED") || SharedHelper.getKey(context, "req_status").equalsIgnoreCase("PICKEDUP")
                                                || SharedHelper.getKey(context, "req_status").equalsIgnoreCase("ARRIVED"))) {
                                    SharedHelper.putKey(context, "extend_address", "" + formatted_address);
                                } else {
                                    if (strType.equalsIgnoreCase("source")) {
                                        SharedHelper.putKey(context, "source", "" + formatted_address);
                                    } else if (strType.equalsIgnoreCase("setlocationtext")) {
                                        SharedHelper.putKey(context, "dsdsd", "" + formatted_address);
                                    } else {
                                        SharedHelper.putKey(context, "destination", "" + formatted_address);
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Service not available!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            formatted_address = "";
                            SharedHelper.putKey(context, "source", "");
                            SharedHelper.putKey(context, "destination", "");
                            txtView.setText("");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        formatted_address = "";
                        SharedHelper.putKey(context, "source", "");
                        SharedHelper.putKey(context, "destination", "");
                        txtView.setText("");
                    }
                } else {
                    formatted_address = "";
                    SharedHelper.putKey(context, "source", "");
                    SharedHelper.putKey(context, "destination", "");
                    txtView.setText("");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "onFailure" + call.request().url());
                formatted_address = "";
                SharedHelper.putKey(context, "source", "");
                SharedHelper.putKey(context, "destination", "");
                txtView.setText("");
            }
        });
        return "" + formatted_address;

    }

    public static String getAddressUsingLatLng(final String strType, final TextView txtView, final Context context, String latitude, String longitude) {
        return getAddressLocal(strType, txtView, context, latitude, longitude);
    }

    public static String getAddressUsingLatLng(final Context context, String latitude, String longitude) {
        mApiInterface = RetrofitClient.getClient().create(ApiInterface.class);
        formatted_address = "Fetching Address...";
        Call<ResponseBody> call = mApiInterface.getResponse(latitude + "," + longitude,
                context.getResources().getString(R.string.google_map_api));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Log.e("sUCESS","SUCESS"+response.body());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        // Log.e("sUCESS","bodyString"+bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            JSONArray jsonArray = jsonObj.optJSONArray("results");
                            if (jsonArray.length() > 0) {
                                formatted_address = jsonArray.optJSONObject(0).optString("formatted_address");
                                // Log.v("Formatted Address", ""+formatted_address);
                                if (SharedHelper.getKey(context, "track_status").equalsIgnoreCase("YES") &&
                                        (SharedHelper.getKey(context, "req_status").equalsIgnoreCase("STARTED") || SharedHelper.getKey(context, "req_status").equalsIgnoreCase("PICKEDUP")
                                                || SharedHelper.getKey(context, "req_status").equalsIgnoreCase("ARRIVED"))) {
                                    SharedHelper.putKey(context, "extend_address", "" + formatted_address);
                                }
                            } else {
                                Toast.makeText(context, "Service not available!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            formatted_address = "";
                            SharedHelper.putKey(context, "source", "");
                            SharedHelper.putKey(context, "destination", "");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        formatted_address = "";
                        SharedHelper.putKey(context, "source", "");
                        SharedHelper.putKey(context, "destination", "");
                    }
                } else {
                    formatted_address = "";
                    SharedHelper.putKey(context, "source", "");
                    SharedHelper.putKey(context, "destination", "");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "onFailure" + call.request().url());
                formatted_address = "";
                SharedHelper.putKey(context, "source", "");
                SharedHelper.putKey(context, "destination", "");
            }
        });
        return "" + formatted_address;
    }

    public static String getAddressUsingPlaceId(Context context, String plce_id) {
        String strURL = "https://maps.googleapis.com/maps/api/geocode/json?place_id=" + plce_id + "&key=" + context.getResources().getString(R.string.google_map_api);
        return "";
    }


    public static String formatHoursAndMinutes(int totalMinutes) {
        String minutes = Integer.toString(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        if (totalMinutes / 60 >= 1) {
            return (totalMinutes / 60) + " hr " + minutes + " mins";
        }
        return minutes + " mins";
    }

    public static boolean isServerReachable(Context context, String strUrl) {
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL(strUrl);
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                urlConn.connect();
                return urlConn.getResponseCode() == 200;
            } catch (IOException e1) {
                return false;
            }
        }
        return false;
    }
}