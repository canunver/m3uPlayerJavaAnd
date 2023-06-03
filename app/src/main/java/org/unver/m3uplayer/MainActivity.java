package org.unver.m3uplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {
    ArrayList<M3UBilgi> kanalListe = new ArrayList<>();
    AutoCompleteTextView actv;
    int say = 0;
    private TextView sayac;
    AutoCompleteTextView grupSec;
    private RecyclerView recyclerView;
    private KanalAdapter kanalAdapter;
    public M3UBilgi.M3UTur aktifTur = M3UBilgi.M3UTur.tv;
    public String aktifGrupAd = "-";
    private M3UFiltre filtre = new M3UFiltre(M3UBilgi.M3UTur.tv, "", "", false);
    private EditText filtreAlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IzinAl();
    }

    private void UygulamayaBasla() {
        filtreAlan =(EditText) findViewById(R.id.filtreAd);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        kanalAdapter = new KanalAdapter(this, kanalListe);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(kanalAdapter);
        setupPagination();

        String[] turListesi = getResources().getStringArray(R.array.turListesi);
        ArrayAdapter<String> aaTur = new ArrayAdapter<String>(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, turListesi);
        TextInputLayout tilTur = findViewById(R.id.turSecCont);
        sayac = findViewById(R.id.filtreAd);

        actv = findViewById(R.id.turSec);
        grupSec = findViewById(R.id.grupSec);
        actv.setAdapter(aaTur);

        actv.setText(aaTur.getItem(0), false);
        OkuBakayim();

        TurSecildi(0);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TurSecildi(position);
            }
        });

        grupSec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GrupSecildi(position);
            }
        });
    }

    boolean isLoading = false;

    private void setupPagination() {
        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == kanalListe.size() - 1) {
                            if (!isLoading) {
                                isLoading = true;
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Yukle(false);
                                        isLoading = false;
                                    }
                                }, 50);
                            }
                        }
                    }
                }
        );
    }

    private static final int REQUEST_STORAGE_PERMISSION = 2;

    private void IzinAl() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted, request it at runtime
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    REQUEST_STORAGE_PERMISSION);
//        } else {
        // Permission is already granted, proceed with file operations
        UygulamayaBasla();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, proceed with file operations
                UygulamayaBasla();
            } else {
                // Permission is denied, handle accordingly (e.g., show a message or disable certain functionality)
                Toast.makeText(this, "Storage permission denied, program colud not store m3u file to storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    ArrayAdapter<String> grupAdapter;

    public void TurSecildi(int position) {
        aktifTur = position == 2 ? M3UBilgi.M3UTur.seri : (position == 1 ? M3UBilgi.M3UTur.film : M3UBilgi.M3UTur.tv);
        ArrayList<String> s = new ArrayList<String>();
        ArrayList<M3UGrup> grup = GrupKodBul(position);

        for (M3UGrup item : grup) {
            if(item.FiltreyeUygunMu(tumM3Ular, filtre))
                s.add(item.grupAdi);
        }
        if(s.isEmpty()) s.add("-");
        grupAdapter = new ArrayAdapter<String>(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, s);
        grupSec.setAdapter(grupAdapter);
        grupSec.setText(grupAdapter.getItem(0), false);
        GrupSecildi(0);
    }

    public void GrupSecildi(int position) {
        aktifGrupAd = grupAdapter.getItem(position);
        Yukle(true);
    }

    private void Yukle(boolean ilk) {
        M3UGrup bulunanGrup = GrupBul(GrupDegiskenBul());
        if (ilk)
            kanalListe.clear();
        int basla = kanalListe.size();
        if (bulunanGrup != null) {
            int kanalSay = 0;
            int eklenenSay = 0;
            for (String kanalId : bulunanGrup.kanallar) {
                M3UBilgi m3u = tumM3Ular.get(kanalId);
                if(m3u.FiltreUygunMu(filtre)) {
                    kanalSay++;
                    if (kanalSay <= basla) continue;
                    if (eklenenSay++ > 20) break;
                    kanalListe.add(m3u);
                }
            }
        }
        if (ilk)
            recyclerView.scrollToPosition(0);
        kanalAdapter.veriDegisti();
    }

    private M3UGrup GrupBul(ArrayList<M3UGrup> grupListesi) {
        for (M3UGrup g : grupListesi) {
            if (g.grupAdi == aktifGrupAd)
                return g;
        }
        return null;
    }

    private ArrayList<M3UGrup> GrupDegiskenBul() {
        if (aktifTur == M3UBilgi.M3UTur.film)
            return filmGruplari;
        else if (aktifTur == M3UBilgi.M3UTur.seri)
            return seriGruplari;
        else
            return tvGruplari;
    }

    void OkuBakayim() {
        //CekBakalim();
        M3UVeri dbHelper = new M3UVeri(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(M3UVeri.TABLE_M3U, null, null, null, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int IDIndex = cursor.getColumnIndex("ID");
                    int tvgIdIndex = cursor.getColumnIndex("tvgId");
                    int tvgNameIndex = cursor.getColumnIndex("tvgName");
                    int tvgLogoIndex = cursor.getColumnIndex("tvgLogo");
                    int groupTitleIndex = cursor.getColumnIndex("groupTitle");
                    int urlAdresIndex = cursor.getColumnIndex("urlAdres");
                    int eklemeTarihIndex = cursor.getColumnIndex("eklemeTarih");
                    int gizliIndex = cursor.getColumnIndex("gizli");
                    int adultIndex = cursor.getColumnIndex("adult");
                    int tmdbIdIndex = cursor.getColumnIndex("tmdbId");

                    do {
                        String ID = cursor.getString(IDIndex);
                        String tvgId = cursor.getString(tvgIdIndex);
                        String tvgName = cursor.getString(tvgNameIndex);
                        String tvgLogo = cursor.getString(tvgLogoIndex);
                        String groupTitle = cursor.getString(groupTitleIndex);
                        String urlAdres = cursor.getString(urlAdresIndex);
                        String eklemeTarih = cursor.getString(eklemeTarihIndex);
                        int gizli = cursor.getInt(gizliIndex);
                        int adult = cursor.getInt(adultIndex);
                        int tmdbId = cursor.getInt(tmdbIdIndex);

                        M3UBilgi m3u = new M3UBilgi(ID, tvgId, tvgName,
                                tvgLogo, groupTitle, urlAdres,
                                eklemeTarih, gizli, adult, tmdbId);
                        GruplaraIsle(m3u, true);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
    }

    private M3UGrup GrupBulYoksaEkle(ArrayList<M3UGrup> anaGrup, String groupTitle, boolean gelenGrup, boolean yoksaEkle) {
        for (M3UGrup item : anaGrup
        ) {
            if (item.grupAdi.equalsIgnoreCase(groupTitle))
                return item;
        }
        M3UGrup yeni;
        if (yoksaEkle) {
            yeni = new M3UGrup(groupTitle, gelenGrup);
            anaGrup.add(yeni);
        } else
            yeni = null;
        return yeni;
    }

    Hashtable<String, M3UBilgi> tumM3Ular = new Hashtable<>();
    public ArrayList<M3UGrup> tvGruplari = new ArrayList<>();
    public ArrayList<M3UGrup> filmGruplari = new ArrayList<>();
    public ArrayList<M3UGrup> seriGruplari = new ArrayList<>();
    public Hashtable<String, String> tumSerilerAd = new Hashtable<>();

    private ArrayList<M3UGrup> GrupKodBul(int position) {
        if (position == 0) return tvGruplari;
        else if (position == 1) return filmGruplari;
        else return seriGruplari;
    }


    private void GrupaEkle(ArrayList<M3UGrup> anaGrup, M3UBilgi m3u, boolean gelenGrup) {
        M3UGrup grp = GrupBulYoksaEkle(anaGrup, m3u.groupTitle, gelenGrup, true);
        grp.kanallar.add(m3u.ID);
    }

    public void GruplaraIsle(M3UBilgi m3u, boolean gelenGrup) {
        tumM3Ular.put(m3u.ID, m3u);
        if (m3u.Tur == M3UBilgi.M3UTur.tv) {
            GrupaEkle(tvGruplari, m3u, gelenGrup);
        } else if (m3u.Tur == M3UBilgi.M3UTur.film) {
            GrupaEkle(filmGruplari, m3u, gelenGrup);
        } else if (m3u.Tur == M3UBilgi.M3UTur.seri) {
            String m3uID;
            M3UBilgi seri;
            if (!tumSerilerAd.containsKey(m3u.seriAd)) {
                GrupaEkle(seriGruplari, m3u, gelenGrup);
                m3uID = m3u.ID;
                tumSerilerAd.put(m3u.seriAd, m3u.ID);
                seri = m3u;
            } else {
                m3uID = tumSerilerAd.get(m3u.seriAd);
                seri = tumM3Ular.get(m3uID);
            }
            Sezon sezon = SezonBulYoksaEkle(seri, m3u.sezon);
            sezon.bolumler.add(new Bolum(m3u.ID, m3u.bolum));
        }
    }

    public Sezon SezonBulYoksaEkle(M3UBilgi seri, String sezonAd) {
        for (Sezon item : seri.seriSezonlari) {
            if (item.sezonAd.equalsIgnoreCase(sezonAd))
                return item;
        }
        Sezon s = new Sezon(sezonAd);
        seri.seriSezonlari.add(s);
        return s;
    }

//    void SaklaBakayim()
//    {
//        Ekle(tumM3Ular, "#EXTINF:-1 tvg-id=\"TRT1.tr\" tvg-name=\"TR: TRT1 UHD\" tvg-logo=\"https://i.ibb.co/w75gVXp/trt1.png\" group-title=\"Ulusal Kanallar\",TR: TRT1 UHD", "http://panel.atlaspremium11.com:8080/futboloyuncu19341/2yegQAhncz/839");
//        Ekle(tumM3Ular, "#EXTINF:-1 tvg-id=\"TRT1.tr\" tvg-name=\"TR: TRT1 HD\" tvg-logo=\"https://i.ibb.co/w75gVXp/trt1.png\" group-title=\"Ulusal Kanallar\",TR: TRT1 HD","http://panel.atlaspremium11.com:8080/futboloyuncu19341/2yegQAhncz/125");
//        Ekle(tumM3Ular, "#EXTINF:-1 tvg-id=\"\" tvg-name=\"Yüzüklerin Efendisi: Yüzük Kardeşliği (2001)\" tvg-logo=\"http://image.tmdb.org/t/p/w600_and_h900_bestv2/e2QqbLA7BnuYKdYkmFeuVKZlurP.jpg\" group-title=\"YABANCI FİLMLER\",Yüzüklerin Efendisi: Yüzük Kardeşliği (2001)","http://panel.atlaspremium11.com:8080/movie/futboloyuncu19341/2yegQAhncz/51653.mkv");
//        Ekle(tumM3Ular, "#EXTINF:-1 tvg-id=\"\" tvg-name=\"The Wizards Return: Alex vs. Alex (2013)\" tvg-logo=\"http://image.tmdb.org/t/p/w600_and_h900_bestv2/iWL88DymJrzO6hYI1ykUeUohqef.jpg\" group-title=\"YABANCI FİLMLER\",The Wizards Return: Alex vs. Alex (2013)","http://panel.atlaspremium11.com:8080/movie/futboloyuncu19341/2yegQAhncz/51656.mkv");
//        Ekle(tumM3Ular, "#EXTINF:-1 tvg-id=\"\" tvg-name=\"Marvel's Daredevil S01 E01\" tvg-logo=\"http://image.tmdb.org/t/p/w600_and_h900_bestv2/aMXqdeXjvlRvvwScqtdlDyT75Qi.jpg\" group-title=\"ESKİ YABANCI DİZİLER\",Marvel's Daredevil S01 E01","http://panel.atlaspremium11.com:8080/series/futboloyuncu19341/2yegQAhncz/45066.mkv");
//        Ekle(tumM3Ular, "#EXTINF:-1 tvg-id=\"\" tvg-name=\"Marvel's Daredevil S01 E02\" tvg-logo=\"http://image.tmdb.org/t/p/w600_and_h900_bestv2/u6qLlpPbeNnOwzS7xJhIEmIT7Fq.jpg\" group-title=\"ESKİ YABANCI DİZİLER\",Marvel's Daredevil S01 E02","http://panel.atlaspremium11.com:8080/series/futboloyuncu19341/2yegQAhncz/45067.mkv");
//        Ekle(tumM3Ular, "#EXTINF:-1 tvg-id=\"\" tvg-name=\"Marvel's Daredevil S02 E01\" tvg-logo=\"http://image.tmdb.org/t/p/w600_and_h900_bestv2/oiDSkY2h8OGeitwHC0diCy4VllX.jpg\" group-title=\"ESKİ YABANCI DİZİLER\",Marvel's Daredevil S02 E01","http://panel.atlaspremium11.com:8080/series/futboloyuncu19341/2yegQAhncz/45079.mkv");
//        Ekle(tumM3Ular, "#EXTINF:-1 tvg-id=\"\" tvg-name=\"Marvel's Daredevil S02 E02\" tvg-logo=\"http://image.tmdb.org/t/p/w600_and_h900_bestv2/xe3jmTRJF1UyW2V4lXlN3hEfliC.jpg\" group-title=\"ESKİ YABANCI DİZİLER\",Marvel's Daredevil S02 E02","http://panel.atlaspremium11.com:8080/series/futboloyuncu19341/2yegQAhncz/45080.mkv");
//
//        M3UVeri dbHelper = new M3UVeri(getApplicationContext());
//
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        for (Map.Entry<String, M3UBilgi> d: tumM3Ular.entrySet()) {
//            d.getValue().Yaz(db);
//        }
//    }


    public void CekBakalim() {
//        try {
//            M3UVeri dbHelper = new M3UVeri(getApplicationContext());
//            SQLiteDatabase db = dbHelper.getWritableDatabase();
//            new InternettenOku().performNetworkOperation(this, db, "A");
//        } catch (Exception e) {
//            Log.d("Hata", e.getMessage());
//        }
    }

    public void btnAraClicked(View view) {
        filtre.filtre = filtreAlan.getText().toString();
        TurSecildi(SiraBul(aktifTur));
    }

    private int SiraBul(M3UBilgi.M3UTur aktifTur) {
        if(aktifTur== M3UBilgi.M3UTur.film) return 1;
        if(aktifTur== M3UBilgi.M3UTur.seri) return 2;
        return 0;
    }
}