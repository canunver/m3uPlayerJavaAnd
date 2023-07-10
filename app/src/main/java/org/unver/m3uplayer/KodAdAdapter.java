package org.unver.m3uplayer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class KodAdAdapter extends ArrayAdapter<KodAd> {
    private final boolean cokluSecim;
    private final List<KodAd> objects;
    private int selectedItemPosition;

    public KodAdAdapter(@NonNull Context context, int resource, @NonNull List<KodAd> objects, boolean cokluSecim) {
        super(context, resource, objects);
        this.cokluSecim = cokluSecim;
        this.objects = objects;
        this.selectedItemPosition = -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        KodAd ka = objects.get(position);
        TextView textView = view.findViewById(android.R.id.text1);

        if (ka.secili) {
            view.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.YELLOW);
        } else {
            view.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }
        textView.setText((position + 1) + "-" + ka.toString());
        return view;
    }

    public void setSelectedItemPosition(int position) {
        if (cokluSecim || (selectedItemPosition == position && position > -1)) {
            KodAd ka = objects.get(selectedItemPosition);
            ka.secili = !ka.secili;
            selectedItemPosition = -1;
        } else {
            if (selectedItemPosition > -1) {
                KodAd ka = objects.get(selectedItemPosition);
                ka.secili = false;
            }
            selectedItemPosition = position;
            if (selectedItemPosition > -1) {
                KodAd ka = objects.get(selectedItemPosition);
                ka.secili = true;
            }
        }
        notifyDataSetChanged();
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }
}
