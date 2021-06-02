package com.bapan.torch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
private ConstraintLayout display;
private MyGestureDetector gestureDetector;
private float brightness = 0.25f;
private WindowManager.LayoutParams lp;
private CameraManager mCameraManager;
private String mCameraId;
private SharedPrepData sharedPrepData;
private GuideFragment guideFragment;
private FragmentTransaction fragmentTransaction;

private boolean torchType = false;
TextView tv;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize Views
        display = findViewById(R.id.display_id);
        tv = findViewById(R.id.textView);
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
        //start App
        if(sharedPrepData.getGuideStatus() || sharedPrepData.getGuideInt() > 2){
            guideFragment = new GuideFragment();
            showGuide();
        }
        switchOnTorch(torchType);
        getWindow().setAttributes(lp);
        hideNavigationButton();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        gestureDetector = new MyGestureDetector(display,this);
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
            }

            @Override
            public void onPinchIn() {
                sharedPrepData.setGuideInt(0);
                switchOnTorch(false);
                finish();
            }
        });
        //Test codes here
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationButton();
    }

    @Override
    public void onBackPressed() {
        sharedPrepData.setGuideInt(sharedPrepData.getGuideInt()+1);
        super.onBackPressed();
    }

    /* methods*/
    // hide navigation button //
    void hideNavigationButton(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    // switch on torch //
    void switchOnTorch(boolean type){
            switchBackTorch(type);
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
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void switchBackTorch(boolean state){
        try {
            mCameraManager.setTorchMode(mCameraId, state);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}