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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.cls.R;
import com.fyp.cls.utilities.Constants;
import com.fyp.cls.utilities.Encryption;
import com.fyp.cls.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import org.apache.commons.text.StringEscapeUtils;

import java.util.HashMap;
import java.util.Objects;

public class SignUp extends AppCompatActivity {

    public PreferenceManager preferenceManager;
    String userPassword = "";
    String userEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Objects.requireNonNull(getSupportActionBar()).hide();
        setupActionBar();

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.medium_green));
        preferenceManager = new PreferenceManager(this);
        setListeners();
    }

    private void setupActionBar() {

        getSupportActionBar().setTitle("Sign Up");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#348180"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }

    private void setListeners() {
        Button alreadyLogin = findViewById(R.id.alreadylogin);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        alreadyLogin.setOnClickListener(v-> onBackPressed());
        buttonRegister.setOnClickListener(v->{
            if (isInternetConnected()) {
                if (isValidSignUpDetails()) {
                    signup();
                }
            } else {
                showToast("Internet is not connected");
            }
        });
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

       /*private String getUserIpAddress() {

        String ipv4 = request.getRemoteAddr();

        String ipv6 = request.getHeader("X-FORWARDED-FOR");

        return ipv4; // Replace this with the actual user's IP address
    }

    private boolean isDeviceAllowed(String ipAddress) {
        // Implement the logic to check if the device's IP address exists in Firestore
        // You can use the Firebase Admin SDK to query Firestore for the device's IP address
        // For example:
        // DatabaseReference deviceRef = FirebaseDatabase.getInstance().getReference("devices");
        // DataSnapshot snapshot = deviceRef.child(ipAddress).get();
        // return snapshot.exists();

        // Replace the above lines with your actual Firestore querying logic
        return true; // Replace with your implementation
    }*/

    private void signup() {
        loading(true);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        TextView email = findViewById(R.id.emailValue);
        TextView pass = findViewById(R.id.passValue);
        userEmail = StringEscapeUtils.escapeHtml4(email.getText().toString());
        userPassword = StringEscapeUtils.escapeHtml4(pass.getText().toString());

        mAuth.createUserWithEmailAndPassword(Objects.requireNonNull(userEmail), userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {

                            String userId = firebaseUser.getUid();
                            String encryptedEmail = "";

                            Encryption encryption = new Encryption();

                            /*try {
                                encryption = new Encryption(getApplicationContext());
                            } catch (UnrecoverableEntryException e) {
                                throw new RuntimeException(e);
                            } catch (CertificateException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            } catch (KeyStoreException e) {
                                throw new RuntimeException(e);
                            }*/

                            encryptedEmail = encryption.encrypt(userEmail);

                            user.put(Constants.KEY_IS_SIGNED_IN, false);
                            user.put(Constants.KEY_NAME, "");
                            user.put(Constants.KEY_PHONE, "");
                            user.put(Constants.KEY_IMAGE, "");
                            user.put(Constants.KEY_CNIC, "");
                            user.put(Constants.KEY_EMAIL, encryptedEmail);
                            user.put(Constants.ACC_TYPE, "BASIC");
                            user.put(Constants.ACC_EXP, "");
                            user.put(Constants.ACC_REG, "");
                            user.put(Constants.ACC_SPACE_OCCUPIED, "0");
                            user.put(Constants.KEY_ACC_BALANCE, "0");

                            user.put(Constants.KEY_BOND100, "");
                            user.put(Constants.KEY_BOND200, "");
                            user.put(Constants.KEY_BOND750, "");
                            user.put(Constants.KEY_BOND1500, "");
                            user.put(Constants.KEY_BOND25000, "");
                            user.put(Constants.KEY_BOND40000, "");

                            user.put(Constants.KEY_TOTAL100, "0");
                            user.put(Constants.KEY_TOTAL200, "0");
                            user.put(Constants.KEY_TOTAL750, "0");
                            user.put(Constants.KEY_TOTAL1500, "0");
                            user.put(Constants.KEY_TOTAL25000, "0");
                            user.put(Constants.KEY_TOTAL40000, "0");

                            database.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(userId)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        loading(false);
                                        //preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                        preferenceManager.putString(Constants.KEY_USERID, userId);
                                        preferenceManager.putString(Constants.KEY_EMAIL, userEmail);
                                        preferenceManager.putString(Constants.KEY_CNIC, "");
                                        preferenceManager.putString(Constants.KEY_NAME, "");
                                        preferenceManager.putString(Constants.KEY_IMAGE, "");
                                        preferenceManager.putString(Constants.KEY_PHONE,  "");
                                        sendEmailVerification();

                                        Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(exception -> {
                                        loading(false);
                                        showToast(exception.getMessage());
                                    });
                        }
                    } else {
                        loading(false);
                        showToast(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loading(false);
                                showToast("Confirmation email sent. Please verify your email before signing in.");
                            } else {
                                loading(false);
                                showToast("Failed to send confirmation email");
                            }
                        }
                    });
        }
    }

    private Boolean isValidSignUpDetails() {
        TextView email = findViewById(R.id.emailValue);
        TextView pass = findViewById(R.id.passValue);
        if(email.getText().toString().trim().isEmpty()){
            showToast("Enter Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            showToast("Enter Valid Image");
            return false;
        } else if (pass.getText().toString().trim().isEmpty()){
            showToast("Enter Password");
            return false;
        } else {
            return true;
        }
    }

    private void loading (boolean isLoading) {
        Button btn = findViewById(R.id.buttonRegister);
        ProgressBar progressBar = findViewById(R.id.progressbar);
        if(isLoading) {
            btn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.VISIBLE);
        }
    }
}
