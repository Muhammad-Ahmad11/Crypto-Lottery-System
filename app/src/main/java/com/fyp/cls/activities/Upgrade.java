package com.fyp.cls.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fyp.cls.R;
import com.fyp.cls.utilities.Constants;
import com.fyp.cls.utilities.PreferenceManager;

import java.util.Objects;

public class Upgrade extends AppCompatActivity {
    private PreferenceManager preferenceManager;

    String userAccount = "";
    TextView pkg3Text;
    TextView pkg2Text;
    TextView pkg1Text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        preferenceManager = new PreferenceManager(this);

        initialize_components();
        setRemoteConfig();
        setListeners();
    }

    private void setRemoteConfig() {

         pkg1Text = findViewById(R.id.pkg1Value);
         pkg2Text = findViewById(R.id.pkg2Value);
         pkg3Text = findViewById(R.id.pkg3Value);
        TextView pkg1year = findViewById(R.id.pkg1year);
        TextView pkg2year = findViewById(R.id.pkg2year);
        TextView pkg3year = findViewById(R.id.pkg3year);

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1) // Set your desired fetch interval in seconds
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults); // Point to your default values XML

        // Fetch remote config values
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        boolean updated = task.getResult();
                        // Now you can use the updated Remote Config values in your app
                        String pkg1 = mFirebaseRemoteConfig.getString("pkg1");
                        String pkg2 = mFirebaseRemoteConfig.getString("pkg2");
                        String pkg3 = mFirebaseRemoteConfig.getString("pkg3");
                        pkg1year.setText(mFirebaseRemoteConfig.getString("pkg1year"));
                        pkg2year.setText(mFirebaseRemoteConfig.getString("pkg2year"));
                        pkg3year.setText(mFirebaseRemoteConfig.getString("pkg3year"));

                        pkg1Text.setText(pkg1);
                        pkg2Text.setText(pkg2);
                        pkg3Text.setText(pkg3);

                    } else {
                        // Failed to fetch remote config values
                        //Toast.makeText(Upgrade.this, "Failed to fetch Remote config values", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private  void initialize_components() {
        // Set the status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.medium_green));

        // Define ActionBar object
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).show();
        getSupportActionBar().setTitle("Upgrade");

        // Set BackgroundDrawable
        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#348180")));

        PreferenceManager preferenceManager = new PreferenceManager(this);
        userAccount = preferenceManager.getString(Constants.ACC_TYPE);
    }

    private  void setListeners() {
        Button basic = findViewById(R.id.basicID);
        Button standard = findViewById(R.id.standardID);
        Button premium = findViewById(R.id.premiumID);
        Button advanced = findViewById(R.id.advancedID);

        if(userAccount != null) {
            if (userAccount.trim().equals("BASIC")) {
                basic.setBackgroundResource(R.drawable.button_border);
                basic.setTextColor(ContextCompat.getColor(this, R.color.green));
                basic.setText("SUBSCRIBED");
            } if (userAccount.trim().equals("STANDARD")) {
                standard.setBackgroundResource(R.drawable.button_border);
                standard.setTextColor(ContextCompat.getColor(this, R.color.green));
                standard.setText("SUBSCRIBED");
            } if (userAccount.trim().equals("PREMIUM")) {
                premium.setBackgroundResource(R.drawable.button_border);
                premium.setTextColor(ContextCompat.getColor(this, R.color.green));
                premium.setText("SUBSCRIBED");
            } if (userAccount.trim().equals("ADVANCED")) {
                advanced.setBackgroundResource(R.drawable.button_border);
                advanced.setTextColor(ContextCompat.getColor(this, R.color.green));
                advanced.setText("SUBSCRIBED");
            }
        }

        basic.setOnClickListener(view -> {
            if(userAccount != null) {
                if(userAccount.trim().equals("BASIC")) {
                    Intent intent = new Intent(getApplicationContext(), PackageDetails.class);
                    startActivity(intent);
                } else {
                    //check for sufficient user balance then upgrade

                }
            }

        });

        standard.setOnClickListener(view -> {
            if(userAccount != null) {
                if(userAccount.trim().equals("STANDARD")) {
                    Intent intent = new Intent(getApplicationContext(), PackageDetails.class);
                    startActivity(intent);
                } else {
                  subscribeData( pkg1Text.getText().toString().trim());
                }
            }
        });

        premium.setOnClickListener(view -> {
            if(userAccount != null) {
                if(userAccount.trim().equals("PREMIUM")) {
                    Intent intent = new Intent(getApplicationContext(), PackageDetails.class);
                    startActivity(intent);
                } else {
                    subscribeData( pkg2Text.getText().toString().trim());
                }
            }
        });

        advanced.setOnClickListener(view -> {
            if(userAccount != null) {
                if(userAccount.trim().equals("ADVANCED")) {
                    Intent intent = new Intent(getApplicationContext(), PackageDetails.class);
                    startActivity(intent);
                } else {
                    subscribeData( pkg3Text.getText().toString().trim());
                }
            }
        });
    }

    private void subscribeData(String price) {
        long balance = Long.parseLong(preferenceManager.getString(Constants.KEY_ACC_BALANCE));
        String amountString =price;
        // Remove all non-digit characters from the string
        amountString = amountString.replaceAll("[^\\d]", "");
        // Parse the numeric string to a long
        long amount = Long.parseLong(amountString);
        if (balance<amount){
            Toast.makeText(Upgrade.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
        }else {
            long remaningBalance=balance-amount;
            preferenceManager.putString(Constants.KEY_ACC_BALANCE, String.valueOf(remaningBalance));
            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USERID))
                    .update(Constants.KEY_ACC_BALANCE, String.valueOf(remaningBalance));
            displayAlert("Subscription Subscribed", "Congratulations, your subscription has been completed for amount " + amount + " and amount deduct from your balance");

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Handle the back button click here, similar to onBackPressed()
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayAlert(@NonNull String title, @Nullable String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", (DialogInterface dialog, int which) ->{
                    dialog.dismiss();
                    finish();
                })
                .show();
    }
    @Override
    public void onBackPressed() {
        // Perform the same behavior as the hardware back button
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
