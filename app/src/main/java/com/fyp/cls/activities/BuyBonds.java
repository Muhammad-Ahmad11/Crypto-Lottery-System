package com.fyp.cls.activities;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.cls.R;
import com.fyp.cls.adapters.BondsListAdapter;
import com.fyp.cls.utilities.Constants;
import com.fyp.cls.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuyBonds extends AppCompatActivity {

    RecyclerView recyclerView;
    PreferenceManager preferenceManager;
    String Current_UserId;
    LoadDataTask loadMarket;
    FirebaseFirestore firebase;
    int category = 0;
    String buyingBondsList = "";
    Spinner categorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_bonds);

        initialize_components();
    }

    private void initialize_components() {
        preferenceManager = new PreferenceManager(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
            actionBar.setTitle("Buy Bonds");
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#348180")));
        }

        firebase = FirebaseFirestore.getInstance();

        Current_UserId = preferenceManager.getString(Constants.KEY_USERID);

        recyclerView = findViewById(R.id.recyclerView);

        categorySpinner = findViewById(R.id.filter1Spinner);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleItemSelected(parent, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.category_array));
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(0);
    }

    private void handleItemSelected(AdapterView<?> parent, int position) {
        category = position-1;
        if(category != -1) {
            loadMarket = new LoadDataTask();
            loadMarket.startTask(BuyBonds.this);
            loadBondsData();
        }
    }

    private void loadBondsData() {

        firebase.collection("buyBonds")
                .document(Constants.KEY_BONDS_ARRAY[category])
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                buyingBondsList = documentSnapshot.getString(Constants.KEY_BONDS_ARRAY[category]);
                                assert buyingBondsList != null;
                                updateAdapterAndTextView(buyingBondsList);
                            }
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        loadMarket.cancelTask();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadMarket.cancelTask();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAdapterAndTextView(String updatedBonds) {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        ArrayList<String> myBondsArrayList = new ArrayList<>(Arrays.asList(updatedBonds.split(" ")));

        // Iterate over the list in reverse order to avoid index shifting
        for (int i = myBondsArrayList.size() - 1; i >= 0; i--) {
            if (myBondsArrayList.get(i).isEmpty()) {
                myBondsArrayList.remove(i);
            }
        }

        BondsListAdapter bondsListAdapter = new BondsListAdapter(BuyBonds.this, myBondsArrayList, Constants.KEY_BONDS_ARRAY[category], Current_UserId,"BuyBonds");
        recyclerView.setAdapter(bondsListAdapter);
    }


}