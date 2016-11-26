package com.iptv.rocky.tcl.view.vod;

import com.iptv.rocky.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 
 */
public class VideoPlayer extends BaseVideoPlayer {

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void fixLandscapeUI() {
        Toast.makeText(context, "Landscapte", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fixPortraitUI() {
        Toast.makeText(context, "Portrait", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void init(){
        parentView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.base_video_view, this);
        surfaceView = (SurfaceView) parentView.findViewById(R.id.video_surface);
        progressBar = (ProgressBar) parentView.findViewById(R.id.base_video_progressBar);
        surfaceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPause){
                    pause();
                    isPause = true;
                }else{
                    start();
                    isPause = false;
                }
            }
        });
        Log.v("BaseVideo", ">>>created! time:" + System.currentTimeMillis());
    }


}
