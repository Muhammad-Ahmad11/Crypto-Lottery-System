package com.fyp.cls.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.cls.R;
import com.fyp.cls.utilities.Constants;
import com.fyp.cls.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity {

    private EditText amountText;
    private Button payButton;
    private FirebaseFirestore firestore;

    private long amountDouble;
    private PreferenceManager preferenceManager;

    private PaymentSheet paymentSheet;

    private String customerId;
    private String ephemeralKey;
    private String clientSecret;

    private static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initializeUI();
        setupStripeCustomer();
    }

    private void initializeUI() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.medium_green));

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#348180")));
        actionBar.setDisplayHomeAsUpEnabled(true);

        amountText = findViewById(R.id.amount_id);
        payButton = findViewById(R.id.payButton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Transaction in progress");
        progressDialog.setCancelable(false);

        preferenceManager = new PreferenceManager(this);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        payButton.setOnClickListener(v -> {
            if (amountText.getText().toString().trim().isEmpty()) {
                Toast.makeText(PaymentActivity.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                return;
            }
            amountDouble = Long.parseLong(amountText.getText().toString().trim());
            progressDialog.show();
            getClientSecret();
        });
    }

    private void setupStripeCustomer() {
//        Map<String, String> params = new HashMap<>();
//        params.put("", "");

        makeStripeRequest("https://api.stripe.com/v1/customers", null,
                response -> {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(response);
                        customerId = object.getString("id");
                        getEphemeralKey(customerId);
                    } catch (JSONException e) {
                        Log.e("PaymentActivity", "JSON parsing error", e);
                    }
                },
                error -> {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    Log.e("PaymentActivity", "Volley error", error);
                }
        );
    }

    private void getEphemeralKey(String customerId) {
        Map<String, String> params = new HashMap<>();
        params.put("customer", customerId);

        makeStripeRequest("https://api.stripe.com/v1/ephemeral_keys", params,

                response -> {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(response);
                        ephemeralKey = object.getString("id");
                    } catch (JSONException e) {
                        Log.e("PaymentActivity", "JSON parsing error", e);
                    }
                },
                error -> {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    Log.e("PaymentActivity", "Volley error", error);
                }
        );
    }

    private void getClientSecret() {
        Map<String, String> params = new HashMap<>();
        params.put("customer", customerId);
        params.put("amount", amountDouble + "00");
        params.put("currency", "usd");

        makeStripeRequest("https://api.stripe.com/v1/payment_intents", params,
                response -> {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(response);
                        clientSecret = object.getString("client_secret");
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        paymentFlow();
                    } catch (JSONException e) {
                        Log.e("PaymentActivity", "JSON parsing error", e);
                    }
                },
                error -> {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    Log.e("PaymentActivity", "Volley error", error);
                }
        );
    }

    private void makeStripeRequest(String url, @Nullable Map<String, String> params,
                                   Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url,
                responseListener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + Constants.SK_STRIPE_TEST_KEY);
                if (url.contains("ephemeral_keys")) {
                    headers.put("Stripe-Version", "2023-10-16"); // Adjust Stripe-Version as needed
                }
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (params!=null){
                    return params;
                }
                else {
                    return super.getParams();
                }
            }
        };
        Log.d("StripeRequest", "URL: " + url);
        Log.d("StripeRequest", "Method: " +Request.Method.POST);
//        Log.d("StripeRequest", "Headers: " + hea.toString());
//        Log.d("StripeRequest", "Body: " + new JSONObject(params).toString());
        requestQueue.add(jsonObjectRequest);
    }

    private void paymentFlow() {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration(
                getString(R.string.app_name),
                new PaymentSheet.CustomerConfiguration(
                        customerId,
                        ephemeralKey
                )
        );
        paymentSheet.presentWithPaymentIntent(clientSecret, configuration);
    }

    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            long amount = Long.parseLong(preferenceManager.getString(Constants.KEY_ACC_BALANCE)) + amountDouble;
            preferenceManager.putString(Constants.KEY_ACC_BALANCE, String.valueOf(amount));
            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USERID))
                    .update(Constants.KEY_ACC_BALANCE, String.valueOf(amount))
                    .addOnSuccessListener(aVoid -> displayAlert("Payment Successful", "Congratulations, your amount " + amountDouble + " has been added to your balance."))
                    .addOnFailureListener(e -> Toast.makeText(PaymentActivity.this, "Failed to update balance", Toast.LENGTH_SHORT).show());
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
        }
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
}
