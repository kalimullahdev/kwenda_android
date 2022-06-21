package com.kwendaapp.rideo.Utils;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kwendaapp.rideo.R;

import java.util.List;


public class MapAnimator {

    private static MapAnimator mapAnimator;

    private Polyline backgroundPolyline;

    private Polyline foregroundPolyline;

    private AnimatorSet firstRunAnimSet;

    static final int GREY = Color.parseColor("#FFA7A6A6");

    private boolean stopAnim = false;

    private MapAnimator() {

    }

    public static MapAnimator getInstance() {
        if (mapAnimator == null) mapAnimator = new MapAnimator();
        return mapAnimator;
    }


    public void stopAnim() {
        if (firstRunAnimSet != null) {
            firstRunAnimSet.removeAllListeners();
            firstRunAnimSet.end();
            firstRunAnimSet.cancel();
            stopAnim = true;
            firstRunAnimSet = new AnimatorSet();
        }
    }

    public void animateRoute(final Context context, final GoogleMap googleMap, final List<LatLng> bangaloreRoute) {
        stopAnim = false;
        if (firstRunAnimSet == null) {
            firstRunAnimSet = new AnimatorSet();
        } else {
            firstRunAnimSet.removeAllListeners();
            firstRunAnimSet.end();
            firstRunAnimSet.cancel();

            firstRunAnimSet = new AnimatorSet();
        }

        if (foregroundPolyline != null) foregroundPolyline.remove();
        if (backgroundPolyline != null) backgroundPolyline.remove();


        @SuppressLint("ResourceType") PolylineOptions optionsBackground = new PolylineOptions().add(bangaloreRoute.get(0)).color(Color.parseColor(context.getResources().getString(R.color.colorAccent))).width(10);
        backgroundPolyline = googleMap.addPolyline(optionsBackground);

        @SuppressLint("ResourceType") PolylineOptions optionsForeground = new PolylineOptions().add(bangaloreRoute.get(0)).color(Color.parseColor(context.getResources().getString(R.color.colorAccent))).width(10);
        foregroundPolyline = googleMap.addPolyline(optionsForeground);

        final ValueAnimator percentageCompletion = ValueAnimator.ofInt(0, 100);
        percentageCompletion.setDuration(2000);
        percentageCompletion.setInterpolator(new DecelerateInterpolator());
        percentageCompletion.addUpdateListener(animation -> {
            List<LatLng> foregroundPoints = backgroundPolyline.getPoints();

            int percentageValue = (int) animation.getAnimatedValue();
            int pointcount = foregroundPoints.size();
            int countTobeRemoved = (int) (pointcount * (percentageValue / 100.0f));
            List<LatLng> subListTobeRemoved = foregroundPoints.subList(0, countTobeRemoved);
            subListTobeRemoved.clear();

            foregroundPolyline.setPoints(foregroundPoints);
        });
        percentageCompletion.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                foregroundPolyline.setColor(GREY);
                foregroundPolyline.setPoints(backgroundPolyline.getPoints());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), GREY, Color.BLACK);
        colorAnimation.setInterpolator(new AccelerateInterpolator());
        colorAnimation.setDuration(1200); // milliseconds

        colorAnimation.addUpdateListener(animator -> foregroundPolyline.setColor((int) animator.getAnimatedValue()));

        ObjectAnimator foregroundRouteAnimator = ObjectAnimator.ofObject(this, "routeIncreaseForward", new RouteEvaluator(), bangaloreRoute.toArray());
        foregroundRouteAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        foregroundRouteAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    if (foregroundPolyline != null && backgroundPolyline != null) {
                        backgroundPolyline.setPoints(foregroundPolyline.getPoints());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        foregroundRouteAnimator.setDuration(1600);
//        foregroundRouteAnimator.start();

        firstRunAnimSet.playSequentially(foregroundRouteAnimator, percentageCompletion);

        firstRunAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    if (!stopAnim) {
                        firstRunAnimSet = null;
                        animateRoute(context, googleMap, bangaloreRoute);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        firstRunAnimSet.start();
    }



}

