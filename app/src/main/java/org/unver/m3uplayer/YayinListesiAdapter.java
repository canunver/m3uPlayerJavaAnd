package org.unver.m3uplayer;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
            if (v.getId() == R.id.filmTmdb || v.getId() == R.id.seriTmdb) {
                islem = 2;
            } else if (v.getId() == R.id.seriTmdbDetay) {
                islem = 3;
            } else {
                long currTimeInMS = Calendar.getInstance().getTimeInMillis();
                if (currTimeInMS - prevClickTimeInMS < 800) {
                    islem = 1;
                }
                prevClickTimeInMS = currTimeInMS;
            }

            if (islem != 0) {
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
                yayinFragment.nesneSecildi(yayinFragment.kanalAdapter, islem, holder.getAdapterPosition(), sezon, bolum);
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
            if (blg.tmdbBagBul() != null) {
                filmOzellik.setText(blg.ozellikBul(yayinFragment.getString(R.string.filmOzellik)));
            } else
                filmOzellik.setText(R.string.tmdbBagYok);
            filmAciklama.setText(blg.aciklamaBul());
            M3UListeArac.ImageYukle(filmAfis, blg.afisBul(500));
        }
    }

    public class SeriViewHolder extends RecyclerView.ViewHolder {
        public TextView seriAd;
        public TextView seriOzellik;
        public TextView seriAciklama;
        public TextView bolumAciklama;
        public ImageView seriAfis;
        public AutoCompleteTextView sezonSec;
        public AutoCompleteTextView bolumSec;
        public String aktifSezonAd;
        public Sezon aktifSezon;
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
            bolumAciklama = itemView.findViewById(R.id.bolumAciklama);

            Button seriTmdb = itemView.findViewById(R.id.seriTmdb);
            seriTmdb.setOnClickListener(new MyOnClickListener(this));

            Button bolumTmdb = itemView.findViewById(R.id.seriTmdbDetay);
            bolumTmdb.setOnClickListener(new MyOnClickListener(this));

            sezonSec.setOnItemClickListener((parent, view, position, id) -> SezonSecildi(position, -1));
            bolumSec.setOnItemClickListener((parent, view, position, id) -> BolumSecildi(position));
            itemView.setOnClickListener(new MyOnClickListener(this));
        }

        public void bind(M3UBilgi blg, int ignoredPosition) {
            this.blg = blg;
            seriAd.setText(blg.seriAd);
            if (blg.tmdbBagBul() != null) {
                seriOzellik.setText(blg.ozellikBul(yayinFragment.getString(R.string.filmOzellik)));
            } else
                seriOzellik.setText(R.string.tmdbBagYok);

            M3UListeArac.ImageYukle(seriAfis, blg.tvgLogo);
            seriAciklama.setText(blg.aciklamaBul());

            ArrayList<String> al = new ArrayList<>();
            int secSezon = 0;
            int secBolum = 0;
            for (int si = 0; si < blg.seriSezonlari.size(); si++) {
                Sezon s = blg.seriSezonlari.get(si);
                al.add(s.sezonAd);
                int yer = s.seyredilenSonBul();
                if (yer > -1) {
                    secSezon = si;
                    secBolum = yer;
                }
            }

            sezonAdapter = new ArrayAdapter<>(yayinFragment.mainActivity, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, al);
            sezonSec.setAdapter(sezonAdapter);
            if (sezonAdapter != null && !sezonAdapter.isEmpty()) {
                sezonSec.setText(sezonAdapter.getItem(secSezon), false);
                SezonSecildi(secSezon, secBolum);
            }
        }

        public void SezonSecildi(int position, int bolumPosition) {
            aktifSezonAd = sezonAdapter.getItem(position);
            aktifSezon = blg.SezonBul(aktifSezonAd);
            int secPosition = 0;
            ArrayList<String> al = new ArrayList<>();
            if (aktifSezon != null) {
                if (bolumPosition == -1)
                    bolumPosition = aktifSezon.seyredilenSonBul();
                for (int bi = 0; bi < aktifSezon.bolumler.size(); bi++) {
                    Bolum b = aktifSezon.bolumler.get(bi);
                    al.add(b.bolum);
                    if (bi == bolumPosition)
                        secPosition = al.size() - 1;
                    for (int i = 1; i < b.ids.size(); i++) {
                        al.add(b.bolum + " (" + (i + 1) + ")");
                    }
                }
            }
            if (al.size() == 0)
                al.add("-");
            if (secPosition == -1 || secPosition >= al.size())
                secPosition = 0;
            bolumAdapter = new ArrayAdapter<>(yayinFragment.mainActivity, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, al);
            bolumSec.setAdapter(bolumAdapter);
            bolumSec.setText(bolumAdapter.getItem(secPosition), false);
            BolumSecildi(secPosition);
        }

        public void BolumSecildi(int position) {
            aktifBolumAd = bolumAdapter.getItem(position);
            if (aktifSezon != null && !OrtakAlan.StringIsNUllOrEmpty(aktifBolumAd)) {
                String[] bolumParca = aktifBolumAd.split("\\s+");
                Bolum b = aktifSezon.BolumBul(bolumParca[0]);
                if (b != null) {
                    bolumAciklama.setText(b.tmdbAciklamaBul());
                    return;
                }
            }
            bolumAciklama.setText(String.format("%s%s", aktifSezonAd, aktifBolumAd));
        }
    }
}
