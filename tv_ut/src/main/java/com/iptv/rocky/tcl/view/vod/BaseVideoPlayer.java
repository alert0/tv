package com.iptv.rocky.tcl.view.vod;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.iptv.rocky.R;
//import android.media.Image;


/**
 * Created by Wayne on 2015/4/17.
 * This is a basic video widget that can be used directly or extended
 * In this class it will only implement:
 * 1. video IO/Streaming
 * 2. AD/Content preloading 
 * 3. P/L switch calculation
 *  
 */
public class BaseVideoPlayer extends RelativeLayout{
    protected Context context;
    protected RelativeLayout parentView;
    // widgets
    protected SurfaceView surfaceView;
    protected SurfaceHolder surfaceHolder;
    protected ProgressBar progressBar;
    protected ImageView maskImage;
    // values
    protected int videoWidth;
    protected int videoHeight;
    protected boolean isPause = false;
    protected boolean isLandscape = false;
    protected boolean isFullScreenClick = false;
    // VideoPlayer
    private MyVideoPlayer myVideoPlayer;
    /** listeners **/
    public BaseVideoPlayer(Context context){
        super(context);
        this.context = context;
        init();
    }
    public BaseVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    /** fix UI here **/
    public void fixLandscapeUI(){}
    public void fixPortraitUI(){}

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

    public  void setDisplay(ArrayList<String> path){
        myVideoPlayer = new MyVideoPlayer(path);
        this.surfaceHolder = this.surfaceView.getHolder();

                BaseVideoPlayer.this.surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated( SurfaceHolder surfaceHolder) {
                        myVideoPlayer.loadFirstVideo(surfaceHolder, new MyVideoPlayer.onPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mediaPlayer.start();
                                fixPLViews();
                                try {
                                    progressBar.setVisibility(GONE);
                                    maskImage.setVisibility(GONE);
                                } catch (Exception e) {e.printStackTrace();}

                                Log.v("BaseVideo", ">>>start! time:" + System.currentTimeMillis());
                                Log.v("BaseVideo", ">>>video duration:" +  myVideoPlayer.getCurrentVideoDuration());
                            }
                        });
                        myVideoPlayer.loadMorePlayerThread(surfaceHolder);
                    }

                    @Override
                    public void surfaceChanged(final SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                        if (null != myVideoPlayer.getCurrentPlayer()) {
                            myVideoPlayer.releaseAll();
                            synchronized(this) {
                                this.notifyAll();
                            }
                        }
                    }
                });

        this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        new OrientationEventListener(context){

            @Override
            public void onOrientationChanged(int rotation) {
                // TODO Auto-generated method stub

                if(isLandscape && !isFullScreenClick){
                    if (((rotation >= 0) && (rotation <= 30)) || (rotation >= 330)) {
                        ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        isLandscape = false;
                    }
                }else if(!isFullScreenClick && !isLandscape){
                    if (((rotation >= 230) && (rotation <= 310))) {
                        ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        isLandscape = true;
                    }

                }
                if(isFullScreenClick){
                    if(isLandscape){
                        if (((rotation >= 0) && (rotation <= 30)) || (rotation >= 330)) {     // Mark A
                            isFullScreenClick = false;
                        }
                    }else {
                        if (((rotation >= 230) && (rotation <= 310))) {
                            isFullScreenClick = false;
                        }
                    }
                }
            }
        }.enable();
    }

    /** this is a method for full screen button **/
    public void setFullScreenSwitcher(){
        if(isLandscape){
            ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isFullScreenClick = true;
        }else {
            ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isFullScreenClick = true;
        }
    }

    /** media player function widgets **/
    public void start() {
        try {
            myVideoPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            myVideoPlayer.pause();
            Log.v("BaseVideo", ">>>pause! time:" + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            myVideoPlayer.stop();
            Log.v("BaseVideo", ">>>stop! time:" + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
          myVideoPlayer.releaseAll();
            Log.v("BaseVideo", ">>>release! time:" + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void seekTo(int position) {
        try {
            myVideoPlayer.seekTo(position);
            Log.v("BaseVideo", ">>>seek to:" + position + " time:" + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCurrentVideoPosition() {
        int position = 0;
        try {
            position = myVideoPlayer.getCurrentVideoPosition();
            Log.v("BaseVideo", ">>>current position: " + position + " time:" + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return position;
    }

    public int getCurrentVideoDuration() {
        int duration = 0;
        try {
            duration = myVideoPlayer.getCurrentVideoDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duration;
    }

    /** fix screen size **/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixPLViews();
    }

    public void fixPLViews(){
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenH = dm.heightPixels;
        int screenW = dm.widthPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.surfaceView.getLayoutParams();
        LinearLayout.LayoutParams parentParam = (LinearLayout.LayoutParams) this.parentView.getLayoutParams();
        this.videoWidth = this.myVideoPlayer.getVideoWidth();
        this.videoHeight = this.myVideoPlayer.getVideoHeight();
        if(screenH > screenW){  // portrait
            try {
                /** video view **/
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = screenW * this.videoHeight / this.videoWidth;
                this.surfaceView.setLayoutParams(params);

                /** back to normal screen **/
                ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

                fixPortraitUI();
            }catch (Exception e){e.printStackTrace();}

        }else if(screenH < screenW){  // landscape
            try{
                /** video view **/
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = screenH * this.videoWidth / this.videoHeight;
                params.alignWithParent = true;
                this.surfaceView.setLayoutParams(params);

                /** full screen with correct video definition **/
                ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                fixLandscapeUI();
            }catch (Exception e){e.printStackTrace();}
        }
        try {
            this.surfaceHolder.setFixedSize(this.videoWidth, this.videoHeight);
        }catch (Exception e) {e.printStackTrace();}

    }

}
