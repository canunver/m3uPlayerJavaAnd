package org.unver.m3uplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KanalAdapter extends RecyclerView.Adapter<KanalAdapter.ViewHolder> {
    private Context context;
    private ArrayList<M3UBilgi> data;

    public KanalAdapter(MainActivity context, ArrayList<M3UBilgi> data)
    {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kanalitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        M3UBilgi blg = data.get(position);
        holder.textView.setText(blg.tvgName);

        Handler handler = new Handler(Looper.getMainLooper());

        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(() -> {
            try {
                Log.d("URL", blg.tvgLogo);
                InputStream iStream = new URL(blg.tvgLogo).openStream();
                Bitmap image = BitmapFactory.decodeStream(iStream);
                handler.post(()->{
                    holder.imageView.setImageBitmap(image);
                });
            } catch (IOException e) {
                Log.d("URL", e.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        if(data == null) return 0;
        return data.size();
    }

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

        public TextView getTextView() {
            return textView;
        }
    }
}
