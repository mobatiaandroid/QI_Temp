package com.qi.LeaderBoard.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.qi.LeaderBoard.model.LeaderModel;
import com.qi.R;

import java.util.List;

public class LeadersAdapter extends RecyclerView.Adapter<LeadersAdapter.MyViewHolder> {

    private List<LeaderModel> moviesList;
    String[] mColors = {"#8ac8fd", "#6dbaf9"};

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView count, name, score;
        ConstraintLayout layout;

        public MyViewHolder(View view) {
            super(view);
            count = (TextView) view.findViewById(R.id.index_count);
            name = (TextView) view.findViewById(R.id.leader_students_name);
            score = (TextView) view.findViewById(R.id.leaders_points);
            layout = (ConstraintLayout)view.findViewById(R.id.leader_constraint);
        }
    }

    public LeadersAdapter(List<LeaderModel> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leader_recycler_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.layout.setBackgroundColor(Color.parseColor(mColors[position % 2]));
        LeaderModel movie = moviesList.get(position);
        holder.count.setText(String.valueOf(position+1));
        holder.name.setText(movie.getName());
      /*  holder.score.setText(movie.getScore());*/
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
