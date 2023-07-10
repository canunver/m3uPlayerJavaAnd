package org.unver.m3uplayer;

import android.content.DialogInterface;
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
import android.widget.Toast;

import java.util.Comparator;

public class GrupFragment extends Fragment {
    private final MainActivity mainActivity;
    private View frgmnt;
    private ArrayAdapter<String> grupAdapterGel;
    private ArrayAdapter<String> grupAdapterKul;
    private AutoCompleteTextView grupSecKul;

    public GrupFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        frgmnt = inflater.inflate(R.layout.fragment_grup, container, false);


        Object[] donenlerKul = YayinFragment.GrupListesiOl(mainActivity, false, null, null, 1, false, false);
        grupAdapterKul = (ArrayAdapter<String>) donenlerKul[0];
        int yerIndKul = (int) donenlerKul[1];
        grupSecKul = frgmnt.findViewById(R.id.kulGrupSec);

        grupSecKul.setAdapter(grupAdapterKul);
        if (yerIndKul >= 0) {
            grupSecKul.setText(grupAdapterKul.getItem(yerIndKul), false);
            GrupSecildiKul(yerIndKul);
        }

        Object[] donenlerGel = YayinFragment.GrupListesiOl(mainActivity, false, null, null, 2, false, false);
        grupAdapterGel = (ArrayAdapter<String>) donenlerGel[0];
        int yerIndGel = (int) donenlerGel[1];
        AutoCompleteTextView grupSecGel = frgmnt.findViewById(R.id.gelGrupSec);

        grupSecGel.setAdapter(grupAdapterGel);
        if (yerIndGel >= 0) {
            grupSecGel.setText(grupAdapterGel.getItem(yerIndGel), false);
            GrupSecildiGel(yerIndGel);
        }

        Button kulGrupMenuButton = frgmnt.findViewById(R.id.kulGrupMenu);
        Button gelGrupMenuButton = frgmnt.findViewById(R.id.gelGrupMenu);
        PopupMenu popupMenuKul = new PopupMenu(frgmnt.getContext(), kulGrupMenuButton);
        popupMenuKul.getMenuInflater().inflate(R.menu.menukulgrup, popupMenuKul.getMenu());
        popupMenuKul.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_grp_yeni) {
                    GrupIsmiAl(null);
                    return true;
                } else if (item.getItemId() == R.id.action_grp_ad_degistir) {
                    GrupIsmiAl(grupSecKul.getText().toString());
                    return true;
                } else {
                    Toast.makeText(frgmnt.getContext(), "ItemId: " + item.getItemId(), Toast.LENGTH_SHORT).show();

                    return false;
                }
            }
        });
        PopupMenu popupMenuGel = new PopupMenu(frgmnt.getContext(), gelGrupMenuButton);
        popupMenuGel.getMenuInflater().inflate(R.menu.menugelgrup, popupMenuGel.getMenu());
        popupMenuGel.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(frgmnt.getContext(), "Hayda", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        kulGrupMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenuKul.show();
            }
        });

        gelGrupMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenuGel.show();
            }
        });
        return frgmnt;
    }

    private void GrupIsmiAl(String kulGrupIsmi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(frgmnt.getContext());
        int titleId;
        if (kulGrupIsmi == null)
            titleId = R.string.yeni_grup_ismi_gir;
        else
            titleId = R.string.yeni_grup_ismi_degistir;
        builder.setTitle(titleId);

        final EditText input = new EditText(frgmnt.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (kulGrupIsmi != null)
            input.setText(kulGrupIsmi);
        builder.setView(input);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                int hata;
                if ((hata = M3UVeri.GrupIsminiYaz(mainActivity.aktifTur, kulGrupIsmi, text)) == 0) {
                    if (kulGrupIsmi != null) {
                        grupAdapterKul.remove(kulGrupIsmi);
                    }
                    grupAdapterKul.add(text);
                    grupAdapterKul.sort(new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareToIgnoreCase(o2);
                        }
                    });
                    grupAdapterKul.notifyDataSetChanged();
                    grupSecKul.setText(text, false);
                } else
                    Toast.makeText(frgmnt.getContext(), hata, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void GrupSecildiGel(int yerIndGel) {
    }

    private void GrupSecildiKul(int yerInd) {
    }

    public void YonlendirmeAyarla() {
    }
}