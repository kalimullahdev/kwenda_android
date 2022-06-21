package com.kwendaapp.rideo.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.R;
import de.hdodenhof.circleimageview.CircleImageView;
public class Profile extends AppCompatActivity {


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        CircleImageView profile_img;
        ImageView edit_profile, back;
        TextView profile_username, profile_email, profile_phone, profile_home, profile_work;

        edit_profile = findViewById(R.id.edit_profile);
        back = findViewById(R.id.backArrow);
        profile_img = findViewById(R.id.profile_userimg);
        profile_username = findViewById(R.id.profile_username);
        profile_email = findViewById(R.id.profile_email);
        profile_phone = findViewById(R.id.profile_phone);
        profile_home = findViewById(R.id.profile_home);
        profile_work = findViewById(R.id.profile_work);

        Glide.with(this)
                .load(SharedHelper.getKey(this, "picture"))
                .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_dummy_user))
                .into(profile_img);

        profile_username.setText(SharedHelper.getKey(this, "first_name") + " " + SharedHelper.getKey(this, "last_name"));
        profile_email.setText(SharedHelper.getKey(this, "email"));
        profile_phone.setText(SharedHelper.getKey(this, "mobile"));
        profile_home.setText(SharedHelper.getKey(this, "home"));
        profile_work.setText(SharedHelper.getKey(this, "work"));

        back.setOnClickListener(v -> onBackPressed());

        edit_profile.setOnClickListener(v -> startActivity(new Intent(Profile.this, EditProfile.class)));

    }
}
