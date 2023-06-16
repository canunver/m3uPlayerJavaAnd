package org.unver.m3uplayer;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;
import org.videolan.libvlc.media.VideoView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the { @ l i n k PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {
    private ConstraintLayout anaYerlesim;
    private ConstraintLayout aramaBolmesi;
    private ConstraintLayout oynatmaBolmesi;
    private ConstraintSet landscapeConstraintSet;
    private ConstraintSet portraitConstraintSet;
    private VideoView mVideoView;
    private MediaPlayer mMediaPlayer;
    Media mediaItem;
    private M3UFiltre filtre = new M3UFiltre(M3UBilgi.M3UTur.tv, "", "", false);
    private RecyclerView recyclerView;
    private OynaticiAdapter kanalAdapter;
    public String aktifGrupAd = "-";
    ArrayList<M3UBilgi> kanalListe = new ArrayList<>();
    int say = 0;
    AutoCompleteTextView grupSec;

    private View currView;
    private LibVLC libVLC;
    private boolean buyuklukAyarlandi = false;
    private IVLCVout vout = null;
    //private ExtMediaController mediaController;
    private CustomMediaController mediaController;
    private SurfaceHolder holder;

    public PlayerFragment(MainActivity mainActivity) {
        // Required empty public constructor
        this.mainActivity = mainActivity;
        ;
    }

    MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        createPlayer();
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

        mediaController = new CustomMediaController(this, oynatmaBolmesi);
//        mediaController.setVLCMediaPlayer(mMediaPlayer);
//        mediaController.setAnchorView(mVideoView);

//        mVideoView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int left, int top, int right, int bottom,
//                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                Log.d("PlayerFragment", "mVideoView.addOnLayoutChangeListener:onLayoutChange");
//                // VideoView boyutu değiştiğinde MediaController'ı güncelle
////                mediaController.updateLayout();
//            }
//        });

        // Associate the media controller with the media player
//        mediaController.setMediaPlayer(mediaController);
//        mVideoView.setMediaController(mediaController);
        //mediaController.setAnchorView(mVideoView);
        buyuklukAyarlandi = false;
        //mediaController.setAutoHideTimeout(10000);
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
        // Inflate the layout for this fragment
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
        kanalAdapter = new OynaticiAdapter(this, kanalListe);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(kanalAdapter);

        grupSec = currView.findViewById(R.id.grupSec);

        setupPagination();

        grupSec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GrupSecildi(position);
            }
        });
        mVideoView = currView.findViewById(R.id.playerView);
        holder = mVideoView.getHolder();
        YonlendirmeAyarla();
        TurSecildi(0);
        return currView;
    }


    public void YonlendirmeAyarla() {
        int currentOrientation = getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("M3U", "landscapeConstraintSet");
            landscapeConstraintSet.applyTo(anaYerlesim);
            if (mediaController != null && mediaController.tamEkran)
                aramaBolmesi.setVisibility(View.GONE);
        } else {
            Log.d("M3U", "portraitConstraintSet");
            portraitConstraintSet.applyTo(anaYerlesim);
        }
        anaYerlesim.requestLayout();
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

    ArrayAdapter<String> grupAdapter;

    public void TurSecildi(int position) {
        mainActivity.aktifTur = position == 2 ? M3UBilgi.M3UTur.seri : (position == 1 ? M3UBilgi.M3UTur.film : M3UBilgi.M3UTur.tv);
        ArrayList<String> s = new ArrayList<String>();
        ArrayList<M3UGrup> grup = M3UVeri.GrupKodBul(position);

        int yerInd = -1;
        String simd = grupSec.getText().toString();
        for (M3UGrup item : grup) {
            if (item.FiltreyeUygunMu(M3UVeri.tumM3Ular, filtre)) {
                s.add(item.grupAdi);
                if (item.grupAdi.equals(simd))
                    yerInd = s.size() - 1;
            }
        }
        if (yerInd == -1) yerInd = 0;
        if (s.isEmpty()) s.add("-");
        grupAdapter = new ArrayAdapter<String>(mainActivity, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, s);
        grupSec.setAdapter(grupAdapter);
        grupSec.setText(grupAdapter.getItem(yerInd), false);
        GrupSecildi(yerInd);
    }

    public void GrupSecildi(int position) {
        aktifGrupAd = grupAdapter.getItem(position);
        Yukle(true);
    }

    private void Yukle(boolean ilk) {
        M3UGrup bulunanGrup = M3UVeri.GrupBul(M3UVeri.GrupDegiskenBul(mainActivity.aktifTur), aktifGrupAd);
        if (ilk)
            kanalListe.clear();
        int basla = kanalListe.size();
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
                }
            }
        }
        if (ilk)
            recyclerView.scrollToPosition(0);
        kanalAdapter.veriDegisti();
    }

    public void NesneSecildi(int pos) {
        //Log.d("RV", kanalListe.get(pos).urlAdres);
        OynatBakalim(kanalListe.get(pos));
    }

    public void OynatmaBolgesiBuyuklukAyarla() {
        if (vout != null) {
            buyuklukAyarlandi = true;
//            int width = mVideoView.getWidth();
//            int height = mVideoView.getHeight();
            int width = oynatmaBolmesi.getWidth();
            int height = oynatmaBolmesi.getHeight();
            Log.d("Buyukluk", "width:" + width + ";  height:" + height);
            vout.setWindowSize(width, height);
        }
    }

    void OynatBakalim(M3UBilgi m3uBilgi) {
        if (!buyuklukAyarlandi) {
            OynatmaBolgesiBuyuklukAyarla();
        } else
            mMediaPlayer.stop();

        mediaItem = new Media(libVLC, Uri.parse(m3uBilgi.urlAdres));
        mMediaPlayer.setMedia(mediaItem);
        mMediaPlayer.play();


        // Set the media controller to be visible
        mediaController.show(8000);
    }

    private void releasePlayer() {
        if (libVLC == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        //vout.removeCallback(this);
        vout.detachViews();
        libVLC.release();
        libVLC = null;
    }

    public void TamEkranDegistir(boolean tamEkran) {
        if (tamEkran) {
            //playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            aramaBolmesi.setVisibility(View.GONE);
            //actionBar.hide();
            mainActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mainActivity.YatayYap();
        } else {
            Log.d("M3U", "Tam Ekrandan Çıkış Tuşu");
            //playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            mainActivity.GoruntulenenYap();
            aramaBolmesi.setVisibility(View.VISIBLE);
            mainActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        //OynatmaBolgesiBuyuklukAyarla();
    }

}