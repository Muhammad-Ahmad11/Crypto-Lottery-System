package com.fyp.cls.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.cls.R;
import com.fyp.cls.adapters.BondsAdapter;
import com.fyp.cls.utilities.Constants;
import com.fyp.cls.utilities.PreferenceManager;

import java.util.Objects;

public class SaveBondsPrev extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_bond_category);
        setupActionBar();
        setupRecyclerView();
    }

    private void setupActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Wallet");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#348180"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }

    private void setupRecyclerView() {

        Bundle bundle = getIntent().getExtras();
        String currentUserId = "";

        if(bundle != null) {
            currentUserId = bundle.getString(Constants.KEY_USERID);
        }

        PreferenceManager preferenceManager  = new PreferenceManager(SaveBondsPrev.this);

        RecyclerView prizeBonds = findViewById(R.id.list);
        String[] bonds = getResources().getStringArray(R.array.bonds_array);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        prizeBonds.setLayoutManager(gridLayoutManager);

        BondsAdapter bondsAdapter = new BondsAdapter(SaveBondsPrev.this, bonds, currentUserId, "SaveBondsPrev");
        prizeBonds.setAdapter(bondsAdapter);
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


    @Override
    public void onBackPressed() {
        // Perform the same behavior as the hardware back button
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
