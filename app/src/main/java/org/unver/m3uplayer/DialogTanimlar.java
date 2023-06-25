package org.unver.m3uplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class DialogTanimlar {

    private static AlertDialog alertDialogTurSec;

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
        builder.setTitle("Select Language");

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
}
