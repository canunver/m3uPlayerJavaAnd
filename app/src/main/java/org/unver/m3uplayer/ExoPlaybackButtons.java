package org.unver.m3uplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.ui.PlayerView;

//import androidx.media3.exoplayer.ui.PlayerControlView;
//import androidx.media3.exoplayer.ui.PlayerView;
//extends FrameLayout
public class ExoPlaybackButtons extends FrameLayout implements PlayerView.ControllerVisibilityListener {
    private Button tamEkranButton;

    public ExoPlaybackButtons(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ExoPlaybackButtons(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExoPlaybackButtons(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //LayoutInflater.from(context).inflate(R.layout.exo_playback_control_view, this, true);
        //tamEkranButton = findViewById(R.id.tamEkranButton);

//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//        );
        //addView(tamEkranButton, params);


        // Find and customize the necessary views in the custom control view layout
//        tamEkranButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle the custom button click event
//            }
//        });
    }

    @Override
    public void onVisibilityChanged(int visibility) {

    }
}
