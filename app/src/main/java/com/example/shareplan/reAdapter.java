package com.example.shareplan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class reAdapter extends RecyclerView.Adapter<reAdapter.reViewHolder> {

    private ArrayList<TodoInfo> arrayList;
    private Context context;


    public reAdapter(ArrayList<TodoInfo> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public reViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item2, parent, false);
        reViewHolder holder = new reViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull reViewHolder holder, int position) {
        holder.lectime.setText(arrayList.get(position).getDate());
        holder.lecsubject.setText(arrayList.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class reViewHolder extends RecyclerView.ViewHolder {
        TextView lectime;
        TextView lecsubject;

        public reViewHolder(@NonNull View itemView) {
            super(itemView);
            this.lectime = itemView.findViewById(R.id.lectime);
            this.lecsubject = itemView.findViewById(R.id.lecsubject);
        }
    }
}

