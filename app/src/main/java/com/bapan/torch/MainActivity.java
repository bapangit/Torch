package com.bapan.torch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
private ConstraintLayout display;
private MyGestureDetector gestureDetector;
public float brightness = 0.25f;
private WindowManager.LayoutParams lp;
private CameraManager mCameraManager;
private String mCameraId;
private SharedPrepData sharedPrepData;
private GuideFragment guideFragment;
private FragmentTransaction fragmentTransaction;
private AdView adView;
private ImageView imageView;
private boolean torchMode,sideMode;
private Timer timer;
private boolean torchType = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize Views
        display = findViewById(R.id.display_id);
        imageView = findViewById(R.id.imageView);
        //initialize App
        sharedPrepData = new SharedPrepData(MainActivity.this);
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        lp = getWindow().getAttributes();
        lp.screenBrightness = brightness;
        
        if(sharedPrepData.getGuideInt() > 1){
            Toast.makeText(this, "Double tap on screen to swap torch.", Toast.LENGTH_LONG).show();
        }
        getWindow().setAttributes(lp);
        hideNavigationButton();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final MediaPlayer btnClickEffect = MediaPlayer.create(this,R.raw.click_effect);
        btnClickEffect.setVolume(0.4f,0.4f);
        //admob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                //Toast.makeText(MainActivity.this, "successful", Toast.LENGTH_SHORT).show();
            }
        });

        adView = findViewById(R.id.ad_view_id);
        /*adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Toast.makeText(MainActivity.this, "loaded", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Toast.makeText(MainActivity.this, "failed "+adError.getCode(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                Toast.makeText(MainActivity.this, "opened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(MainActivity.this, "closed", Toast.LENGTH_SHORT).show();
            }
        });*/
        
        AdRequest adRequest = new AdRequest.Builder().build();
        //Toast.makeText(this, ""+sharedPrepData.getAdWaitTimes(), Toast.LENGTH_SHORT).show();
        if(sharedPrepData.getAdWaitTimes() == 0){
            sharedPrepData.setAdWaitTimes(4);
            adView.loadAd(adRequest); 
        }
        else {
                sharedPrepData.setAdWaitTimes(sharedPrepData.getAdWaitTimes() - 1);
        }
        
        
        /////
        gestureDetector = new MyGestureDetector(display,this);
        //Listeners

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        imageView.animate().scaleX(0.80f).setDuration(320);
                        imageView.animate().scaleY(0.80f).setDuration(320);
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                finish();
                                setBackTorchMode(false);
                            }
                        }, 1000);
                        break;
                    case MotionEvent.ACTION_UP:
                        timer.cancel();
                        imageView.animate().scaleX(1f).setDuration(100);
                        imageView.animate().scaleY(1f).setDuration(100);
                        if(sideMode){
                            torchMode = !torchMode;
                            if(torchMode){
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.on_button));
                                setBackTorchMode(true);
                            }
                            else {
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.off_button));
                                setBackTorchMode(false);
                            }
                        }
                        btnClickEffect.start();
                        break;
                }
                return true;
            }
        });
        gestureDetector.setListeners(new MyGestureDetector.MyGestureListeners() {
            @Override
            public void onVerticalSwipe(float change) {
                if(!torchType){
                    if(change < 0f)
                    if(brightness < 0.97f)
                        brightness += 0.02f;
                        else 
                        brightness = 0.99f;
                    if(change > 0f){
                        if(brightness > 0.04f)
                        brightness -= 0.03f;
                        else
                        brightness = 0.01f;
                    }
                    //Log.d("mTag",""+brightness);
                    lp.screenBrightness = brightness;
                    getWindow().setAttributes(lp);   
                }
            }

            @Override
            public void onDoubleTapped() {
                torchType = !torchType;
                switchOnTorch(torchType);
                if(torchType && sharedPrepData.getGuideInt() > 1){
                    Toast.makeText(MainActivity.this, "Press and hold to exit.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPinchIn() {
                sharedPrepData.setGuideInt(0);
                setBackTorchMode(false);
                finish();
            }
        });
        //start
        torchType = sharedPrepData.getTorchType();
        switchOnTorch(torchType);
        if(sharedPrepData.getGuideStatus() || sharedPrepData.getGuideInt() > 2){
            guideFragment = new GuideFragment();
            showGuide();
        }
        //Test codes here
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationButton();
        setBackTorchMode(torchMode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBackPressed() {
        sharedPrepData.setGuideInt(sharedPrepData.getGuideInt()+1);
        setBackTorchMode(false);
        super.onBackPressed();
    }

    /* methods */
    // hide navigation button //
    void hideNavigationButton(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    // switch on torch //
    public void switchOnTorch(boolean type){
    torchMode = type;
    sideMode = type;
    if(type){
        imageView.setVisibility(View.VISIBLE);
    }else {
        imageView.setVisibility(View.INVISIBLE);
    }
    setBackTorchMode(type);
            if(type){
                display.setBackgroundColor(Color.BLACK);
                setScreenBrightness(0.02f);
            }
            else {
                display.setBackgroundColor(Color.WHITE);
                setScreenBrightness(brightness);
            }
    }
    void setScreenBrightness(float level){
        lp.screenBrightness = level;
        getWindow().setAttributes(lp);
    }

    private void showGuide(){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container_id, guideFragment);
        fragmentTransaction.addToBackStack("string");
        fragmentTransaction.commit();
        setScreenBrightness(0.5f);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setBackTorchMode(boolean mode){
        try {
            mCameraManager.setTorchMode(mCameraId, mode);
                if(mode)
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.on_button));
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}