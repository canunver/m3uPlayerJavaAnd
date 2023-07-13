package org.unver.m3uplayer;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;
import org.videolan.libvlc.media.VideoView;

import java.util.ArrayList;

public class YayinFragment extends Fragment {
    private boolean baslangictan;
    private ConstraintLayout anaYerlesim;
    private ConstraintLayout aramaBolmesi;
    private ConstraintLayout oynatmaBolmesi;
    private ConstraintSet landscapeConstraintSet;
    private ConstraintSet portraitConstraintSet;
    private VideoView mVideoView;
    private MediaPlayer mMediaPlayer;
    Media mediaItem;
    public M3UFiltre filtre = new M3UFiltre();
    private RecyclerView recyclerView;
    private YayinListesiAdapter kanalAdapter;
    public String aktifGrupAd = "-";
    ArrayList<M3UBilgi> kanalListe = new ArrayList<>();
    AutoCompleteTextView grupSec;
    private View currView;
    private LibVLC libVLC;
    private boolean buyuklukAyarlandi = false;
    private IVLCVout vout = null;
    private CustomMediaController mediaController;
    private SurfaceHolder holder;
    MainActivity mainActivity;
    boolean isLoading = false;
    ArrayAdapter<String> grupAdapter;
    private String otoAc = null;
    private M3UBilgi.M3UTur otoTur;
    private String otoSezon = null;
    private String otoBolum = null;
    private boolean pauseBaslat;
    private boolean tamEkranBaslat;
    private boolean ilkPlay;
    private ImageView imgOp;

    public YayinFragment(MainActivity mainActivity) {
        this.baslangictan = true;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        createPlayer();
        if (baslangictan && !OrtakAlan.StringIsNUllOrEmpty(otoAc)) {
            OynatBakalim(M3UVeri.tumM3Ular.getOrDefault(otoAc, null), otoSezon, otoBolum, otoTur != M3UBilgi.M3UTur.tv, OrtakAlan.tamEkranBaslat);
            otoAc = null;
        }
        baslangictan = false;
    }

