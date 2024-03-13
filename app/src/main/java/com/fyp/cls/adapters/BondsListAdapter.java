package com.fyp.cls.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.cls.utilities.EthereumIntegration;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.fyp.cls.R;
import com.fyp.cls.utilities.Constants;
import com.fyp.cls.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BondsListAdapter extends RecyclerView.Adapter<BondsListAdapter.ViewHolder>  {
    private final Context context;
    private List<String> prizeBondsList;
    private Animation animation;
    private int filerTax = 0;
    private int nonFilerTax = 0;
    String uniqueBonds="";
    private String firstPrize;
    private String  searched;
    private String secondPrize;
    private String className;
    private String drawDate;
    private String drawNumber;
    PreferenceManager preferenceManager;
    private String userID;
    int totalBonds;
    String category = "";

    FirebaseFirestore firestore;
    EthereumIntegration ethereumIntegration;

    public BondsListAdapter(Context context, List<String> prizeBondsList, String category, String currentUserId, String className) {
        ethereumIntegration = new EthereumIntegration(context, "503f38a9c967ed597e47fe25643985f032b072db8075426a92110f82df48dfcb");
        ethereumIntegration.strictMode();
        ethereumIntegration.connectToEthNetwork();

        Set<String> uniqueprizeBondsLists = new LinkedHashSet<>(prizeBondsList);
        prizeBondsList.clear();
        prizeBondsList.addAll(uniqueprizeBondsLists);

        searched = "";
        Iterator<String> iterator = prizeBondsList.iterator();
        while (iterator.hasNext()) {
            String prizeBondsListz = iterator.next();
            if (prizeBondsListz.trim().isEmpty()) {
                iterator.remove();
            }
        }

        totalBonds = 0;
        this.context = context;
        this.drawDate = "";
        this.drawNumber = "";
        preferenceManager = new PreferenceManager(context);
        this.prizeBondsList = prizeBondsList;
        this.category = category;
        this.userID = currentUserId;
        this.className = className;
        if((!Objects.equals(context.getClass().getSimpleName(), "SellBonds")) ||
                (!Objects.equals(context.getClass().getSimpleName(), "SellBonds"))) {
            this.animation = AnimationUtils.loadAnimation(context, R.anim.animation1);
        }
    }

    public BondsListAdapter(Context context, List<String> prizeBondsList, String category, String currentUserId, String className, String searched) {
        Set<String> uniqueprizeBondsLists = new LinkedHashSet<>(prizeBondsList);
        prizeBondsList.clear();
        prizeBondsList.addAll(uniqueprizeBondsLists);

        Iterator<String> iterator = prizeBondsList.iterator();
        while (iterator.hasNext()) {
            String prizeBondsListz = iterator.next();
            if (prizeBondsListz.trim().isEmpty()) {
                iterator.remove();
            }
        }

        this.searched = searched;
        totalBonds = 0;
        this.drawDate = "";
        this.drawNumber = "";
        this.context = context;
        preferenceManager = new PreferenceManager(context);
        this.prizeBondsList = prizeBondsList;
        this.category = category;
        this.userID = currentUserId;
        this.className = className;
        if((!Objects.equals(context.getClass().getSimpleName(), "SellBonds"))) {
            this.animation = AnimationUtils.loadAnimation(context, R.anim.animation1);
        }
    }

    public BondsListAdapter(Context context, List<String> prizeBondsList, String category, String currentUserId, String className, String firstPrize, String secondPrize, String drawDate, String drawNumber) {
        Set<String> uniqueprizeBondsLists = new LinkedHashSet<>(prizeBondsList);
        prizeBondsList.clear();
        prizeBondsList.addAll(uniqueprizeBondsLists);

        Iterator<String> iterator = prizeBondsList.iterator();
        while (iterator.hasNext()) {
            String prizeBondsListz = iterator.next();
            if (prizeBondsListz.trim().isEmpty()) {
                iterator.remove();
            }
        }

        this.drawNumber = drawNumber;
        this.drawDate = drawDate;
        searched = "";
        totalBonds = 0;
        this.context = context;
        this.prizeBondsList = prizeBondsList;
        this.category = category;
        this.userID = currentUserId;
        this.className = className;
        this.firstPrize = firstPrize;
        this.secondPrize = secondPrize;
        preferenceManager = new PreferenceManager(context);
        if((!Objects.equals(context.getClass().getSimpleName(), "SellBonds"))) {
            this.animation = AnimationUtils.loadAnimation(context, R.anim.animation1);
        }
    }

    public void updateData(List<String> updatedList) {
        prizeBondsList = updatedList;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (Objects.equals(this.className, "SellBonds") || Objects.equals(this.className, "BuyBonds")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buybondlayout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_bond_item_layout, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String text = prizeBondsList.get(position);

        if(Objects.equals(context.getClass().getSimpleName(), "CheckBonds")) {
            holder.details.setVisibility(View.VISIBLE);
            holder.secondCard.setCardBackgroundColor(Color.WHITE);
            holder.secondCard.setVisibility(View.VISIBLE);

            /*holder.secondCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Inflate the custom layout
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.draw_details, null);

                    if(Objects.equals(category, "25000") || Objects.equals(category, "40000")) {
                        View v = dialogView.findViewById(R.id.nonFilerLayoutView);
                        v.setVisibility(View.GONE);
                        LinearLayout l = dialogView.findViewById(R.id.nonFilerLayout);
                        l.setVisibility(View.GONE);
                    }

                    //Remote Config
                    setupRemoteConfig(holder, dialogView);

                    TextView drawNumberw = dialogView.findViewById(R.id.drawNumber);
                    drawNumberw.setText("Draw: " + drawNumber);

                    TextView categoryw = dialogView.findViewById(R.id.textCategoryValue);
                    categoryw.setText(category);

                    TextView prize = dialogView.findViewById(R.id.prizeTypeValue);
                    prize.setText(holder.prizeNumber.getText());

                    TextView value = dialogView.findViewById(R.id.prizeAmtValue);
                    int index = getIndexForValue(category, Constants.KEY_BONDS_ARRAY);
                    int prizeValueforfilersandnonfilers = 0;
                    if(holder.prizeNumber.getText() == "3rd") {
                        value.setText("Rs. " + Constants.THIRD_PRIZE[index]);
                        prizeValueforfilersandnonfilers = Integer.parseInt(Constants.THIRD_PRIZE[index]);
                    } else if (holder.prizeNumber.getText() == "2nd") {
                        value.setText("Rs. " + Constants.SECOND_PRIZE[index]);
                        prizeValueforfilersandnonfilers = Integer.parseInt(Constants.SECOND_PRIZE[index]);
                    } else {
                        value.setText("Rs. " + Constants.FIRST_PRIZE[index]);
                        prizeValueforfilersandnonfilers = Integer.parseInt(Constants.FIRST_PRIZE[index]);
                    }

                    TextView bond = dialogView.findViewById(R.id.bondNumber);
                    bond.setText(holder.textView.getText());

                    TextView filer = dialogView.findViewById(R.id.filerValue);
                    double fifteenPercent = 0.15 * prizeValueforfilersandnonfilers;
                    double newAmount = prizeValueforfilersandnonfilers - fifteenPercent;
                    filer.setText(String.valueOf((int) newAmount));

                    TextView nonfiler = dialogView.findViewById(R.id.nonFilerValue);
                    double thirtyPercent = 0.30 * prizeValueforfilersandnonfilers;
                    double newAmount2 = prizeValueforfilersandnonfilers - thirtyPercent;
                    nonfiler.setText(String.valueOf((int) newAmount2));

                    String newDateString = "";
                    TextView drawDatew = dialogView.findViewById(R.id.textDrawDateValue);
                    drawDatew.setText(drawDate);

                    TextView drawExpiry = dialogView.findViewById(R.id.drawExpiryValue);
                    String dateString = drawDate;
                    int yearsToAdd = 6;

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
                    Calendar calendar = Calendar.getInstance();

                    try {
                        Date date = dateFormat.parse(dateString);
                        calendar.setTime(date);

                        calendar.add(Calendar.YEAR, yearsToAdd);

                        Date newDate = calendar.getTime();
                        newDateString = dateFormat.format(newDate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    drawExpiry.setText(newDateString);

                    TextView drawStatus = dialogView.findViewById(R.id.statusValue);

                    Calendar givenCalendar = Calendar.getInstance();
                    Calendar currentCalendar = Calendar.getInstance();

                    try {
                        Date givenDate = dateFormat.parse(drawDate);
                        givenCalendar.setTime(givenDate);

                        // Compare the given date to the current date
                        if (givenCalendar.before(currentCalendar)) {
                            drawStatus.setText("VALID");
                        } else {
                            drawStatus.setText("EXPIRED");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Button button = dialogView.findViewById(R.id.buttonExit);

                    // Create the dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialogView);

                    // Show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    // Set a click listener for the button within the dialog
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });*/
        }

        if(Objects.equals(context.getClass().getSimpleName(), "BuyBonds")) {

            holder.bondNumber.setText(text);
            holder.bondCategory.setText("Category: " + category);

            holder.buyBondBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //remove the bond from sell list and add to current user account

                    int currBalance = Integer.parseInt(preferenceManager.getString(Constants.KEY_ACC_BALANCE));

                    if (Integer.parseInt(category) <= currBalance) {

                        String remBalance = String.valueOf(currBalance - Integer.parseInt(category));
                        preferenceManager.putString(Constants.KEY_ACC_BALANCE, remBalance);

                        firestore = FirebaseFirestore.getInstance();

                        firestore.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(userID)
                                                .update(Constants.KEY_ACC_BALANCE, remBalance);

                        firestore.collection("buyBonds")
                                .document(category)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot.exists()) {
                                                String prevBonds = documentSnapshot.getString(category);
                                                String bondUserClickedToBuy = holder.bondNumber.getText().toString() + " ";

                                                assert prevBonds != null;
                                                String resultString = prevBonds.replace(bondUserClickedToBuy, "");
                                                firestore.collection("buyBonds")
                                                        .document(category)
                                                        .update(category, resultString)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                //update bonds in buyBonds
                                                                updateData(Arrays.asList(resultString.split(" ")));

                                                                //check for balance Then display Toast
                                                                Toast.makeText(context, "Purchased Successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        //updating data in user account
                                        firestore.collection(Constants.KEY_COLLECTION_USERS)
                                                .document(userID)
                                                .get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful() && task1.getResult() != null) {
                                                        DocumentSnapshot documentSnapshot = task1.getResult();
                                                        if (documentSnapshot.exists()) {

                                                            String prevBonds = documentSnapshot.getString(category);
                                                            String updatedBonds = prevBonds + holder.bondNumber.getText().toString() + " ";

                                                            if(Objects.equals(preferenceManager.getString(Constants.ACC_TYPE), "BASIC")) {

                                                                if(1 + Integer.parseInt(preferenceManager.getString(Constants.ACC_SPACE_OCCUPIED)) < 1001) {

                                                                    ethereumIntegration.setUserDataOnBlockchain(prevBonds + " " + holder.bondNumber.getText().toString());

                                                                    // Upload the pairs to Firebase
                                                                    updateBondsInFirebase(userID, updatedBonds);
                                                                } else {
                                                                    Toast.makeText(context, "Consider Upgrading you Account for more Space.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } else {
                                                                totalBonds = 0;
                                                                updateBondsInFirebase(userID, updatedBonds);
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                });
                    } else {
                        Toast.makeText(context, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(Objects.equals(context.getClass().getSimpleName(), "SellBonds")) {

            //here nuyBondBTn is just name actually it is sellBond Button
            holder.buyBondBtn.setText("SELL");
            holder.bondCategory.setText("Category: " + category);
            holder.buyBondBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //remove the bond from sell list and add to bank account
                    firestore = FirebaseFirestore.getInstance();
                    firestore.collection("buyBonds")
                            .document(category)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful() && task.getResult() != null) {

                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if (documentSnapshot.exists()) {

                                            //adding the bondNumebr that is sold to firebase all bondNumbers
                                            String prevBonds = documentSnapshot.getString(category);
                                            String bondUserClickedToSell = holder.bondNumber.toString();
                                            String resultString = prevBonds + " "  + bondUserClickedToSell;
                                            // Split the string into an array of strings
                                            String[] numberArray = resultString.split(" ");

                                            // Sort the array using a custom comparator that considers leading zeros
                                            Arrays.sort(numberArray, (s1, s2) -> Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2)));

                                            String userBalance = preferenceManager.getString(Constants.KEY_ACC_BALANCE);
                                            int number = Integer.parseInt(userBalance);

                                                FirebaseFirestore.getInstance()
                                                        .document(userID)
                                                        .update(Constants.KEY_ACC_BALANCE, String.valueOf(number + Integer.parseInt(category)))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                                preferenceManager.putString(Constants.KEY_ACC_BALANCE, String.valueOf((number + Integer.parseInt(category))));

                                                                // Join the sorted array back into a string
                                                                String sortedString = String.join(" ", numberArray);
                                                                firestore.collection("buyBonds")
                                                                        .document(category)
                                                                        .update(category, sortedString)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                Toast.makeText(context, "Sold Successfully", Toast.LENGTH_SHORT).show();
                                                                                //update bonds in buyBonds
                                                                                updateData(Arrays.asList(sortedString.split(" ")));
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(context, "Failed to update Account balance", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                        }
                                    }
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    //updating data in user account
                                    firestore.collection(Constants.KEY_COLLECTION_USERS)
                                            .document(userID)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful() && task1.getResult() != null) {
                                                    DocumentSnapshot documentSnapshot = task1.getResult();
                                                    if (documentSnapshot.exists()) {

                                                        String prevBonds = documentSnapshot.getString(category);
                                                        String sellingBond = holder.bondNumber.toString();
                                                        assert prevBonds != null;
                                                        String updatedBonds = prevBonds.replace(sellingBond, "");

                                                        if(Objects.equals(preferenceManager.getString(Constants.ACC_TYPE), "BASIC")) {

                                                            if(1 + Integer.parseInt(preferenceManager.getString(Constants.ACC_SPACE_OCCUPIED)) < 1001) {

                                                                ethereumIntegration.setUserDataOnBlockchain(prevBonds + " " + holder.bondNumber.getText().toString());

                                                                // Upload the pairs to Firebase
                                                                updateBondsInFirebase(userID, updatedBonds);
                                                            } else {
                                                                Toast.makeText(context, "Consider Upgrading you Account for more Space.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            totalBonds = 0;
                                                            updateBondsInFirebase(userID, updatedBonds);
                                                        }
                                                    }
                                                }
                                            });
                                }
                            });
                }
            });
        }

        if(Objects.equals(context.getClass().getSimpleName(), "CheckBonds")) {
            holder.prizeNumber.setText("3rd");
            if (firstPrize != null) {
                String[] secondPrizes = this.firstPrize.split(" ");

                for (String prize : secondPrizes) {
                    if ((prize.trim()).equals(text.trim())) {
                        holder.prizeNumber.setText("1st");
                        break;
                    }
                }
            }
            if (secondPrize != null) {
                String[] secondPrizes = this.secondPrize.split(" ");

                for (String prize : secondPrizes) {
                    if ((prize.trim()).equals(text.trim())) {
                        holder.prizeNumber.setText("2nd");
                        break;
                    }
                }
            }
        }

        if(!Objects.equals(context.getClass().getSimpleName(), "BuyBonds") &&
                !Objects.equals(context.getClass().getSimpleName(), "SellBonds")) {
            holder.textView.setText(text);
            holder.textView.setAnimation(animation);
            int colorResId = getColorResource(text);
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, colorResId));

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Objects.equals(context.getClass().getSimpleName(), "ActivityGeneralDrawListsPrev")) {
                        Intent intent = null;
                        //intent = new Intent(context, ActivityGeneralDrawLists.class);
                        //intent.putExtra("Category", holder.getAdapterPosition());
                        //intent.putExtra(Constants.KEY_USERID, userID);
                        //context.startActivity(intent);
                    }
                }
            });
        }

    }

    private void updateBondsInFirebase(String currentUserId, String updatedBonds) {

        String[] bondsArray = updatedBonds.split(" ");

        Set<String> uniqueBondsSet = new LinkedHashSet<>(Arrays.asList(bondsArray));
        uniqueBonds = String.join(" ", uniqueBondsSet);
        String[] uniqueBondsArray = uniqueBonds.split(" ");
        uniqueBonds += " ";

        Map<String, Object> updateData = new HashMap<>();
        updateData.put(category, uniqueBonds);
        updateData.put("total" + category, String.valueOf(uniqueBondsArray.length));

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUserId)
                .update(updateData);
    }

    @Override
    public int getItemCount() {
        return prizeBondsList.size();
    }

    private int getColorResource(String text) {
        return android.R.color.white;
    }

    public int getIndexForValue(String valueToFind, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(valueToFind)) {
                return i; // Return the index if the value is found
            }
        }
        return -1; // Return -1 if the value is not found in the array
    }

    private void setupRemoteConfig(ViewHolder holder, View dialogView) {
        //TextView filerText = dialogView.findViewById(R.id.filer);
        //TextView nonFilerText = dialogView.findViewById(R.id.nonfiler);

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1) // Set your desired fetch interval in seconds
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults); // Point to your default values XML

        mFirebaseRemoteConfig.fetchAndActivate()
                        .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                            @Override
                            public void onComplete(@NonNull Task<Boolean> task) {
                                if(task.isSuccessful()) {
                                    String filerTextSet = mFirebaseRemoteConfig.getString("filer");
                                    String nonFilerTextSet = mFirebaseRemoteConfig.getString("nonfiler");
                                    //filerText.setText(filerTextSet);
                                    //nonFilerText.setText(nonFilerTextSet);
                                    filerTax = Integer.parseInt(filerTextSet.replaceAll("[^0-9]", ""));
                                    nonFilerTax = Integer.parseInt(nonFilerTextSet.replaceAll("[^0-9]", ""));
                                    //Toast.makeText(context, String.valueOf(filerTax) + "  " + String.valueOf(nonFilerTax), Toast.LENGTH_SHORT).show();

                                    int index = getIndexForValue(category, Constants.KEY_BONDS_ARRAY);
                                    int prizeValueforfilersandnonfilers = 0;
                                    if(holder.prizeNumber.getText() == "3rd") {

                                        prizeValueforfilersandnonfilers = Integer.parseInt(Constants.THIRD_PRIZE[index]);
                                    } else if (holder.prizeNumber.getText() == "2nd") {

                                        prizeValueforfilersandnonfilers = Integer.parseInt(Constants.SECOND_PRIZE[index]);
                                    } else {

                                        prizeValueforfilersandnonfilers = Integer.parseInt(Constants.FIRST_PRIZE[index]);
                                    }

                                    //TextView filer = dialogView.findViewById(R.id.filerValue);
                                    double fifteenPercent = (filerTax*0.01) * prizeValueforfilersandnonfilers;
                                    double newAmount = prizeValueforfilersandnonfilers - fifteenPercent;
                                    //filer.setText(String.valueOf((int) newAmount));

                                    //TextView nonfiler = dialogView.findViewById(R.id.nonFilerValue);
                                    double thirtyPercent = (nonFilerTax*0.01) * prizeValueforfilersandnonfilers;
                                    double newAmount2 = prizeValueforfilersandnonfilers - thirtyPercent;
                                    //nonfiler.setText(String.valueOf((int) newAmount2));
                                }
                            }
                        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout linearLayout;
        CardView cardView;
        TextView prizeNumber;
        CardView secondCard;
        ImageView details;
        CardView firstCard;

        Button buyBondBtn;
        TextView bondCategory;
        TextView bondNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.item);
            linearLayout = itemView.findViewById(R.id.bond_item_layout);
            cardView = itemView.findViewById(R.id.bond_item_card);
            prizeNumber = itemView.findViewById(R.id.prizeNumber);

            firstCard = itemView.findViewById(R.id.firstCard);
            secondCard = itemView.findViewById(R.id.secondCard);
            details = itemView.findViewById(R.id.details);

            buyBondBtn = itemView.findViewById(R.id.buy_bond_btn);
            bondCategory = itemView.findViewById(R.id.bondCategory);
            bondNumber = itemView.findViewById(R.id.bondNumber);
        }
    }
}
