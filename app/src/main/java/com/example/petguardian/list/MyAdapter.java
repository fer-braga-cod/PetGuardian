package com.example.petguardian.list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.petguardian.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nome.setText(dataList.get(position).getNome());
        holder.desc.setText(dataList.get(position).getDescricao());
        holder.tempo.setText(dataList.get(position).getTempo());
        long timestamp = dataList.get(position).getData();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(new Date(timestamp));
        holder.data.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{
    TextView nome, desc, tempo, data;
    CardView card;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        card = itemView.findViewById(R.id.recCard);
        desc = itemView.findViewById(R.id.recDesc);
        nome = itemView.findViewById(R.id.recTitle);
        tempo = itemView.findViewById(R.id.recHora);
        data = itemView.findViewById(R.id.recDate);
    }
}