    private void createPlayer() {
        ArrayList<String> options = new ArrayList<String>();
        options.add("--no-drop-late-frames");
        options.add("--no-skip-frames");
        options.add("--rtsp-tcp");
        options.add("--aout=opensles");
        options.add("--audio-time-stretch"); // time stretching
        options.add("-vvv"); // verbosity

        libVLC = new LibVLC(mainActivity, options);
        holder.setKeepScreenOn(true);
        mMediaPlayer = new MediaPlayer(libVLC);
        vout = mMediaPlayer.getVLCVout();
        vout.setVideoView(mVideoView);
        vout.attachViews();

        mediaController = new CustomMediaController(this, oynatmaBolmesi, mMediaPlayer);
        buyuklukAyarlandi = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currView = inflater.inflate(R.layout.fragment_player, container, false);

        anaYerlesim = currView.findViewById(R.id.anaYerlesim);
        anaYerlesim.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                OynatmaBolgesiBuyuklukAyarla();
            }
        });
        mVideoView = currView.findViewById(R.id.playerView);
        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaController.GorunumDegistir();
            }
        });
        aramaBolmesi = currView.findViewById(R.id.aramaBolmesi);
        oynatmaBolmesi = currView.findViewById(R.id.oynatmaBolmesi);
        landscapeConstraintSet = new ConstraintSet();
        portraitConstraintSet = new ConstraintSet();

        landscapeConstraintSet.clone(anaYerlesim);
        portraitConstraintSet.clone(anaYerlesim);

        int sw = getResources().getConfiguration().screenWidthDp;
        int sh = getResources().getConfiguration().screenHeightDp;
        int yw = Math.max(sw, sh);

        yw = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, yw, getResources().getDisplayMetrics());

        int ow = (int) (yw * 0.65f);

        try {
            landscapeConstraintSet.constrainWidth(R.id.aramaBolmesi, ow);
            landscapeConstraintSet.constrainHeight(R.id.aramaBolmesi, 0);
            landscapeConstraintSet.connect(R.id.aramaBolmesi, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            landscapeConstraintSet.connect(R.id.aramaBolmesi, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            landscapeConstraintSet.connect(R.id.aramaBolmesi, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);

            landscapeConstraintSet.constrainWidth(R.id.oynatmaBolmesi, 0);
            landscapeConstraintSet.constrainHeight(R.id.oynatmaBolmesi, 0);
            landscapeConstraintSet.connect(R.id.oynatmaBolmesi, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            landscapeConstraintSet.connect(R.id.oynatmaBolmesi, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            landscapeConstraintSet.connect(R.id.oynatmaBolmesi, ConstraintSet.START, R.id.aramaBolmesi, ConstraintSet.END, 0);
            landscapeConstraintSet.connect(R.id.oynatmaBolmesi, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        } catch (Exception ex) {
            Log.d("Exc", ex.getMessage());
        }

        recyclerView = (RecyclerView) currView.findViewById(R.id.recyclerView);
        kanalAdapter = new YayinListesiAdapter(this, kanalListe);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(kanalAdapter);

        imgOp = currView.findViewById(R.id.imgOp);
        grupSec = currView.findViewById(R.id.grupSec);

        setupPagination();

        grupSec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GrupSecildi(position, false);
            }
        });
        mVideoView = currView.findViewById(R.id.playerView);
        holder = mVideoView.getHolder();
        YonlendirmeAyarla();
        int aktifTurPos = 0;
        if (OrtakAlan.son_tv_kanalini_oynatarak_basla && OrtakAlan.StringIsNUllOrEmpty(OrtakAlan.sonTVProgramID))
            aktifTurPos = 0;
        else
            aktifTurPos = M3UVeri.SiraBul(OrtakAlan.sonM3UTur);
        if (aktifTurPos != 0)
            mainActivity.setAktifTur(aktifTurPos);
        TurSecildi(aktifTurPos, true);
        ArkaPlanIslemleri.baslat(mainActivity);
        return currView;
    }

    public void YonlendirmeAyarla() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            landscapeConstraintSet.applyTo(anaYerlesim);
            if (mediaController != null && mediaController.tamEkran)
                aramaBolmesi.setVisibility(View.GONE);
        } else {
            portraitConstraintSet.applyTo(anaYerlesim);
        }
        anaYerlesim.requestLayout();
    }

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
                                        Yukle(false, false);
                                        isLoading = false;
                                    }
                                }, 50);
                            }
                        }
                    }
                }
        );
    }

    public void TurSecildi(int position, boolean acilistan) {
        mainActivity.aktifTur = M3UVeri.TurBul(position);
        String simdSecText = grupSec.getText().toString();
        Object[] donenler = GrupListesiOl(mainActivity, acilistan, filtre, simdSecText, 0, true, true, false, false);
        grupAdapter = (ArrayAdapter<String>) donenler[0];
        int yerInd = (int) donenler[1];
        ArrayList<String> strler = (ArrayList<String>) donenler[2];
        grupSec.setAdapter(grupAdapter);
        if (yerInd > -1) {
            //grupSec.clearListSelection();
            //Log.d("basla", "Tur seçildi grup say:" + grupAdapter.getCount() + " fil:" + grupAdapter.getFilter().toString());
            grupSec.setText(strler.get(yerInd), false);
            //grupSec.setSelection(0, 1);
            //grupAdapter.notifyDataSetChanged();
            GrupSecildi(yerInd, acilistan);
        }
    }

    public static Object[] GrupListesiOl(MainActivity mainActivity, boolean acilistan, M3UFiltre filtre, String simd, int hepsi0Kul1Inen2, boolean sadeceAd, boolean bosKalmasin, boolean ozelliklerOlsun, boolean gizlilerOlsun) {
        ArrayList<String> s = new ArrayList<String>();
        ArrayList<M3UGrup> grupTutan = M3UVeri.GrupKodBul(M3UVeri.SiraBul(mainActivity.aktifTur));

        int yerInd = -1;
        String sondaki = null;
        if (acilistan) {
            if (OrtakAlan.son_tv_kanalini_oynatarak_basla) {
                if (!OrtakAlan.StringIsNUllOrEmpty(OrtakAlan.sonTVGrup))
                    sondaki = OrtakAlan.sonTVGrup;
            }
            if (sondaki == null && !OrtakAlan.StringIsNUllOrEmpty(OrtakAlan.sonGrup))
                sondaki = OrtakAlan.sonGrup;
            if (sondaki != null)
                simd = sondaki;
        }
        for (M3UGrup item : grupTutan) {
            if (item.filtreyeUygunMu(M3UVeri.tumM3Ular, filtre) && item.grupTurUygunMu(hepsi0Kul1Inen2) && item.gizliYetiskinDegilse(gizlilerOlsun)) {
                s.add(item.grupAdiBul(ozelliklerOlsun, OrtakAlan.GizliBul(mainActivity), OrtakAlan.YetiskinBul(mainActivity)));
                if (simd != null && item.grupAdi.equals(simd))
                    yerInd = s.size() - 1;
            }
        }
        if (bosKalmasin && s.isEmpty()) {
            s.add("-");
        }
        if (s.size() > 0 && yerInd == -1)
            yerInd = 0;

        Object[] donenler = new Object[3];
        donenler[0] = new ArrayAdapter<String>(mainActivity, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, s);
        donenler[1] = yerInd;
        donenler[2] = s;
        //Log.d("basla", "Tur seçildi gruplar bulundu:" + s.size());

        return donenler;
    }

    public void GrupSecildi(int position, boolean acilistan) {
        aktifGrupAd = grupAdapter.getItem(position);
        Yukle(true, acilistan);
        if (acilistan) {
            otoAc = null;
            otoTur = M3UBilgi.M3UTur.tv;
            otoSezon = null;
            otoBolum = null;
            if (OrtakAlan.son_tv_kanalini_oynatarak_basla) {
                if (!OrtakAlan.StringIsNUllOrEmpty(OrtakAlan.sonTVProgramID)) {
                    otoAc = OrtakAlan.sonTVProgramID;
                    otoTur = M3UBilgi.M3UTur.tv;
                }
            } else {
                otoAc = OrtakAlan.sonProgramID;
                otoTur = OrtakAlan.sonM3UTur;
            }
        }
    }

    private void Yukle(boolean ilk, boolean acilistan) {
        M3UGrup bulunanGrup = M3UVeri.GrupBul(mainActivity, M3UVeri.GrupDegiskenBul(mainActivity.aktifTur), aktifGrupAd, false);
        if (ilk)
            kanalListe.clear();
        String araProg = null;
        if (acilistan) {
            if (OrtakAlan.son_tv_kanalini_oynatarak_basla && !OrtakAlan.StringIsNUllOrEmpty(OrtakAlan.sonTVProgramID))
                araProg = OrtakAlan.sonTVProgramID;
            if (araProg == null)
                araProg = OrtakAlan.sonProgramID;
        }
        int basla = kanalListe.size();
        int scrollPos = 0;
        if (bulunanGrup != null) {
            int kanalSay = 0;
            int eklenenSay = 0;
            for (String kanalId : bulunanGrup.kanallar) {
                M3UBilgi m3u = M3UVeri.tumM3Ular.get(kanalId);
                if (m3u.FiltreUygunMu(filtre)) {
                    kanalSay++;
                    if (kanalSay <= basla) continue;
                    if (eklenenSay++ > 20) break;
                    kanalListe.add(m3u);
                    if (araProg != null && araProg.equals(m3u.ID))
                        scrollPos = eklenenSay - 1;
                }
            }
        }
        if (ilk)
            recyclerView.scrollToPosition(scrollPos);
        kanalAdapter.veriDegisti();
    }

    public void NesneSecildi(int islem, int pos, String sezon, String bolum) {
        M3UBilgi item = kanalListe.get(pos);
        if (islem == 1)
            OynatBakalim(item, sezon, bolum, false, false);
        else if (islem == 2) {
            DialogTanimlar.tmdbDialogGoster(item);
        } else if (islem == 3) {
            if (item.tmdbId > 0) {
                new InternettenOku().performNetworkOperationTMDBSeri(mainActivity, M3UVeri.db, item);
                Toast.makeText(mainActivity, R.string.bolum_cekme_basladi, Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(mainActivity, R.string.once_dizi_cek, Toast.LENGTH_SHORT).show();
        }
    }

    public void OynatmaBolgesiBuyuklukAyarla() {
        if (vout != null) {
            buyuklukAyarlandi = true;
            int width = oynatmaBolmesi.getWidth();
            int height = oynatmaBolmesi.getHeight();
            vout.setWindowSize(width, height);
            mediaController.BuyuklukAyarla();
        }
    }

    void OynatBakalim(M3UBilgi m3uBilgiGrup, String sezon, String bolum, boolean paused, boolean tamEkran) {
        if (!buyuklukAyarlandi) {
            OynatmaBolgesiBuyuklukAyarla();
        } else
            mMediaPlayer.stop();

        mediaController.m3uBilgiAyarla(m3uBilgiGrup, sezon, bolum);

        mediaItem = new Media(libVLC, Uri.parse(mediaController.m3uBilgiOynayan.urlAdres));
        mMediaPlayer.setMedia(mediaItem);
        ilkPlay = true;
        mMediaPlayer.play();
        pauseBaslat = paused;
        tamEkranBaslat = tamEkran;
        mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                if (event.type == MediaPlayer.Event.Opening) { //İlk oluşan event 258
                } else if (event.type == MediaPlayer.Event.PausableChanged) {//İkinci oluşan event 270
                    //Log.d("PlayerFragment", m3uBilgi.tvgName + "TimeChanged");
                } else if (event.type == MediaPlayer.Event.Playing) {//Üçüncü oluşan event 260
                    if (ilkPlay) {
                        mediaController.SesleriAyarla(mMediaPlayer.getTracks(Media.Track.Type.Audio));
                        //Media.Track[] subtitleTracks =
                        mediaController.AltyazilariAyarla(mMediaPlayer.getTracks(Media.Track.Type.Text));

                        mediaController.BilgiAyarla(pauseBaslat);
                        if (tamEkranBaslat)
                            mediaController.TamEkrandanYap();
                        pauseBaslat = false;
                        tamEkranBaslat = false;
                        ilkPlay = false;
                    }
                    //Log.d("PlayerFragment", m3uBilgi.tvgName + "Playing:" + ":"+mMediaPlayer.getTime() + "/" + mMediaPlayer.getLength());
                } else if (event.type == MediaPlayer.Event.ESAdded) {//Dördüncü oluşan event 276
                    //Log.d("PlayerFragment", m3uBilgi.tvgName + "TimeChanged");
                } else if (event.type == MediaPlayer.Event.ESSelected) {//Beşinci oluşan event 278
                    //Log.d("PlayerFragment", m3uBilgi.tvgName + "TimeChanged");
                } else if (event.type == MediaPlayer.Event.Vout) {//Altıncı oluşan event 274
                    //Log.d("PlayerFragment", m3uBilgi.tvgName + "TimeChanged");
                } else if (event.type == MediaPlayer.Event.TimeChanged) {
                    //Log.d("PlayerFragment", m3uBilgi.tvgName + "TimeChanged:" + event.getTimeChanged() + ":"+mMediaPlayer.getTime() + "/" + mMediaPlayer.getLength());
                    mediaController.ZamanAyarla(mMediaPlayer.getTime());
                } else if (event.type == MediaPlayer.Event.PositionChanged) {

                } else if (event.type == MediaPlayer.Event.Buffering) {

                } else if (event.type == MediaPlayer.Event.SeekableChanged) { //269
                    Log.d("PlayerFragment", mediaController.m3uBilgi.tvgName + "SeekableChanged:" + event.getSeekable());
                } else if (event.type == MediaPlayer.Event.LengthChanged) { //273

                } else if (event.type == MediaPlayer.Event.EndReached) { //269

                } else if (event.type == MediaPlayer.Event.ESDeleted) { //277

                } else if (event.type == MediaPlayer.Event.Stopped) { //262

                } else {
                    Log.d("PlayerFragment", mediaController.m3uBilgi.tvgName + "MediaPlayer.Event.type:" + event.type);
                }
            }
        });

        mediaController.show(8000);
    }

    private void releasePlayer() {
        if (libVLC == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.detachViews();
        libVLC.release();
        libVLC = null;
    }

    public void TamEkranDegistir(boolean tamEkran) {
        if (tamEkran) {
            aramaBolmesi.setVisibility(View.GONE);
            mainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            mainActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mainActivity.YatayYap();
        } else {
            mainActivity.GoruntulenenYap();
            aramaBolmesi.setVisibility(View.VISIBLE);
            mainActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    public boolean TamEkranMi() {
        if (mediaController != null)
            return mediaController.tamEkran;
        return false;
    }

    public void TamEkrandanCik() {
        mediaController.TamEkrandanCik();
    }

    public void TarihceyeEkle() {
        OrtakAlan.TarihceyeEkle(mainActivity.aktifTur, aktifGrupAd, mediaController.m3uBilgi.ID);
    }

    public void ZamaniYaz(M3UBilgi m3uBilgiOynayan, Bolum aktifBolum, int dakika) {
        if (aktifBolum != null) {
            for (String id : aktifBolum.idler) {
                M3UBilgi akt = M3UVeri.tumM3Ular.getOrDefault(id, null);
                if (akt != null) {
                    akt.seyredilenSure = dakika;
                    akt.Yaz(M3UVeri.db);
                }
            }
        } else if (m3uBilgiOynayan != null) {
            if (m3uBilgiOynayan.Tur != M3UBilgi.M3UTur.tv) {
                m3uBilgiOynayan.seyredilenSure = dakika;
                m3uBilgiOynayan.Yaz(M3UVeri.db);
            }
        }
    }

    public void InternettenCekmeIkon(int internettenCekiliyor) {
        try {
            if (internettenCekiliyor == 0)
                imgOp.setVisibility(View.GONE);
            else {
                if (internettenCekiliyor == 1)
                    imgOp.setImageResource(R.drawable.baseline_downloading_24);
                else
                    imgOp.setImageResource(R.drawable.baseline_local_movies_24);
                imgOp.setVisibility(View.VISIBLE);
            }
            //anaYerlesim.requestLayout();
        } catch (Exception ex) {
            Log.d("M3UVeri", ex.getMessage());
        }
    }
}