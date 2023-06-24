package org.unver.m3uplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

public class DialogTanimlar {

    public static AlertDialog ParolaAl(MainActivity mainActivity ) {
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

        return  builder.create();
    }

    public static AlertDialog TurAl(MainActivity mainActivity, ArrayList<String> turDizisi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

        // set title
        builder.setTitle("Select Language");

        // set dialog non cancelable
        builder.setCancelable(false);

        String[] arr = new String[turDizisi.size()];

        builder.setMultiChoiceItems(turDizisi.toArray(arr), null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                // check condition
                if (b) {
                    // when checkbox selected
                    // Add position  in lang list
                    //langList.add(i);
                    // Sort array list
                    //Collections.sort(langList);
                } else {
                    // when checkbox unselected
                    // Remove position from langList
                    //langList.remove(Integer.valueOf(i));
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Initialize string builder
                StringBuilder stringBuilder = new StringBuilder();
                // use for loop
//                for (int j = 0; j < langList.size(); j++) {
//                    // concat array value
//                    stringBuilder.append(langArray[langList.get(j)]);
//                    // check condition
//                    if (j != langList.size() - 1) {
//                        // When j value  not equal
//                        // to lang list size - 1
//                        // add comma
//                        stringBuilder.append(", ");
//                    }
//                }
//                // set text on textView
//                textView.setText(stringBuilder.toString());
            }
        });

//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                // dismiss dialog
//                dialogInterface.dismiss();
//            }
//        });
//        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                // use for loop
//                for (int j = 0; j < selectedLanguage.length; j++) {
//                    // remove all selection
//                    selectedLanguage[j] = false;
//                    // clear language list
//                    langList.clear();
//                    // clear text view value
//                    textView.setText("");
//                }
//            }
//        });
        // show dialog
        return builder.create();
    }
}
