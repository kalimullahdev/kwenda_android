package com.kwendaapp.rideo.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kwendaapp.rideo.API.RetrofitClient;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Models.ConstData;
import com.kwendaapp.rideo.Models.ConstDataResponse;
import com.kwendaapp.rideo.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WelcomeScreenActivity extends AppCompatActivity {
    TextView tnc;
    Button loginButton, signUpButton;
    CustomDialog customDialog;
    String[] key = new String[1];
    final String[][] value = new String[1][1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDialog = new CustomDialog(this);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        setContentView(R.layout.activity_welcome_screen);

        loginButton = findViewById(R.id.sign_in_btn);

        signUpButton = findViewById(R.id.sign_up_btn);

        tnc = findViewById(R.id.tnc);

        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeScreenActivity.this, otpscreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);

        });
        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeScreenActivity.this, RegisterActivity.class).putExtra("signup", true).putExtra("viewpager", "yes").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);

        });

        tnc.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeScreenActivity.this, custom_webview.class);
            intent.putExtra("page", "terms_page");
            intent.putExtra("page_name", "Terms & Conditions");
            startActivity(intent);
        });

        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
        changeStatusBarColor();
        getconstdata();


    }

    public void getconstdata() {
        customDialog = new CustomDialog(this);
        customDialog.show();
        Call<ConstDataResponse> call = RetrofitClient.getInstance().getApi().getconstdata();
        call.enqueue(new Callback<ConstDataResponse>() {
            @Override
            public void onResponse(@NotNull Call<ConstDataResponse> call, @NotNull Response<ConstDataResponse> response) {
                if (response.isSuccessful()) {
                    List<ConstData> constData;
                    ConstDataResponse constDataResponse = response.body();
                    constData = constDataResponse.getData();

                    key = new String[constData.size()];
                    value[0] = new String[constData.size()];

                    for (int i = 0; i < constData.size(); i++) {
                        key[i] = constData.get(i).getKey();
                        value[0][i] = constData.get(i).getValue();
                        if (key[i].equals("page_privacy"))
                            SharedHelper.putKey(WelcomeScreenActivity.this, "privacy_page", value[0][i]);
                        if (key[i].equals("page_terms"))
                            SharedHelper.putKey(WelcomeScreenActivity.this, "terms_page", value[0][i]);
                        Log.v("Terms", SharedHelper.getKey(WelcomeScreenActivity.this, "terms_page"));

                    }
                    if (customDialog.isShowing())
                        customDialog.dismiss();
                } else {
                    if (customDialog.isShowing())
                        customDialog.dismiss();
                    // Toast.makeText(WelcomeScreenActivity.this, "Some Error Occurred ! Please Try Again Later", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NotNull Call<ConstDataResponse> call, @NotNull Throwable t) {
                if (customDialog.isShowing())
                    customDialog.dismiss();
                // Toast.makeText(WelcomeScreenActivity.this, "Some Error Occurred ! Please Try Again Later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}

