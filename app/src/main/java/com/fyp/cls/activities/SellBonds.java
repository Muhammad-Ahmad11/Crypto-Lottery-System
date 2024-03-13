package com.fyp.cls.activities;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.cls.adapters.BondsListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.fyp.cls.R;
import com.fyp.cls.utilities.Constants;
import com.fyp.cls.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SellBonds extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore firebase;
    String Current_UserId;
    PreferenceManager preferenceManager;
    LoadDataTask loadMarket;
    int category = 0;
    String sellingBondsList = "";
    Spinner categorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sell_bonds);

        initialize_components();

        loadBondsData();
    }

    private void initialize_components() {
        preferenceManager = new PreferenceManager(this);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.show();
        actionBar.setTitle("Sell Bonds");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#348180"));
        actionBar.setBackgroundDrawable(colorDrawable);

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

        firebase = FirebaseFirestore.getInstance();

        loadMarket = new LoadDataTask();
        loadMarket.startTask(SellBonds.this);
    }

    private void handleItemSelected(AdapterView<?> parent, int position) {
        category = position;
    }

    private void loadBondsData() {

        firebase.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USERID))
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful() && task1.getResult() != null) {
                        DocumentSnapshot documentSnapshot = task1.getResult();
                        if (documentSnapshot.exists()) {
                            sellingBondsList = documentSnapshot.getString(Constants.KEY_BONDS_ARRAY[category]);
                            assert sellingBondsList != null;
                            updateAdapterAndTextView(sellingBondsList);
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

        BondsListAdapter bondsListAdapter = new BondsListAdapter(SellBonds.this, myBondsArrayList, Constants.KEY_BONDS_ARRAY[category], Current_UserId,"SellBonds");
        recyclerView.setAdapter(bondsListAdapter);
    }

}