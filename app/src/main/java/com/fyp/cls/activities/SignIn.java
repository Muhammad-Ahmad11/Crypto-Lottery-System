package com.fyp.cls.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.cls.R;
import com.fyp.cls.utilities.Constants;
import com.fyp.cls.utilities.Encryption;
import com.fyp.cls.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.text.StringEscapeUtils;

public class SignIn extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    String password;
    private String name;
    private String phone;
    private String image;
    private String cnic;
    private String accBalance;
    private boolean isSignedIn;
    private String expiryDate;
    private String space;
    private String accountType;
    private String registrationDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupActionBar();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.medium_green));

        preferenceManager = new PreferenceManager(this);
        expiryDate = "";
        space = "";
        accountType = "";
        registrationDate = "";
        accBalance = "";

        setListeners();
    }

    private void setupActionBar() {

        getSupportActionBar().setTitle("Sign In");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#348180"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }

    private void setListeners() {
        findViewById(R.id.dontaccbutton).setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), SignUp.class)));
        findViewById(R.id.buttonLogin).setOnClickListener(v-> {
            if (isInternetConnected()) {
                if(isValidSignInDetails()) {
                    signIn();
                }
            } else {
                showToast("Internet is not connected");
            }
        });
        /*TextView forgetPasswordTextView = findViewById(R.id.forgetPassword);
        forgetPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, PasswordResetActivity.class);
            startActivity(intent);
        });*/
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void signIn() {
        loading(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String email = ((TextView) findViewById(R.id.emailValue)).getText().toString();
        password = ((TextView) findViewById(R.id.PassValue)).getText().toString();

        email = StringEscapeUtils.escapeHtml4(email);
        password = StringEscapeUtils.escapeHtml4(password);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {

                            String userId = user.getUid();
                            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                                    .document(userId)
                                    .get()
                                    .addOnCompleteListener(task3 -> {
                                        if (task3.isSuccessful()) {
                                            DocumentSnapshot document = task3.getResult();
                                            if (document.exists()) {

                                                name = document.getString(Constants.KEY_NAME);
                                                image = document.getString(Constants.KEY_IMAGE);
                                                String email2 = document.getString(Constants.KEY_EMAIL);
                                                phone = document.getString(Constants.KEY_PHONE);
                                                cnic = document.getString(Constants.KEY_CNIC);

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
                                                    decryptedEmail = encryption.decrypt(email2);
                                                    decryptedPhone = encryption.decrypt(phone);
                                                    decryptedImage = encryption.decrypt(image);
                                                    decryptedCnic =  encryption.decrypt(cnic);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                if(name != null)    preferenceManager.putString(Constants.KEY_NAME, decryptedName);
                                                if(image != null)   preferenceManager.putString(Constants.KEY_IMAGE, decryptedImage);
                                                if(email2 != null)   preferenceManager.putString(Constants.KEY_EMAIL, decryptedEmail);
                                                if(phone != null)   preferenceManager.putString(Constants.KEY_PHONE, decryptedPhone);
                                                if(cnic != null)    preferenceManager.putString(Constants.KEY_CNIC, decryptedCnic);

                                                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, isSignedIn);
                                                if(expiryDate != null) preferenceManager.putString(Constants.ACC_EXP, expiryDate);
                                                if(registrationDate != null) preferenceManager.putString(Constants.ACC_REG, registrationDate);
                                                if(space != null) preferenceManager.putString(Constants.ACC_SPACE_OCCUPIED, space);
                                                if(accountType != null) preferenceManager.putString(Constants.ACC_TYPE, accountType);
                                                if(accBalance != null) preferenceManager.putString(Constants.KEY_ACC_BALANCE, accBalance);

                                            } else {
                                                System.out.println("No such document");
                                            }
                                        } else {
                                            System.out.println("Error getting document: " + task.getException());
                                        }
                                    }).addOnCompleteListener(task1 -> {

                                        String userId1 = user.getUid();
                                        String userEmail = user.getEmail();

                                        // Store user data in SharedPreferences or any other storage method you prefer
                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                        preferenceManager.putString(Constants.KEY_USERID, userId1);
                                        preferenceManager.putString(Constants.KEY_EMAIL, userEmail);

                                        // Check if the user's email is verified
                                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                        if (currentUser != null && !currentUser.isEmailVerified()) {
                                            showToast("Please verify your email address before signing in.");
                                            loading(false);
                                        } else {
                                            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(userId1)
                                                    .update(Constants.KEY_IS_SIGNED_IN, true);

                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            }

                    } else {
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });
    }

    private void loading (boolean isLoading) {
        if(isLoading) {
            findViewById(R.id.buttonLogin).setVisibility(View.INVISIBLE);
            findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
            findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails() {

        TextView emailValueTextView = findViewById(R.id.emailValue);
        TextView passValueTextView = findViewById(R.id.PassValue);

        if(emailValueTextView.getText().toString().trim().isEmpty()){
            showToast("Enter Email");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(emailValueTextView.getText().toString()).matches()){
            showToast("Enter valid Email");
            return false;
        } else if (passValueTextView.getText().toString().trim().isEmpty()){
            showToast("Enter Password");
            return false;
        } else{
            return true;
        }
    }
}
