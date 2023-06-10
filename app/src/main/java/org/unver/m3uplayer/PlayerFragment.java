package org.unver.m3uplayer;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputLayout;

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
    private androidx.media3.ui.PlayerView playerView;
    private ImageButton tamEkranButton;
    private ImageButton yenidenYukleButton;
    private boolean tamEkran;
    private M3UFiltre filtre = new M3UFiltre(M3UBilgi.M3UTur.tv, "", "", false);
    private EditText filtreAlan;
    private RecyclerView recyclerView;
    private OynaticiAdapter kanalAdapter;
    public M3UBilgi.M3UTur aktifTur = M3UBilgi.M3UTur.tv;
    public String aktifGrupAd = "-";
    ArrayList<M3UBilgi> kanalListe = new ArrayList<>();
    AutoCompleteTextView actv;
    int say = 0;
    AutoCompleteTextView grupSec;
    private InputMethodManager imm;
    private View currView;

    public PlayerFragment(MainActivity mainActivity) {
        // Required empty public constructor
        this.mainActivity = mainActivity;;

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static PlayerFragment newInstance(String param1, String param2) {
//        PlayerFragment fragment = new PlayerFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }
    MainActivity mainActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currView = inflater.inflate(R.layout.fragment_player, container, false);
        imm = (InputMethodManager) mainActivity.getSystemService(mainActivity.INPUT_METHOD_SERVICE);

        anaYerlesim = currView.findViewById(R.id.anaYerlesim);
        playerView = currView.findViewById(R.id.playerView);
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

        filtreAlan = (EditText) currView.findViewById(R.id.filtreAd);
        recyclerView = (RecyclerView) currView.findViewById(R.id.recyclerView);
        kanalAdapter = new OynaticiAdapter(this, kanalListe);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(kanalAdapter);

        String[] turListesi = getResources().getStringArray(R.array.turListesi);
        ArrayAdapter<String> aaTur = new ArrayAdapter<String>(mainActivity, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, turListesi);
        TextInputLayout tilTur = currView.findViewById(R.id.turSecCont);

        actv = currView.findViewById(R.id.turSec);
        grupSec = currView.findViewById(R.id.grupSec);

        setupPagination();

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

        YonlendirmeAyarla();
        PlayerAyarla();
        actv.setAdapter(aaTur);
        actv.setText(aaTur.getItem(0), false);
        TurSecildi(0);
        return currView;
    }


    public void YonlendirmeAyarla() {
        int currentOrientation = getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("M3U", "landscapeConstraintSet");
            landscapeConstraintSet.applyTo(anaYerlesim);
            if(tamEkran) aramaBolmesi.setVisibility(View.GONE);
        } else {
            Log.d("M3U", "portraitConstraintSet");
            portraitConstraintSet.applyTo(anaYerlesim);
        }
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
        aktifTur = position == 2 ? M3UBilgi.M3UTur.seri : (position == 1 ? M3UBilgi.M3UTur.film : M3UBilgi.M3UTur.tv);
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
        M3UGrup bulunanGrup = M3UVeri.GrupBul(M3UVeri.GrupDegiskenBul(aktifTur), aktifGrupAd);
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

    public void btnAraClicked(View view) {
        filtre.filtre = filtreAlan.getText().toString();
        TurSecildi(M3UVeri.SiraBul(aktifTur));

        imm.hideSoftInputFromWindow(filtreAlan.getWindowToken(), 0);
    }


    private ExoPlayer player;
    MediaItem mediaItem;

    void PlayerAyarla() {
        player = new ExoPlayer.Builder(mainActivity).build();
        playerView.setPlayer(player);
        tamEkranButton = TusEkle(R.drawable.baseline_fullscreen_24, 0);
        tamEkran = false;
        yenidenYukleButton = TusEkle(R.drawable.baseline_autorenew_24, 80);

        playerView.setControllerVisibilityListener(new PlayerView.ControllerVisibilityListener() {
            @Override
            public void onVisibilityChanged(int visibility) {
                if (visibility == View.VISIBLE) {
                    tamEkranButton.setVisibility(View.VISIBLE);
                    yenidenYukleButton.setVisibility(View.VISIBLE);
                } else {
                    tamEkranButton.setVisibility(View.GONE);
                    yenidenYukleButton.setVisibility(View.GONE);
                }
            }
        });
    }

    private ImageButton TusEkle(int iconResId, int i) {
        ImageButton btn = new ImageButton(mainActivity);
        btn.setImageResource(iconResId);
        btn.setVisibility(View.GONE);
        btn.setBackgroundColor(Color.TRANSPARENT);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.END // Position the layout as per your desired location
        );

        layoutParams.rightMargin = i;
        playerView.addView(btn, layoutParams);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            @UnstableApi
            public void onClick(View v) {
                if (v == tamEkranButton)
                    TamEkranDegistir();
                // Handle the button click event
            }
        });

        return btn;
    }

    @UnstableApi
    private void TamEkranDegistir() {
        tamEkran = !tamEkran;
        if (tamEkran) {
            Log.d("M3U", "Tam Ekrana Geçiş Tuşu");
            tamEkranButton.setImageResource(R.drawable.baseline_fullscreen_exit_24);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            aramaBolmesi.setVisibility(View.GONE);
            //actionBar.hide();
            mainActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mainActivity.YatayYap();
        } else {
            Log.d("M3U", "Tam Ekrandan Çıkış Tuşu");
            tamEkranButton.setImageResource(R.drawable.baseline_fullscreen_24);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            mainActivity.GoruntulenenYap();
            aramaBolmesi.setVisibility(View.VISIBLE);
            mainActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
    void OynatBakalim(String videoUri) {
        mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();

        player.play();
    }

    public void NesneSecildi(int pos) {
        //Log.d("RV", kanalListe.get(pos).urlAdres);
        OynatBakalim(kanalListe.get(pos).urlAdres);
    }

}