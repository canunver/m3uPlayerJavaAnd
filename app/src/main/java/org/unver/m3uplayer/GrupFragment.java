package org.unver.m3uplayer;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GrupFragment extends Fragment {
    private final MainActivity mainActivity;
    private View aktifFragment;
    private ArrayAdapter<String> grupAdapterGel;
    private ArrayAdapter<String> grupAdapterKul;
    private AutoCompleteTextView grupSecKul;
    private AutoCompleteTextView grupSecGel;
    private EditText filtreAranacak;
    private AutoCompleteTextView grupIcinKanalSec;
    private KodAdAdapter grupIcinKanalSecAdapter;
    private ArrayList<KodAd> kanalListe;
    private int secilenPosition = -1;
    private KodAdAdapter grupKanallariAdapter;
    private final List<KodAd> grupKanalListesi = new ArrayList<>();
    private int secIndKul;
    private int secIndGel;
    private ArrayList<String> grupAdapterArray;
    private ArrayList<String> grupAdapterGelArray;

    public GrupFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void MenuGizleAc(PopupMenu menu, int menuItemID) {
        MenuItem item = menu.getMenu().findItem(menuItemID);
        if (item != null) {
            item.setVisible(OrtakAlan.yetiskinlerVar);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        aktifFragment = inflater.inflate(R.layout.fragment_grup, container, false);

        grupSecKul = aktifFragment.findViewById(R.id.kulGrupSec);
        grupSecKul.setOnItemClickListener((parent, view, position, id) -> {
            GrupSecildiKul(position);
            grupKanallariAdapter.notifyDataSetChanged();
        });

        kullaniciGruplariOl(null);

        Object[] donenlerGel = YayinFragment.GrupListesiOl(mainActivity, false, null, null, 2, false, true, true, false);
        grupAdapterGel = (ArrayAdapter<String>) donenlerGel[0];
        grupAdapterGelArray = (ArrayList<String>) donenlerGel[2];
        grupSecGel = aktifFragment.findViewById(R.id.gelGrupSec);
        grupSecGel.setAdapter(grupAdapterGel);
        grupSecGel.setOnItemClickListener((parent, view, position, id) -> secIndGel = position);


        Button kulGrupMenuButton = aktifFragment.findViewById(R.id.kulGrupMenu);
        Button gelGrupMenuButton = aktifFragment.findViewById(R.id.gelGrupMenu);
        PopupMenu popupMenuKul = new PopupMenu(aktifFragment.getContext(), kulGrupMenuButton);
        popupMenuKul.getMenuInflater().inflate(R.menu.menukulgrup, popupMenuKul.getMenu());
        MenuGizleAc(popupMenuKul, R.id.action_knl_yet_normal);
        MenuGizleAc(popupMenuKul, R.id.action_kulgrp_yet_normal);

        popupMenuKul.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_asagi_tasi || item.getItemId() == R.id.action_yukari_tasi) {
                kanallariTasi(item.getItemId() == R.id.action_yukari_tasi);
                return true;
            } else if (item.getItemId() == R.id.action_knl_gizle_ac || item.getItemId() == R.id.action_knl_yet_normal) {
                kanallariDegistir(item.getItemId() == R.id.action_knl_gizle_ac, item.getItemId() == R.id.action_knl_yet_normal);
                return true;
            } else if (item.getItemId() == R.id.action_knl_sil) {
                DialogTanimlar.onayAl(R.string.onaySilmeBaslik, R.string.onaySilmeMesaj,
                        onaylandi -> {
                            if (onaylandi) {
                                M3UGrup bulunanGrup = M3UVeri.GrupBul(M3UVeri.GrupDegiskenBul(mainActivity.aktifTur), grupSecKul.getText().toString(), true);
                                if (bulunanGrup != null) {

                                    boolean degisti = false;
                                    for (int i = grupKanalListesi.size() - 1; i >= 0; i--) {
                                        KodAd ka = grupKanalListesi.get(i);
                                        if (ka.secili) {
                                            grupKanalListesi.remove(i);
                                            degisti = true;
                                        }
                                    }
                                    if (degisti) {
                                        bulunanGrup.listeYenile(grupKanalListesi);
                                        grupKanallariAdapter.notifyDataSetChanged();
                                    }
                                } else
                                    Toast.makeText(aktifFragment.getContext(), R.string.secilenGrupBulunamadi, Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                );
                return true;
            } else if (item.getItemId() == R.id.action_grp_yeni) {
                GrupIsmiAl(null);
                return true;
            } else if (item.getItemId() == R.id.action_grp_ad_degistir) {
                GrupIsmiAl(grupSecKul.getText().toString());
                return true;
            } else if (item.getItemId() == R.id.action_grp_sil) {
                M3UGrup bulunanGrup = M3UVeri.GrupBul(M3UVeri.GrupDegiskenBul(mainActivity.aktifTur), grupSecKul.getText().toString(), true);
                if (bulunanGrup != null) {
                    if (bulunanGrup.kanallar.size() > 0) {
                        Toast.makeText(aktifFragment.getContext(), R.string.kanalliGrupSilinemez, Toast.LENGTH_LONG).show();
                    } else {
                        bulunanGrup.sil();
                        M3UVeri.GrupDegiskenBul(mainActivity.aktifTur).remove(bulunanGrup);
                        kullaniciGruplariOl(null);
                        grupKanallariAdapter.notifyDataSetChanged();
                    }
                } else
                    Toast.makeText(aktifFragment.getContext(), R.string.secilenGrupBulunamadi, Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.action_kulgrp_gizle_ac || item.getItemId() == R.id.action_kulgrp_yet_normal) {
                grupOzellikDegistir(item.getItemId() == R.id.action_kulgrp_gizle_ac, item.getItemId() == R.id.action_kulgrp_yet_normal);
                return true;
            }
            return false;
        });
        PopupMenu popupMenuGel = new PopupMenu(aktifFragment.getContext(), gelGrupMenuButton);
        popupMenuGel.getMenuInflater().inflate(R.menu.menugelgrup, popupMenuGel.getMenu());
        MenuGizleAc(popupMenuGel, R.id.action_gelknl_yet_normal);
        MenuGizleAc(popupMenuGel, R.id.action_gelgrp_yet_normal);
        popupMenuGel.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_gelknl_gizle_ac || item.getItemId() == R.id.action_gelknl_yet_normal) {
                gelKanaliDegistir(item.getItemId() == R.id.action_gelknl_gizle_ac, item.getItemId() == R.id.action_gelknl_yet_normal);
                return true;
            } else if (item.getItemId() == R.id.action_gelgrp_gizle_ac || item.getItemId() == R.id.action_gelgrp_yet_normal) {
                gelGrupOzellikDegistir(item.getItemId() == R.id.action_gelgrp_gizle_ac, item.getItemId() == R.id.action_gelgrp_yet_normal);
                return true;
            }
            return false;
        });

        kulGrupMenuButton.setOnClickListener(v -> popupMenuKul.show());

        gelGrupMenuButton.setOnClickListener(v -> popupMenuGel.show());

        filtreAranacak = aktifFragment.findViewById(R.id.filtreAranacak);
        ImageButton btnEklenecekAra = aktifFragment.findViewById(R.id.btnEklenecekAra);
        btnEklenecekAra.setOnClickListener(v -> {
            mainActivity.imm.hideSoftInputFromWindow(filtreAranacak.getWindowToken(), 0);
            ArananKanallariDoldur();
        });
        grupIcinKanalSec = aktifFragment.findViewById(R.id.grupIcinKanalSec);
        grupIcinKanalSec.setOnItemClickListener((parent, view, position, id) -> secilenPosition = position);
        Button secKanaliEkle = aktifFragment.findViewById(R.id.secKanaliEkle);
        kanalListe = new ArrayList<>();
        grupIcinKanalSecAdapter = new KodAdAdapter(mainActivity, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, kanalListe, false);
        secKanaliEkle.setOnClickListener(v -> {
            if (secilenPosition >= 0)
                KanalEkle(kanalListe.get(secilenPosition));
        });

        ListView grupKanallari = aktifFragment.findViewById(R.id.grupKanallari);
        grupKanallari.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        grupKanallariAdapter = new KodAdAdapter(mainActivity, android.R.layout.simple_list_item_1, grupKanalListesi, true);
        grupKanallari.setAdapter(grupKanallariAdapter);
        grupKanallari.setOnItemClickListener((parent, view, position, id) -> grupKanallariAdapter.setSelectedItemPosition(position));
        setHasOptionsMenu(true);
        requireActivity().invalidateOptionsMenu();
        return aktifFragment;
    }

    private void gelGrupOzellikDegistir(boolean gizliDegistir, boolean yetiskinDegistir) {
        M3UGrup bulunanGrup = M3UVeri.GrupBul(M3UVeri.GrupDegiskenBul(mainActivity.aktifTur), grupSecGel.getText().toString(), true);
        if (bulunanGrup != null) {
            if (bulunanGrup.ozellikDegistir(gizliDegistir, yetiskinDegistir)) {
                String tut = bulunanGrup.grupAdiBul(true, OrtakAlan.GizliBul(mainActivity), OrtakAlan.YetiskinBul(mainActivity));
                grupSecGel.setText(tut, false);
                grupAdapterGelArray.set(secIndGel, tut);
                grupAdapterGel.notifyDataSetChanged();
            }
        } else
            Toast.makeText(aktifFragment.getContext(), R.string.secilenGrupBulunamadi, Toast.LENGTH_SHORT).show();
    }

    private void grupOzellikDegistir(boolean gizliDegistir, boolean yetiskinDegistir) {
        M3UGrup bulunanGrup = M3UVeri.GrupBul(M3UVeri.GrupDegiskenBul(mainActivity.aktifTur), grupSecKul.getText().toString(), true);
        if (bulunanGrup != null) {
            if (bulunanGrup.ozellikDegistir(gizliDegistir, yetiskinDegistir)) {
                String tut = bulunanGrup.grupAdiBul(true, OrtakAlan.GizliBul(mainActivity), OrtakAlan.YetiskinBul(mainActivity));
                grupSecKul.setText(tut, false);
                grupAdapterArray.set(secIndKul, tut);
                grupAdapterKul.notifyDataSetChanged();
            }
        } else
            Toast.makeText(aktifFragment.getContext(), R.string.secilenGrupBulunamadi, Toast.LENGTH_SHORT).show();
    }

    private void kullaniciGruplariOl(String simdikiSecili) {
        Object[] donenlerKul = YayinFragment.GrupListesiOl(mainActivity, false, null, simdikiSecili, 1, false, true, true, true);
        grupAdapterKul = (ArrayAdapter<String>) donenlerKul[0];
        int yerIndKul = (int) donenlerKul[1];
        grupAdapterArray = (ArrayList<String>) donenlerKul[2];
        grupSecKul.setAdapter(grupAdapterKul);
        if (yerIndKul >= 0) {
            grupSecKul.setText(grupAdapterKul.getItem(yerIndKul), false);
            GrupSecildiKul(yerIndKul);
        }
    }

    private void kanallariDegistir(boolean gizliDegistir, boolean yetiskinDegistir) {
        M3UGrup bulunanGrup = M3UVeri.GrupBul(M3UVeri.GrupDegiskenBul(mainActivity.aktifTur), grupSecKul.getText().toString(), true);
        if (bulunanGrup != null) {
            boolean degisti = false;
            for (int i = 0; i < grupKanalListesi.size(); i++) {
                KodAd simdiki = grupKanalListesi.get(i);
                if (simdiki.secili) {
                    M3UBilgi m = (M3UBilgi) simdiki.o;
                    m.ozellikDegistir(gizliDegistir, yetiskinDegistir);
                    simdiki.ad = m.tvgNameOzellikliAl(OrtakAlan.GizliBul(mainActivity), OrtakAlan.YetiskinBul(mainActivity));
                    degisti = true;
                }
            }
            if (degisti)
                grupKanallariAdapter.notifyDataSetChanged();
        } else
            Toast.makeText(aktifFragment.getContext(), R.string.secilenGrupBulunamadi, Toast.LENGTH_SHORT).show();
    }

    private void gelKanaliDegistir(boolean gizliDegistir, boolean yetiskinDegistir) {
        if (secilenPosition >= 0) {
            KodAd simdiki = kanalListe.get(secilenPosition);
            M3UBilgi m = (M3UBilgi) simdiki.o;
            m.ozellikDegistir(gizliDegistir, yetiskinDegistir);
            simdiki.ad = m.tvgNameOzellikliAl(OrtakAlan.GizliBul(mainActivity), OrtakAlan.YetiskinBul(mainActivity));
            grupIcinKanalSec.setText(simdiki.ad, false);
            kanalListe.set(secilenPosition, simdiki);
            grupIcinKanalSecAdapter.notifyDataSetChanged();
        }
    }

    private void kanallariTasi(boolean siraNoDusur) {
        boolean degisti = false;

        M3UGrup bulunanGrup = M3UVeri.GrupBul(M3UVeri.GrupDegiskenBul(mainActivity.aktifTur), grupSecKul.getText().toString(), true);
        if (bulunanGrup != null) {
            for (int i = 1; i < grupKanalListesi.size(); i++) {
                int islenen;
                if (siraNoDusur)
                    islenen = i;
                else
                    islenen = grupKanalListesi.size() - i - 1;
                KodAd simdiki = grupKanalListesi.get(islenen);
                if (simdiki.secili) {
                    int yerDegistir;
                    if (siraNoDusur)
                        yerDegistir = islenen - 1;
                    else
                        yerDegistir = islenen + 1;
                    KodAd degisecek = grupKanalListesi.get(yerDegistir);
                    if (!degisecek.secili) {
                        grupKanalListesi.set(islenen, degisecek);
                        grupKanalListesi.set(yerDegistir, simdiki);
                        degisti = true;
                    }
                }
            }
            if (degisti) {
                grupKanallariAdapter.notifyDataSetChanged();
                bulunanGrup.listeYenile(grupKanalListesi);
            }
        } else
            Toast.makeText(aktifFragment.getContext(), R.string.secilenGrupBulunamadi, Toast.LENGTH_SHORT).show();
    }

    private void KanalEkle(KodAd kodAd) {
        M3UBilgi m3u = (M3UBilgi) kodAd.o;
        KodAd ka = new KodAd(m3u.ID, m3u.tvgNameOzellikliAl(OrtakAlan.GizliBul(mainActivity), OrtakAlan.YetiskinBul(mainActivity)), m3u);
        if (!grupKanalListesi.contains(ka)) {
            M3UGrup bulunanGrup = M3UVeri.GrupBul(M3UVeri.GrupDegiskenBul(mainActivity.aktifTur), grupSecKul.getText().toString(), true);
            if (bulunanGrup != null) {
                grupKanalListesi.add(ka);
                bulunanGrup.kanalEkle(ka.kod);
                grupKanallariAdapter.notifyDataSetChanged();
            } else
                Toast.makeText(aktifFragment.getContext(), R.string.secilenGrupBulunamadi, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(aktifFragment.getContext(), R.string.zatenListedeVar, Toast.LENGTH_SHORT).show();
    }

    private void ArananKanallariDoldur() {
        M3UGrup bulunanGrup = M3UVeri.GrupBul(M3UVeri.GrupDegiskenBul(mainActivity.aktifTur), grupSecGel.getText().toString(), true);
        if (bulunanGrup != null) {
            M3UFiltre f = new M3UFiltre();
            f.isimFiltreStr = filtreAranacak.getText().toString();
            kanalListe.clear();
            boolean toasted = false;
            for (String kanalId : bulunanGrup.kanallar) {
                M3UBilgi m3u = M3UVeri.tumM3UListesi.get(kanalId);
                if (m3u!=null && m3u.FiltreUygunMu(f, true)) {
                    kanalListe.add(new KodAd(m3u.ID, m3u.tvgNameOzellikliAl(OrtakAlan.GizliBul(mainActivity), OrtakAlan.YetiskinBul(mainActivity)), m3u));
                    if (kanalListe.size() > 30) {
                        Toast.makeText(aktifFragment.getContext(), R.string.aranan30danFazla, Toast.LENGTH_SHORT).show();
                        toasted = true;
                        break;
                    }
                }
            }
            if (!toasted) {
                if(kanalListe.size()>0)
                    Toast.makeText(aktifFragment.getContext(), R.string.bulunanlarEklendi, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(aktifFragment.getContext(), R.string.aramaKayitYok, Toast.LENGTH_SHORT).show();
            }
            grupIcinKanalSec.setAdapter(grupIcinKanalSecAdapter);
            grupIcinKanalSec.clearListSelection();
            if (kanalListe.size() > 0) {
                grupIcinKanalSec.setText(kanalListe.get(0).ad, false);
                secilenPosition = 0;
            } else
                secilenPosition = -1;
        } else
            Toast.makeText(aktifFragment.getContext(), R.string.secilenGrupBulunamadi, Toast.LENGTH_SHORT).show();
    }

    private void GrupIsmiAl(String kulGrupIsmi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(aktifFragment.getContext());
        int titleId;
        if (kulGrupIsmi == null)
            titleId = R.string.yeni_grup_ismi_gir;
        else
            titleId = R.string.yeni_grup_ismi_degistir;
        builder.setTitle(titleId);

        final EditText input = new EditText(aktifFragment.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (kulGrupIsmi != null)
            input.setText(kulGrupIsmi);
        builder.setView(input);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            String yeniAd = input.getText().toString();
            int hata;
            if ((hata = M3UVeri.GrupIsminiYaz(mainActivity.aktifTur, kulGrupIsmi, yeniAd)) == 0) {
                kullaniciGruplariOl(yeniAd);
                grupKanallariAdapter.notifyDataSetChanged();
            } else
                Toast.makeText(aktifFragment.getContext(), hata, Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void GrupSecildiKul(int position) {
        grupKanalListesi.clear();
        secIndKul = position;
        M3UGrup bulunanGrup = M3UVeri.GrupBul(M3UVeri.GrupDegiskenBul(mainActivity.aktifTur), grupSecKul.getText().toString(), true);
        if (bulunanGrup != null) {
            for (String m3uId : bulunanGrup.kanallar) {
                M3UBilgi m3uBilgi = M3UVeri.tumM3UListesi.getOrDefault(m3uId, null);
                if (m3uBilgi != null)
                    grupKanalListesi.add(new KodAd(m3uBilgi.ID, m3uBilgi.tvgNameOzellikliAl(OrtakAlan.GizliBul(mainActivity), OrtakAlan.YetiskinBul(mainActivity)), m3uBilgi));
            }
        } else {
            Toast.makeText(aktifFragment.getContext(), R.string.secilenGrupBulunamadi, Toast.LENGTH_SHORT).show();
        }
    }

    public void YonlendirmeAyarla() {
    }
}