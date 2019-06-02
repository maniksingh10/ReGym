package com.veersgym.manik;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomRecycle extends RecyclerView.Adapter<CustomRecycle.CustomViewHolder> implements View.OnLongClickListener {

    private ArrayList<GymMember> gymMembersRecycle = new ArrayList<>();
    private Context context;
    final private onLongItemCustom onLongItemCustom;

    public CustomRecycle(ArrayList<GymMember> gymMembersRecycle, Context context, onLongItemCustom onLongItemCustom) {
        this.gymMembersRecycle = gymMembersRecycle;
        this.context = context;
        this.onLongItemCustom = onLongItemCustom;
    }

    public interface onLongItemCustom{
        void onLongItemClicked(int clickeditem);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.one_member, viewGroup, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        GymMember gymMember = gymMembersRecycle.get(position);
        holder.name_one_tv.setText(gymMember.getName());
        holder.joindate_one_tv.setText(DateConverter.millsToDateFormat(gymMember.getJoindate()));
        holder.branch_one_tv.setText(gymMember.getBranch());
        holder.gender_one_tv.setText(gymMember.getGender());
        holder.id_one_tv.setText(String.valueOf(gymMember.getGymid()));
        holder.days_remain_tv.setText(String.valueOf(days_remain(gymMember.getFeedate())));

        if (days_remain(gymMember.getFeedate()) < 0) {
            holder.constraintLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.border));
            holder.cardView.setVisibility(View.VISIBLE);
        } else {
            holder.constraintLayout.setBackgroundResource(0);
            holder.cardView.setVisibility(View.VISIBLE);
        }

    }

    private int days_remain(long feedate) {
        long diff = feedate - System.currentTimeMillis();
        // Calculate difference in days
        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
        return diffDays;
    }

    @Override
    public int getItemCount() {
        return gymMembersRecycle.size();
    }

    @Override
    public boolean onLongClick(View v) {

        return true;
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView name_one_tv, gender_one_tv, branch_one_tv, id_one_tv, joindate_one_tv, days_remain_tv;
        ConstraintLayout constraintLayout;
        CardView cardView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setVisibility(View.GONE);
            name_one_tv = itemView.findViewById(R.id.tv_one_name);
            gender_one_tv = itemView.findViewById(R.id.tv_one_gender);
            branch_one_tv = itemView.findViewById(R.id.tv_one_branch);
            id_one_tv = itemView.findViewById(R.id.tv_one_id);
            joindate_one_tv = itemView.findViewById(R.id.tv_one_joindate);
            days_remain_tv = itemView.findViewById(R.id.tv_one_daysremain);
            constraintLayout = itemView.findViewById(R.id.container_one_member);
            cardView = itemView.findViewById(R.id.one_card_view);
            cardView.setVisibility(View.GONE);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public boolean onLongClick(View v) {
            int pos = getAdapterPosition();
            onLongItemCustom.onLongItemClicked(pos);
            return true;
        }
    }
}
