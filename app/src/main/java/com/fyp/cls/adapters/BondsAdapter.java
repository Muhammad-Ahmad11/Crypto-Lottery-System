package com.fyp.cls.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.fyp.cls.R;
import com.fyp.cls.activities.SaveBonds;
import com.fyp.cls.utilities.Constants;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BondsAdapter extends RecyclerView.Adapter<BondsAdapter.ViewHolder>  {
    private final Context context;
    private final List<String> title;
    private final Animation animation;
    private final int backgroundColorResId;
    private String className;
    private String userID;

    public BondsAdapter(Context context, String[] titleArray, String currentUserId, String className) {
        Set<String> uniqueTitles = new LinkedHashSet<>(Arrays.asList(titleArray));
        String[] uniqueTitlesArray = uniqueTitles.toArray(new String[0]);

        this.context = context;
        this.title = new ArrayList<>(Arrays.asList(uniqueTitlesArray));
        this.animation = AnimationUtils.loadAnimation(context, R.anim.animation1);
        this.backgroundColorResId = R.color.medium_green;
        this.userID = currentUserId;
        this.className = className;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_bond_category_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = title.get(position);
        if(text.equals("25000")){
            holder.bondType.setText("Premium");
            holder.textView.setText("25000");
        } else if (text.equals("40000")) {
            holder.bondType.setText("Premium");
            holder.textView.setText("40000");
        } else {
            holder.bondType.setText("Basic");
            holder.textView.setText(text);
        }

        holder.textView.setAnimation(animation);

        int colorResId = getColorResource(text);
        holder.relativeLayout.setBackgroundColor(ContextCompat.getColor(context, colorResId));

        holder.cardView.setOnClickListener(view -> {
            Intent intent = null;
            if(Objects.equals(this.className, "SaveBondsPrev")) {
                intent = new Intent(context, SaveBonds.class);

            }
            if (Objects.equals(this.className, "CheckBondsPrev")) {
                //intent = new Intent(context, CheckBonds.class);
            }
            if (Objects.equals(this.className, "ActivityGeneralDrawListsPrev")) {
                //intent = new Intent(context, ActivityGeneralDrawLists.class);
            }
            if (intent != null) {
                intent.putExtra("Category", position);
                intent.putExtra(Constants.KEY_USERID, this.userID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    private int getColorResource(String text) {
        return android.R.color.white;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView bondType;
        RelativeLayout relativeLayout;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview);
            relativeLayout = itemView.findViewById(R.id.category_layout);
            cardView = itemView.findViewById(R.id.card_view);
            bondType = itemView.findViewById(R.id.bondType);
        }
    }
}
