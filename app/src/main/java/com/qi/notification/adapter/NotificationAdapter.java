package com.qi.notification.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.qi.R;
import com.qi.notification.NotificationDetailActivity;
import com.qi.notification.model.NotificationModel;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    ArrayList<NotificationModel> arrayList = new ArrayList<>();
    Context mContext;
    String[] mColors = {"#8ac8fd", "#6dbaf9"};

    public NotificationAdapter(ArrayList<NotificationModel> arrayList, Context mContext) {
        this.arrayList = arrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.layout.setBackgroundColor(Color.parseColor(mColors[position % 2]));
        holder.Index.setText(String.valueOf(position+1)+".");
        holder.Title.setText(arrayList.get(position).getMessage());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Positoin: "+arrayList.get(position).getMessage());
                Intent i = new Intent(mContext, NotificationDetailActivity.class);
                i.putExtra("Message",arrayList.get(position).getMessage());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Index,Title;
        ConstraintLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Index = itemView.findViewById(R.id.notifi_index_count);
            Title = itemView.findViewById(R.id.Notifi_Head);
            layout = itemView.findViewById(R.id.leader_constraint);

        }
    }
}
