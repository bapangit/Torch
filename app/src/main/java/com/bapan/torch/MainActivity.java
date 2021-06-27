package com.bapan.torch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
private ImageView imageView,imageViewSettings;
private boolean torchMode,sideMode;
private Timer timer;
private boolean sideType = false;
DatabaseReference ref;
boolean isappclosed = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize Views
        display = findViewById(R.id.display_id);
        imageView = findViewById(R.id.imageView);
        imageViewSettings = findViewById(R.id.imageViewSettings);
        //initialize App
        ref = FirebaseDatabase.getInstance().getReference();
        sharedPrepData = new SharedPrepData(MainActivity.this);
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        guideFragment = new GuideFragment();
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        
        lp = getWindow().getAttributes();
        lp.screenBrightness = brightness;
        
        if(sharedPrepData.getGuideInt() == 3){
            Toast.makeText(this, "Double tap on screen to change side.", Toast.LENGTH_LONG).show();
        }
        if(sharedPrepData.getGuideInt() == 4){
            Toast.makeText(MainActivity.this, "Press and hold the button to exit.", Toast.LENGTH_LONG).show();
        }

        
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final MediaPlayer btnClickEffect = MediaPlayer.create(this,R.raw.click_effect);
        btnClickEffect.setVolume(0.4f,0.4f);

        //starting torch
        sideType = sharedPrepData.getSideType();
        switchOnTorch(sideType,false);
        
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

        ref.child("ads/banner1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue((Boolean.class))){

                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        
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
                                sharedPrepData.setGuideInt(0);
                                finish();
                                setBackTorchMode(false);
                                isappclosed = true;
                            }
                        }, 1000);
                        break;
                    case MotionEvent.ACTION_UP:
                        timer.cancel();
                        imageView.animate().scaleX(1f).setDuration(100);
                        imageView.animate().scaleY(1f).setDuration(100);
                        if(!isappclosed){
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
                            if(sharedPrepData.getSwitchSound()){
                                btnClickEffect.start();
                            }
                        }
                        break;
                }
                return true;
            }
        });
        imageViewSettings.setOnClickListener(v -> {
            showGuide();
        });
        gestureDetector.setListeners(new MyGestureDetector.MyGestureListeners() {
            @Override
            public void onVerticalSwipe(float change) {
                if(!sideType){
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
                sideType = !sideType;
                switchOnTorch(sideType,false);
            }

            @Override
            public void onPinchIn() {
                sharedPrepData.setGuideInt(0);
                setBackTorchMode(false);
                finish();
            }
        });
        //start
        if(sharedPrepData.getGuideInt() > 4){
            showGuide();
        }
        //Test codes here
    }

    @Override
    protected void onPause() {
        super.onPause();
        runNotification(sharedPrepData.getNotificationType());
    }

    @Override
    protected void onResume() {
        super.onResume();
        isappclosed = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            hideNavigationButton();
        }
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
    public void switchOnTorch(boolean sideType,boolean torchType){
    sideMode = sideType;
    torchMode = torchType;
    if(sideType){
        imageView.setVisibility(View.VISIBLE);
        imageViewSettings.setVisibility(View.VISIBLE);
    }else {
        imageView.setVisibility(View.INVISIBLE);
        imageViewSettings.setVisibility(View.INVISIBLE);
    }
    setBackTorchMode(torchMode);
            if(sideType){
                display.setBackgroundColor(Color.DKGRAY);
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
                else
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.off_button));
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("RemoteViewLayout")
    public void runNotification(boolean type) {

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), "channel_id");
        contentView = new RemoteViews(getPackageName(), R.layout.my_notification_layout);

        Intent switchIntent = new Intent(this, NotificationBroadcast.class);
        switchIntent.putExtra("key_name","true");
        Intent switchOffIntent = new Intent(this, NotificationBroadcast.class);
        switchOffIntent.putExtra("key_name2","false");

        contentView.setOnClickPendingIntent(R.id.flashButton, PendingIntent.getBroadcast(this, 1020, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        contentView.setOnClickPendingIntent(R.id.flashButton2, PendingIntent.getBroadcast(this, 1021, switchOffIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        mBuilder.setSmallIcon(R.drawable.torchon);
        mBuilder.setAutoCancel(true);
        mBuilder.setOngoing(type);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.build().flags = Notification.VISIBILITY_PUBLIC| Notification.PRIORITY_MAX;
        mBuilder.setContent(contentView);
        mBuilder.setDefaults(0);
        mBuilder.setSound(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            NotificationChannel channel = new NotificationChannel(channelId, "channelname", NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        notification = mBuilder.build();
        notificationManager.notify(NotificationID, notification);
    }
    //notification related
    private static RemoteViews contentView;
    private static Notification notification;
    private static NotificationManager notificationManager;
    private static final int NotificationID = 1005;
    private static NotificationCompat.Builder mBuilder;
}