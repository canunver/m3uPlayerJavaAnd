package org.unver.m3uplayer;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class KanalAdapter extends RecyclerView.Adapter<KanalAdapter.ViewHolder> {
    private final MainActivity context;
    private final ArrayList<M3UBilgi> data;

    public KanalAdapter(MainActivity context, ArrayList<M3UBilgi> data)
    {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kanalitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        M3UBilgi blg = data.get(position);
        holder.textView.setText(blg.tvgName);
        M3UListeArac.ImageYukle(holder.imageView, blg.tvgLogo);
    }

    @Override
    public int getItemCount() {
        if(data == null) return 0;
        return data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void veriDegisti() {
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }

    }
}
