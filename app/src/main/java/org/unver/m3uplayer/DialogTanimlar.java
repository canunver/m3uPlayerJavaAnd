package org.unver.m3uplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TVInfoAdapter extends ArrayAdapter<TVInfo> {
    private int selectedItemPosition;

    public TVInfoAdapter(@NonNull Context context, int resource, @NonNull List<TVInfo> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        if (position == selectedItemPosition) {
            view.setBackgroundColor(Color.BLUE);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }
        return view;
    }

    public void setSelectedItemPosition(int position) {
        selectedItemPosition = position;
        notifyDataSetChanged(); // Değişikliği bildir
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }
}

public class DialogTanimlar {
    private static AlertDialog alertDialogTurSec;
    private static M3UBilgi arananM3U;

    public static AlertDialog ParolaAl(MainActivity mainActivity) {
        // Parola girişi için AlertDialog oluşturma
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Parola Girin");

// Parola girişi için EditText oluşturma
        final EditText passwordInput = new EditText(mainActivity);
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordInput);

// Tamam ve İptal düğmelerini ekleyin
        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = passwordInput.getText().toString();
                // Parola girişi tamamlandı, yapılacak işlemleri burada gerçekleştirin
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    public static AlertDialog TurAl(MainActivity mainActivity, ArrayList<String> turDizisi, TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        // set title
        builder.setTitle(R.string.TurSec);

        String[] isimlerDizisi = new String[turDizisi.size()];
        boolean[] secili = new boolean[turDizisi.size()];

        builder.setMultiChoiceItems(turDizisi.toArray(isimlerDizisi), secili, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                secili[i] = b;
                SecilileriYaz(secili, isimlerDizisi, textView);
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialogTurSec.dismiss();
            }
        });

        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0; j < secili.length; j++) {
                    secili[j] = false;
                }
                SecilileriYaz(secili, isimlerDizisi, textView);
                ((AlertDialog) dialogInterface).getListView().clearChoices();
                ((AlertDialog) dialogInterface).getListView().requestLayout();
            }
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

    public static List<TVInfo> tmdbSecimList = new ArrayList<>();
    public static TVInfoAdapter tvInfoArrayAdapter;
    public static AlertDialog tmdbAldialog;
    private static EditText tmdbAraText;

    public static void TMDBDialogOl(MainActivity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tmdblayout, null);

        tmdbAraText = dialogView.findViewById(R.id.tmdbAraText);
        Button tmdbAraButton = dialogView.findViewById(R.id.tmdbAraButton);
        ListView tmdbListeView = dialogView.findViewById(R.id.tmdbListeView);
        tmdbListeView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        tvInfoArrayAdapter = new TVInfoAdapter(mainActivity, android.R.layout.simple_list_item_1, tmdbSecimList);
        tmdbListeView.setAdapter(tvInfoArrayAdapter);

        tmdbListeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvInfoArrayAdapter.setSelectedItemPosition(position);
                tmdbListeView.setSelection(position);
            }
        });

        tmdbAraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmdbAraButton.setEnabled(false);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        TVResponse tr = new InternettenOku().getTmdbInfo(arananM3U.TMDBTur(), tmdbAraText.getText().toString());
                        if (tr != null && tr.results != null && tr.results.length > 0) {
                            tmdbSecimList.clear();
                            for (TVInfo a : tr.results) {
                                tmdbSecimList.add(a);
                            }
                            this.DegerAta(true);
                        }
                    } catch (Exception ex) {
                        Log.d("M3UVeri", ex.getMessage());
                    }
                    this.DegerAta(false);
                });
            }

            private void DegerAta(boolean veriGeldi) {
                mainActivity.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (veriGeldi) {
                            tvInfoArrayAdapter.notifyDataSetChanged();
                            tvInfoArrayAdapter.setSelectedItemPosition(-1);
                        }
                        tmdbAraButton.setEnabled(true);
                    }
                }, 50);
            }
        });

        builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String hata;
                        int secili = tvInfoArrayAdapter.getSelectedItemPosition();
                        if (secili != -1) {
                            TVInfo ti = tvInfoArrayAdapter.getItem(secili);
                            if (ti != null) {
                                ti.type = M3UVeri.SiraBul(arananM3U.Tur);
                                arananM3U.tmdbId = ti.id;
                                arananM3U.Yaz(M3UVeri.db);
                                Log.d("Cek", "arananM3U.tmdbId: " + arananM3U.tmdbId + ", " + "ti.tostr:" + ti.toString());
                                M3UVeri.tumTMDBler.put(ti.anahtarBul(), ti);
                                ti.Yaz(M3UVeri.db);
                                hata = "";
                            } else
                                hata = "Seçilen nesne alınamadı!";
                        } else
                            hata = "Seçilen nesne bulunamadı!";
                        if (!ProgSettings.StringIsNUllOrEmpty(hata))
                            Toast.makeText(mainActivity, hata, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // İptal düğmesine basılınca yapılacak işlemler
                    }
                });

        tmdbAldialog = builder.create();
    }

    public static void tmdbDialogGoster(M3UBilgi m3u) {
        arananM3U = m3u;
        tvInfoArrayAdapter.setSelectedItemPosition(-1);
        if (m3u.Tur == M3UBilgi.M3UTur.seri)
            tmdbAraText.setText(m3u.seriAd);
        else
            tmdbAraText.setText(m3u.tvgName);
        tmdbAldialog.show();
    }
}
