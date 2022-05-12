package com.qi.Common.Login.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.qi.Common.Login.Model.StudentModels;
import com.qi.R;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<StudentModels> mStudentList;
    String dept;
    String[] mColors = {"#8ac8fd", "#6dbaf9"};


    public StudentAdapter(Context mContext,ArrayList<StudentModels> mStudentList) {
        this.mContext = mContext;
        this.mStudentList = mStudentList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleTxt,listTxtClass;
        ImageView imgIcon,SelectedIcon;
        ConstraintLayout backround;


        public MyViewHolder(View view) {
            super(view);

            mTitleTxt= (TextView) view.findViewById(R.id.listTxtTitle);
            listTxtClass= (TextView) view.findViewById(R.id.listTxtClass);
            imgIcon=(ImageView) view.findViewById(R.id.imagicon);
            backround = (ConstraintLayout)view.findViewById(R.id.relSub);
            SelectedIcon = (ImageView)view.findViewById(R.id.arrowImg);

        }

    }

    @NonNull
    @Override
    public StudentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_student_listview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.MyViewHolder holder, int position) {
        holder.backround.setBackgroundColor(Color.parseColor(mColors[position % 2]));
        holder.mTitleTxt.setText(mStudentList.get(position).getName());
        holder.imgIcon.setVisibility(View.VISIBLE);
        if(!mStudentList.get(position).getStudent_photo().equals("")) {
            Picasso.get()
                    .load(mStudentList.get(position).getStudent_photo().replaceAll(" ", "%20"))
                    .placeholder(R.drawable.boy)
                    .fit()
                    .into(holder.imgIcon);
//            Picasso.with(mContext).load(mStudentList.get(position).getStudent_photo().replaceAll(" ", "%20")).fit()
//                    .placeholder(R.drawable.boy)
//                    .into(holder.imgIcon);
        }

        holder.listTxtClass.setText(mStudentList.get(position).getClass_name()+" "+mStudentList.get(position).getStudent_division_name());

        if (mStudentList.get(position).isClicked()){
            System.out.println("TREE: IF "+mStudentList.get(position).isClicked());
            holder.SelectedIcon.setVisibility(View.VISIBLE);
        }else {
            System.out.println("TREE: ELSE "+mStudentList.get(position).isClicked());
            holder.SelectedIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mStudentList.size();
    }


}
