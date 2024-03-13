package com.fyp.cls.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.cls.utilities.EthereumIntegration;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.fyp.cls.R;
import com.fyp.cls.utilities.Constants;
import com.fyp.cls.utilities.Encryption;
import com.fyp.cls.utilities.PreferenceManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class LoadingActivity extends AppCompatActivity {

    private String userId;
    private String name;
    private String cnic;
    private String phone;
    private String email;
    private String image;
    private boolean isSignedIn;
    private String accBalance;
    PreferenceManager preferenceManager;
    private String expiryDate;
    private String space;
    private String accountType;
    private String registrationDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());

        preferenceManager = new PreferenceManager(this);

        settingView();
        setGreet();
        fetchUserDetailsInBackground();
    }

    private void settingView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        userId = "";
        name = "";
        image = "";
        email = "";
        cnic = "";
        phone = "";
        expiryDate = "";
        space = "";
        accountType = "";
        registrationDate = "";
        accBalance = "";
        isSignedIn = false;
    }

    private void setGreet() {
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();

        // Get the hour of the day
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String text = "";

        // Display different messages based on the time
        if (hour >= 0 && hour < 12) {
            text = "Good Morning!";
        } else if (hour >= 12 && hour < 18) {
            text = "Good Afternoon!";
        } else {
            text = "Good Evening!";
        }

        preferenceManager.putString(Constants.KEY_GREET, text);
    }

    private void fetchUserDetailsInBackground() {

        new Thread(() -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                userId = firebaseUser.getUid();
                FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                        .document(userId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {

                                    // Retrieve the user's name
                                    name = document.getString(Constants.KEY_NAME);
                                    image = document.getString(Constants.KEY_IMAGE);
                                    email = document.getString(Constants.KEY_EMAIL);
                                    phone = document.getString(Constants.KEY_PHONE);
                                    cnic =  document.getString(Constants.KEY_CNIC);

                                    isSignedIn = Boolean.TRUE.equals(document.getBoolean(Constants.KEY_IS_SIGNED_IN));
                                    expiryDate = document.getString(Constants.ACC_EXP);
                                    registrationDate = document.getString(Constants.ACC_REG);
                                    space = document.getString(Constants.ACC_SPACE_OCCUPIED);
                                    accountType = document.getString(Constants.ACC_TYPE);
                                    accBalance = document.getString(Constants.KEY_ACC_BALANCE);

                                    String decryptedEmail = "";
                                    String decryptedName = "";
                                    String decryptedImage = "";
                                    String decryptedPhone = "";
                                    String decryptedCnic = "";

                                    Encryption encryption = new Encryption();

                                    try {
                                            decryptedName = encryption.decrypt(name);
                                            decryptedEmail = encryption.decrypt(email);
                                            decryptedPhone = encryption.decrypt(phone);
                                            decryptedImage = encryption.decrypt(image);
                                            decryptedCnic = encryption.decrypt(cnic);

                                        } catch (Exception e) {
                                            e.printStackTrace();}


                                    if(name != null)    preferenceManager.putString(Constants.KEY_NAME, decryptedName);
                                    if(image != null)   preferenceManager.putString(Constants.KEY_IMAGE, decryptedImage);
                                    if(email != null)   preferenceManager.putString(Constants.KEY_EMAIL, decryptedEmail);
                                    if(phone != null)   preferenceManager.putString(Constants.KEY_PHONE, decryptedPhone);
                                    if(cnic != null)    preferenceManager.putString(Constants.KEY_CNIC, decryptedCnic);
                                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, isSignedIn);
                                    if(expiryDate != null) preferenceManager.putString(Constants.ACC_EXP, expiryDate);
                                    if(registrationDate != null) preferenceManager.putString(Constants.ACC_REG, registrationDate);
                                    if(space != null) preferenceManager.putString(Constants.ACC_SPACE_OCCUPIED, space);
                                    if(accountType != null) preferenceManager.putString(Constants.ACC_TYPE, accountType);
                                    if(accBalance != null) preferenceManager.putString(Constants.KEY_ACC_BALANCE, accBalance);

                                    //long daysRemaining = DateUtils.calculateDaysRemaining(expiryDate);

                                    //preferenceManager.putString(Constants.DAYS_REMAINING, String.valueOf(daysRemaining));

                                }
                                loadAppropriateScreen();
                            } else {
                                System.out.println("Error getting document: " + task.getException());
                            }
                        });
            } else {
                loadAppropriateScreen();
            }
        }).start();
    }

    private void loadAppropriateScreen() {
        runOnUiThread(() -> {
            if (!isSignedIn) {
                startActivity(new Intent(LoadingActivity.this, SignIn.class));
            } else {
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
            }
            finish();
        });
    }
}
