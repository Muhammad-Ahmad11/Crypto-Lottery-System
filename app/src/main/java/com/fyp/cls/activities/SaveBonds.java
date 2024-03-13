package com.fyp.cls.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.cls.R;
import com.fyp.cls.adapters.BondsListAdapter;
import com.fyp.cls.utilities.Constants;
import com.fyp.cls.utilities.EthereumIntegration;
import com.fyp.cls.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SaveBonds extends AppCompatActivity {


    private String myBondsString = "";
    private String currentUserId;
    private int category = 0;
    RecyclerView myBondsList;
    FirebaseFirestore firestore;
    String uniqueBonds = "";
    PreferenceManager preferenceManager;
    androidx.appcompat.widget.SearchView searchView;
    EditText searchEditText;
    private LoadDataTask loadDataTask;
    EthereumIntegration ethereumIntegration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_bonds);

        initialize_components();
        loadDataTask = new LoadDataTask();
        loadDataTask.startTask(this);
        loadBondsData();
        setupSearchView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_sort) {

            String[] myStringArray = myBondsString.split(" ");

            Arrays.sort(myStringArray, (a, b) -> {
                if (a.length() == b.length()) {
                    return a.compareTo(b);
                }
                return Integer.compare(a.length(), b.length());
            });

            String sortedBondsString = String.join(" ", myStringArray);

            //updateSortedBonds
            ethereumIntegration.setUserDataOnBlockchain(sortedBondsString);
            //updateAdapterAndTextView(sortedBondsString, "");

            updateBondsInFirebase(currentUserId, sortedBondsString);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize_components() {
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#348180"));

        assert actionBar != null;
        actionBar.setBackgroundDrawable(colorDrawable);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            category = bundle.getInt("Category");
            currentUserId = bundle.getString(Constants.KEY_USERID);
        }

        firestore = FirebaseFirestore.getInstance();

        preferenceManager = new PreferenceManager(this);
        currentUserId = preferenceManager.getString(Constants.KEY_USERID);

        ethereumIntegration = new EthereumIntegration(this, "503f38a9c967ed597e47fe25643985f032b072db8075426a92110f82df48dfcb");
        ethereumIntegration.strictMode();
        ethereumIntegration.connectToEthNetwork();

        actionBar.setTitle("Wallet(" + Constants.KEY_BONDS_ARRAY[category] + ")");

        myBondsList = findViewById(R.id.myBonds);

        searchView = findViewById(R.id.search);
    }

    private void setupSearchView() {

        searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        searchEditText.setTextColor(getResources().getColor(R.color.teal_700));

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called when the text is changed.
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text is changed.
                int maxLength = 6; // Maximum allowed length
                if (s.length() > maxLength) {
                    String trimmedText = s.toString().substring(0, maxLength);
                    searchEditText.setText(trimmedText);
                    searchEditText.setSelection(trimmedText.length());
                }
            }
        });

        searchView.clearFocus();

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    updateAdapterAndTextView(myBondsString, "");
                }
                return true;
            }
        });
    }

    public void searchList(String text) {

        boolean found  = false;
        String[] myStringArray = myBondsString.split(" ");
        StringBuilder foundStrings = new StringBuilder();

        for (int i = 0; i < myStringArray.length; i++) {
            if (myStringArray[i].equals(text)) {
                foundStrings.append(myStringArray[i]);
                foundStrings.append(" ");
                found =  true;
            }
        }

        if (found) {
            updateAdapterAndTextView(foundStrings.toString(), "searched");
        } else {
            updateAdapterAndTextView(myBondsString, "");
            Toast.makeText(SaveBonds.this, "NOT FOUND", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBondsData() {

        //ethereumIntegration.makeTransaction(Integer.valueOf(category));
        myBondsString = ethereumIntegration.getUserDataFromBlockchain();
        if (myBondsString != null && !myBondsString.trim().isEmpty()) {
            updateAdapterAndTextView(myBondsString, "");
            loadDataTask.cancelTask();
        }

        /*CollectionReference usersCollection = firebase.collection(Constants.KEY_COLLECTION_USERS);
        usersCollection.document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {

                            myBondsString = documentSnapshot.getString(Constants.KEY_BONDS_ARRAY[category]);
                            if (myBondsString != null && !myBondsString.trim().isEmpty()) {
                                updateAdapterAndTextView(myBondsString, "");
                            }
                        }
                    }
                }).addOnCompleteListener(task -> loadDataTask.cancelTask());*/
    }

    private void updateBondsInFirebase(String currentUserId, String updatedBonds) {

        String[] bondsArray = updatedBonds.split(" ");

        Set<String> uniqueBondsSet = new LinkedHashSet<>(Arrays.asList(bondsArray));
        uniqueBonds = String.join(" ", uniqueBondsSet);
        String[] uniqueBondsArray = uniqueBonds.split(" ");
        uniqueBonds += " ";

        Map<String, Object> updateData = new HashMap<>();
        updateData.put(Constants.KEY_BONDS_ARRAY[category], uniqueBonds);
        updateData.put("total" + Constants.KEY_BONDS_ARRAY[category], String.valueOf(uniqueBondsArray.length));

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUserId)
                .update(updateData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateAdapterAndTextView(uniqueBonds, "");
                    }
                });
    }

    private void updateAdapterAndTextView(String updatedBonds, String searched) {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        myBondsList.setLayoutManager(gridLayoutManager);

        ArrayList<String> myBondsArrayList = new ArrayList<>(Arrays.asList(updatedBonds.split(" ")));

        // Iterate over the list in reverse order to avoid index shifting
        for (int i = myBondsArrayList.size() - 1; i >= 0; i--) {
            if (myBondsArrayList.get(i).isEmpty()) {
                myBondsArrayList.remove(i);
            }
        }

        if(searched.equals("searched")) {
            BondsListAdapter bondsListAdapter = new BondsListAdapter(SaveBonds.this, myBondsArrayList, Constants.KEY_BONDS_ARRAY[category], currentUserId,"SaveBonds", "searched");
            myBondsList.setAdapter(bondsListAdapter);
        } else {
            BondsListAdapter bondsListAdapter = new BondsListAdapter(SaveBonds.this, myBondsArrayList, Constants.KEY_BONDS_ARRAY[category], currentUserId,"SaveBonds");
            myBondsList.setAdapter(bondsListAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}