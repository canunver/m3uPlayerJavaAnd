package org.unver.m3uplayer;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class YayinListesiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_FILM = 1;
    private static final int VIEW_TYPE_SERI = 2;
    private static final int VIEW_TYPE_TV = 0;
    private final YayinFragment yayinFragment;
    private final ArrayList<M3UBilgi> data;

    public YayinListesiAdapter(YayinFragment yayinFragment, ArrayList<M3UBilgi> data) {
        this.yayinFragment = yayinFragment;
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        // Determine the view type based on the position or data at that position
        M3UBilgi blg = data.get(position);
        if (blg.Tur == M3UBilgi.M3UTur.film)
            return VIEW_TYPE_FILM;
        else if (blg.Tur == M3UBilgi.M3UTur.seri)
            return VIEW_TYPE_SERI;
        else
            return VIEW_TYPE_TV;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FILM)
            return new FilmViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filmitem, parent, false));
        else if (viewType == VIEW_TYPE_SERI)
            return new SeriViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.seriitem, parent, false));
        else
            return new KanalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.kanalitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        M3UBilgi blg = data.get(position);
        if (blg.Tur == M3UBilgi.M3UTur.tv)
            ((KanalViewHolder) holder).bind(blg, position);
        {
            if (blg.Tur == M3UBilgi.M3UTur.film)
                ((FilmViewHolder) holder).bind(blg, position);
            else if (blg.Tur == M3UBilgi.M3UTur.seri)
                ((SeriViewHolder) holder).bind(blg, position);
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) return 0;
        return data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void veriDegisti() {
        notifyDataSetChanged();
    }

    public class MyOnClickListener implements View.OnClickListener {
        private final RecyclerView.ViewHolder holder;
        long prevClickTimeInMS = 0;

        public MyOnClickListener(RecyclerView.ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            int islem = 0;
            if(v.getId() == R.id.filmTmdb)
            {
                islem = 2;
            }
            else {
                long currTimeInMS = Calendar.getInstance().getTimeInMillis();
                if (currTimeInMS - prevClickTimeInMS < 800) {
                    islem = 1;
                }
                prevClickTimeInMS = currTimeInMS;
            }

            if(islem!=0)
            {
                String bolum;
                String sezon;
                if (holder instanceof SeriViewHolder) {
                    SeriViewHolder svh = (SeriViewHolder) holder;
                    sezon = svh.aktifSezonAd;
                    bolum = svh.aktifBolumAd;
                } else {
                    sezon = null;
                    bolum = null;
                }
                yayinFragment.NesneSecildi(islem, holder.getAdapterPosition(), sezon, bolum);
            }
        }
    }

    public class KanalViewHolder extends RecyclerView.ViewHolder {
        public TextView kanalAd;
        public TextView programAd;
        public ImageView kanalLogo;

        public KanalViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new MyOnClickListener(this));
            kanalAd = itemView.findViewById(R.id.kanalAd);
            kanalLogo = itemView.findViewById(R.id.kanalLogo);
            programAd = itemView.findViewById(R.id.programAd);
        }

        public void bind(M3UBilgi blg, int ignoredPosition) {
            kanalAd.setText(blg.tvgName);
            programAd.setText(R.string.NoEPG);
            M3UListeArac.ImageYukle(kanalLogo, blg.tvgLogo);
        }
    }

    public class FilmViewHolder extends RecyclerView.ViewHolder {
        public TextView filmAd;
        public TextView filmOzellik;
        public TextView filmAciklama;
        public ImageView filmAfis;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            filmAd = itemView.findViewById(R.id.filmAd);
            filmAfis = itemView.findViewById(R.id.filmAfis);
            filmOzellik = itemView.findViewById(R.id.filmOzellik);
            filmAciklama = itemView.findViewById(R.id.filmAciklama);
            Button filmTmdb = itemView.findViewById(R.id.filmTmdb);
            filmTmdb.setOnClickListener(new MyOnClickListener(this));
            itemView.setOnClickListener(new MyOnClickListener(this));
        }

        public void bind(M3UBilgi blg, int ignoredPosition) {
            filmAd.setText(blg.tvgName);
            filmOzellik.setText("Puan, Türler,  Yayın Tarihi ");
            filmAciklama.setText(blg.aciklamaBul());
            M3UListeArac.ImageYukle(filmAfis, blg.afisBul(500));
        }
    }

    public class SeriViewHolder extends RecyclerView.ViewHolder {
        public TextView seriAd;
        public TextView seriOzellik;
        public TextView seriAciklama;
        public ImageView seriAfis;
        public AutoCompleteTextView sezonSec;
        public AutoCompleteTextView bolumSec;
        public String aktifSezonAd;
        public String aktifBolumAd;
        public ArrayAdapter<String> sezonAdapter;
        public ArrayAdapter<String> bolumAdapter;
        private M3UBilgi blg;

        public SeriViewHolder(@NonNull View itemView) {
            super(itemView);

            seriAd = itemView.findViewById(R.id.seriAd);
            seriAfis = itemView.findViewById(R.id.seriAfis);
            seriOzellik = itemView.findViewById(R.id.seriOzellik);
            seriAciklama = itemView.findViewById(R.id.seriAciklama);
            sezonSec = itemView.findViewById(R.id.sezonSec);
            bolumSec = itemView.findViewById(R.id.bolumSec);

            sezonSec.setOnItemClickListener((parent, view, position, id) -> SezonSecildi(position));
            bolumSec.setOnItemClickListener((parent, view, position, id) -> BolumSecildi(position));
            itemView.setOnClickListener(new MyOnClickListener(this));
        }

        public void bind(M3UBilgi blg, int ignoredPosition) {
            this.blg = blg;
            seriAd.setText(blg.seriAd);
            seriOzellik.setText(blg.filmYil);
            M3UListeArac.ImageYukle(seriAfis, blg.tvgLogo);
            //seriAciklama.setText("blg.seriYil");

            ArrayList<String> al = new ArrayList<>();
            for (Sezon s : blg.seriSezonlari) {
                al.add(s.sezonAd);
            }

            sezonAdapter = new ArrayAdapter<>(yayinFragment.mainActivity, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, al);
            sezonSec.setAdapter(sezonAdapter);
            if(sezonAdapter!=null && !sezonAdapter.isEmpty()) {
                sezonSec.setText(sezonAdapter.getItem(0), false);
                SezonSecildi(0);
            }
        }

        public void SezonSecildi(int position) {
            aktifSezonAd = sezonAdapter.getItem(position);

            ArrayList<String> al = new ArrayList<>();
            Sezon s = blg.SezonBul(aktifSezonAd);
            if (s != null) {
                for (Bolum b : s.bolumler) {
                    al.add(b.bolum);
                    for (int i = 1; i < b.idler.size(); i++) {
                        al.add(b.bolum + " (" + (i + 1) + ")");
                    }
                }
            }
            if (al.size() == 0)
                al.add("-");

            bolumAdapter = new ArrayAdapter<>(yayinFragment.mainActivity, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, al);
            bolumSec.setAdapter(bolumAdapter);
            bolumSec.setText(bolumAdapter.getItem(0), false);
            BolumSecildi(0);
        }

        public void BolumSecildi(int position) {
            aktifBolumAd = bolumAdapter.getItem(position);
        }
    }
}
