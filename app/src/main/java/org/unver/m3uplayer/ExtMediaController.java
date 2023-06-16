package org.unver.m3uplayer;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import org.videolan.libvlc.MediaPlayer;

public class ExtMediaController extends MediaController implements MediaController.MediaPlayerControl {

    private ImageButton tamEkranButton;
    private ImageButton yenidenYukleButton;

    public ExtMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public ExtMediaController(Context context) {
        super(context);
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        tamEkranButton = TusEkle(R.drawable.baseline_fullscreen_24, 0);
        yenidenYukleButton = TusEkle(R.drawable.baseline_autorenew_24, 80);
    }

    private ImageButton TusEkle(int iconResId, int i) {
        ImageButton btn = new ImageButton(getContext());
        btn.setImageResource(iconResId);
        //btn.setVisibility(View.GONE);
        btn.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams.rightMargin = i;
        addView(btn, layoutParams);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (v == tamEkranButton)
//                    TamEkranDegistir();
                // Handle the button click event
            }
        });

        return btn;
    }


    @Override
    public void start() {
        mediaPlayer.play();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return (int) mediaPlayer.getLength();
    }

    @Override
    public int getCurrentPosition() {
        return (int) mediaPlayer.getTime();
    }

    @Override
    public void seekTo(int position) {
        mediaPlayer.setTime(position);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        // Not implemented in this example
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    private MediaPlayer mediaPlayer;

    public void setVLCMediaPlayer(MediaPlayer mediaPlayer) {
        // Not implemented in this example
        this.mediaPlayer = mediaPlayer;
    }


    @Override
    public int getAudioSessionId() {
        // Not implemented in this example
        return 0;
    }

    public void updateLayout() {
//        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//        int width = getMeasuredWidth();
//        int height = getMeasuredHeight();
//
//        // Yeni boyutu ayarla
//        setLayoutParams(new RelativeLayout.LayoutParams(width, height));

    }
}
