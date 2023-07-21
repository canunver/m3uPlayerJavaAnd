package org.unver.m3uplayer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DialogTanimlar {
    private static AlertDialog alertDialogTurSec;
    private static M3UBilgi arananM3U;
    private static AlertDialog parolaAlDialog;

    public static AlertDialog ParolaAl(MainActivity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.parolalayout, null);

        EditText txtParola = dialogView.findViewById(R.id.txtParola);
        EditText txtParolaYeni1 = dialogView.findViewById(R.id.txtParolaYeni1);
        EditText txtParolaYeni2 = dialogView.findViewById(R.id.txtParolaYeni2);
        View blgYeniParola = dialogView.findViewById(R.id.blgYeniParola);
        Button parolaIptal = dialogView.findViewById(R.id.parolaIptal);
        Button parolaDegistir = dialogView.findViewById(R.id.parolaDegistir);
        Button parolaTamam = dialogView.findViewById(R.id.parolaTamam);

        builder.setView(dialogView);
        parolaAlDialog = builder.create();
        parolaIptal.setOnClickListener(v -> parolaAlDialog.cancel());

        parolaTamam.setOnClickListener(v -> {
            if (blgYeniParola.getVisibility() == View.GONE) {
                OrtakAlan.parolaGirildi(txtParola.getText().toString());
                if (OrtakAlan.parolaVar)
                    parolaAlDialog.cancel();
                else
                    Toast.makeText(parolaAlDialog.getContext(), R.string.hatali_parola, Toast.LENGTH_SHORT).show();
            } else {
                String parolaYeni1 = txtParolaYeni1.getText().toString();
                String parolaYeni2 = txtParolaYeni2.getText().toString();
                String parolaEski = txtParola.getText().toString().trim();
                if (parolaYeni1.length() < 4) {
                    Toast.makeText(parolaAlDialog.getContext(), R.string.hata_parola_kisa, Toast.LENGTH_SHORT).show();
                } else if (parolaYeni1.indexOf(' ') > 0) {
                    Toast.makeText(parolaAlDialog.getContext(), R.string.hata_parola_bosluk, Toast.LENGTH_SHORT).show();
                } else if (!parolaYeni1.equals(parolaYeni2)) {
                    Toast.makeText(parolaAlDialog.getContext(), R.string.hata_parola_ayni_degil, Toast.LENGTH_SHORT).show();
                } else {
                    if (!OrtakAlan.parolaDogruMu(parolaEski)) {
                        Toast.makeText(parolaAlDialog.getContext(), R.string.hatali_parola, Toast.LENGTH_SHORT).show();
                    } else {
                        OrtakAlan.ParolaAta(parolaYeni1);
                        txtParola.setText("");
                        blgYeniParola.setVisibility(View.GONE);
                        parolaDegistir.setText(R.string.degistir);
                        Toast.makeText(parolaAlDialog.getContext(), R.string.parola_degisti, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        parolaDegistir.setOnClickListener(v -> {
            if (blgYeniParola.getVisibility() == View.GONE) {
                blgYeniParola.setVisibility(View.VISIBLE);
                parolaDegistir.setText(R.string.kapat);
            }
            else {
                blgYeniParola.setVisibility(View.GONE);
                parolaDegistir.setText(R.string.degistir);
            }
        });

        return parolaAlDialog;
    }

    public static AlertDialog TurAl(MainActivity mainActivity, ArrayList<String> turDizisi, TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        // set title
        builder.setTitle(R.string.TurSec);

        String[] isimlerDizisi = new String[turDizisi.size()];
        boolean[] secili = new boolean[turDizisi.size()];

        builder.setMultiChoiceItems(turDizisi.toArray(isimlerDizisi), secili, (dialogInterface, i, b) -> {
            secili[i] = b;
            SecilileriYaz(secili, isimlerDizisi, textView);
        });

        builder.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> alertDialogTurSec.dismiss());

        builder.setNeutralButton("Clear All", (dialogInterface, i) -> {
            Arrays.fill(secili, false);
            SecilileriYaz(secili, isimlerDizisi, textView);
            ((AlertDialog) dialogInterface).getListView().clearChoices();
            ((AlertDialog) dialogInterface).getListView().requestLayout();
        });
        // set dialog non cancelable
        builder.setCancelable(false);
        alertDialogTurSec = builder.create();
        alertDialogTurSec.setCancelable(false);
        return alertDialogTurSec;
    }

    private static void SecilileriYaz(boolean[] secili, String[] isimlerDizisi, TextView textView) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean kayitVar = false;
        for (int j = 0; j < secili.length; j++) {
            if (secili[j]) {
                if (kayitVar)
                    stringBuilder.append(", ");
                else
                    kayitVar = true;
                stringBuilder.append(isimlerDizisi[j]);
            }
        }
        // set text on textView
        textView.setText(stringBuilder.toString());
    }

    public static List<KodAd> tmdbSecimList = new ArrayList<>();
    public static KodAdAdapter tvInfoArrayAdapter;
    public static AlertDialog tmdbAldialog;
    @SuppressLint("StaticFieldLeak")
    private static EditText tmdbAraText;

    public static void TMDBDialogOl(MainActivity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tmdblayout, null);

        tmdbAraText = dialogView.findViewById(R.id.tmdbAraText);
        Button tmdbAraButton = dialogView.findViewById(R.id.tmdbAraButton);
        ListView tmdbListeView = dialogView.findViewById(R.id.tmdbListeView);
        tmdbListeView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        tvInfoArrayAdapter = new KodAdAdapter(mainActivity, android.R.layout.simple_list_item_1, tmdbSecimList, false);
        tmdbListeView.setAdapter(tvInfoArrayAdapter);

        tmdbListeView.setOnItemClickListener((parent, view, position, id) -> {
            tvInfoArrayAdapter.setSelectedItemPosition(position);
            //tmdbListeView.setSelection(position);
        });

        tmdbAraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(OrtakAlan.StringIsNUllOrEmpty(OrtakAlan.tmdb_erisim_anahtar))
                {
                    Toast.makeText(v.getContext(), R.string.TMDBKur, Toast.LENGTH_SHORT).show();
                    return;
                }

                tmdbAraButton.setEnabled(false);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        TVResponse tr = InternettenOku.getTmdbInfo(arananM3U.TMDBTur(), tmdbAraText.getText().toString());
                        if (tr != null && tr.results != null && tr.results.length > 0) {
                            tmdbSecimList.clear();
                            for (TVInfo a : tr.results) {
                                tmdbSecimList.add(new KodAd(a.anahtarBul(), a.toString(), a));
                            }
                            this.DegerAta(true);
                        }
                    } catch (Exception ex) {
                        Log.e("M3UVeri", ex.getMessage());
                    }
                    this.DegerAta(false);
                });
            }

            private void DegerAta(boolean veriGeldi) {
                mainActivity.handler.postDelayed(() -> {
                    if (veriGeldi) {
                        tvInfoArrayAdapter.notifyDataSetChanged();
                        tvInfoArrayAdapter.setSelectedItemPosition(-1);
                    }
                    tmdbAraButton.setEnabled(true);
                }, 50);
            }
        });

        builder.setView(dialogView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String hata;
                    int secili = tvInfoArrayAdapter.getSelectedItemPosition();
                    if (secili != -1) {
                        KodAd ka = tvInfoArrayAdapter.getItem(secili);
                        if (ka != null) {
                            TVInfo ti = (TVInfo) ka.o;
                            ti.type = M3UVeri.SiraBul(arananM3U.Tur);
                            arananM3U.tmdbId = ti.id;
                            arananM3U.Yaz(M3UVeri.db);
                            M3UVeri.tumTMDBListesi.put(ti.anahtarBul(), ti);
                            ti.Yaz(M3UVeri.db);
                            aktifAdapter.notifyItemChanged(seciliItemPos);
                            hata = "";
                        } else
                            hata = "Seçilen nesne alınamadı!";
                    } else
                        hata = "Seçilen nesne bulunamadı!";
                    if (!OrtakAlan.StringIsNUllOrEmpty(hata))
                        Toast.makeText(mainActivity, hata, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    // İptal düğmesine basılınca yapılacak işlemler
                });

        tmdbAldialog = builder.create();
    }

    private static int seciliItemPos;
    private static YayinListesiAdapter aktifAdapter;

    public static void tmdbDialogGoster(M3UBilgi m3u, YayinListesiAdapter kanalAdapter, int pos) {
        aktifAdapter = kanalAdapter;
        seciliItemPos = pos;
        arananM3U = m3u;
        tvInfoArrayAdapter.setSelectedItemPosition(-1);
        if (m3u.Tur == M3UBilgi.M3UTur.seri)
            tmdbAraText.setText(m3u.seriAd);
        else
            tmdbAraText.setText(m3u.tvgName);
        tmdbAldialog.show();
    }

    public static void onayAl(int titleId, int messageId, final OnayDialogGeriBildirim geriBildirim) {
        AlertDialog.Builder builder = new AlertDialog.Builder(M3UVeri.mainActivity);
        builder.setTitle(M3UVeri.mainActivity.getString(titleId));
        builder.setMessage(M3UVeri.mainActivity.getString(messageId));
        builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            if (geriBildirim != null) {
                geriBildirim.onDialogTusaBasildi(true);
            }
        });

        builder.setNegativeButton(android.R.string.no, (dialog, which) -> {
            if (geriBildirim != null) {
                geriBildirim.onDialogTusaBasildi(false);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}


