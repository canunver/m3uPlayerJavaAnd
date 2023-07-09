package org.unver.m3uplayer;

import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class GrupFragment extends Fragment {
    private final MainActivity mainActivity;

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
        View frgmnt = inflater.inflate(R.layout.fragment_grup, container, false);


        //mainActivity.GruplariBul();

        Button kulGrupMenuButton = frgmnt.findViewById(R.id.kulGrupMenu);
        Button gelGrupMenuButton = frgmnt.findViewById(R.id.gelGrupMenu);
        PopupMenu popupMenuKul = new PopupMenu(frgmnt.getContext(), kulGrupMenuButton);
        popupMenuKul.getMenuInflater().inflate(R.menu.menukulgrup, popupMenuKul.getMenu());
        popupMenuKul.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(frgmnt.getContext(), "ItemId: " + item.getItemId(), Toast.LENGTH_SHORT).show();
                return false;
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

    public void YonlendirmeAyarla() {
    }
}