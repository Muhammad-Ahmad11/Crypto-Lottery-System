package com.fyp.cls.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fyp.cls.R;
import com.fyp.cls.utilities.Constants;
import com.fyp.cls.utilities.PreferenceManager;

import java.util.Objects;

public class PackageDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.package_details);


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

        getSupportActionBar().setTitle("Package Details");

        PreferenceManager preferenceManager;
        preferenceManager = new PreferenceManager(this);
        TextView userEmail = findViewById(R.id.userEmailEdit);
        TextView userAccount = findViewById(R.id.userAccountTypeEdit);
        TextView regDate = findViewById(R.id.userRegEdit);
        TextView expDate = findViewById(R.id.userExpiryDate);
        TextView accSpace = findViewById(R.id.userAccSpace);

        String email = preferenceManager.getString(Constants.KEY_EMAIL);
        String accountType = preferenceManager.getString(Constants.ACC_TYPE);
        String regisDate = preferenceManager.getString(Constants.ACC_REG);
        String expirDate = preferenceManager.getString(Constants.ACC_EXP);
        String accountSpace = preferenceManager.getString(Constants.ACC_SPACE_OCCUPIED);

        if(email != null) {
            userEmail.setText(email);
        }
        if(accountType != null) {
            userAccount.setText(accountType);
            if(accountType.equals("BASIC")) {
                regDate.setText("No Registration Date");
                expDate.setText("Never Expiring");
                accSpace.setText(accountSpace + "/1000");
            } else {
                if(regisDate != null) {
                    regDate.setText(regisDate);
                }
                if(expirDate != null) {
                    expDate.setText(expirDate);
                }
                if(accSpace != null) {
                    accSpace.setText("Unlimited");
                }
            }
        }
    }
}
