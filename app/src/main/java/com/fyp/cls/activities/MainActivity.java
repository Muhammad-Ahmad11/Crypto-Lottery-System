package com.fyp.cls.activities;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import com.fyp.cls.R;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.cls.utilities.Constants;
import com.fyp.cls.utilities.PreferenceManager;

import java.util.Base64;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public String Current_UserId = "", name, email, phone, image, greetText, accBalance;
    TextView userName;
    TextView phoneText;
    CircleImageView profileImage;
    PreferenceManager preferenceManager;
    int totalBonds;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initialize_components();

        setListeners();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void initialize_components() {
        userName = findViewById(R.id.name_text);
        profileImage = findViewById(R.id.profile_image);
        phoneText = findViewById(R.id.phone);
        TextView acc_Balance = findViewById(R.id.balance);


        TextView greet = findViewById(R.id.greeting_text);
        preferenceManager = new PreferenceManager(this);

        totalBonds = 0;

        // Set the status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.medium_green));

        // Define ActionBar object
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).show();

        // Set BackgroundDrawable
        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#348180")));

        Current_UserId = preferenceManager.getString(Constants.KEY_USERID);
        image = preferenceManager.getString(Constants.KEY_IMAGE);
        name = preferenceManager.getString(Constants.KEY_NAME);
        phone = preferenceManager.getString(Constants.KEY_PHONE);
        email = preferenceManager.getString(Constants.KEY_EMAIL);
        greetText = preferenceManager.getString(Constants.KEY_GREET);
        accBalance = preferenceManager.getString(Constants.KEY_ACC_BALANCE);

        if(name != null && !name.equals("")) {
            userName.setText(name);
        } if( email != null && !email.equals("")) {
            phoneText.setText(email);
        } if(accBalance != null && !accBalance.equals(""))  {
            acc_Balance.setText("Balance: " + accBalance);
        } if (image != null && !image.equals("")) {
            profileImage.setImageBitmap(decodeImage(image));
        } else {
            profileImage.setImageResource(R.drawable.user);
        } if (greetText != null && !greetText.equals("")) {
            greet.setText(greetText);
        }
    }

    void setListeners() {

        LinearLayout saveBonds = findViewById(R.id.saveBonds);
        saveBonds.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SaveBondsPrev.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(Constants.KEY_USERID, Current_UserId);
            startActivity(intent);
            finish();
        });

        LinearLayout buyBonds = findViewById(R.id.buyBonds);
        buyBonds.setOnClickListener(v -> {
            String phone = preferenceManager.getString(Constants.KEY_PHONE);
            String cnic = preferenceManager.getString(Constants.KEY_CNIC);

            if (!phone.equals("") || !cnic.equals("")) {
                Intent intent = new Intent(getApplicationContext(), BuyBonds.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please enter CNIC and Phone number first", Toast.LENGTH_LONG).show();
            }
        });

        LinearLayout sellBonds = findViewById(R.id.sellBonds);
        sellBonds.setOnClickListener(v -> {
            String phone = preferenceManager.getString(Constants.KEY_PHONE);
            String cnic = preferenceManager.getString(Constants.KEY_CNIC);

            if (!phone.equals("") || !cnic.equals("")) {
                Intent intent = new Intent(getApplicationContext(), SellBonds.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please enter CNIC and Phone number first", Toast.LENGTH_LONG).show();
            }
        });

        LinearLayout upgrade = findViewById(R.id.upgrade);
        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Upgrade.class);
                startActivity(intent);
            }
        });

    }

    private Bitmap decodeImage(String encodedImage) {
        if (encodedImage == null) {
            // Handle the case when the encoded image is null
            return null;
        }

        byte[] decodedBytes;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            decodedBytes = Base64.getDecoder().decode(encodedImage);
        } else {
            decodedBytes = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
        }

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}