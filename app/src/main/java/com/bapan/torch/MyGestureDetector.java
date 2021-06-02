package com.bapan.torch;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

public class MyGestureDetector {
   private View view;
   private Context context;
   private float current_X,current_Y;
   private float change_X,change_Y;
    
    public MyGestureDetector(View view,Context context) {
        this.view = view;
        this.context = context;
    }
    void setListeners(MyGestureListeners gestureListeners){
        view.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    gestureListeners.onDoubleTapped();
                    return super.onDoubleTap(e);
                }
            });
            ScaleGestureDetector pinchDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener(){

                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    //pinched in
                    if(detector.getScaleFactor()<0.9){
                        gestureListeners.onPinchIn();
                    }
                    return true;
                }

            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                pinchDetector.onTouchEvent(event);
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //Toast.makeText(context, "Down", Toast.LENGTH_SHORT).show();
                    break;
                    case MotionEvent.ACTION_MOVE:
                        change_X = event.getX()-current_X;
                        change_Y = event.getY()-current_Y;
                        if(Math.abs(change_Y) > Math.abs(change_X)){
                            gestureListeners.onVerticalSwipe(change_Y);
                        }
                    break;
                    case MotionEvent.ACTION_UP:
                        //Toast.makeText(context, "Up", Toast.LENGTH_SHORT).show();
                    break;
                }
                current_X = event.getX();
                current_Y = event.getY();
                return true;
            }
        });
    }
    interface MyGestureListeners{
         void onVerticalSwipe(float change);
         void onDoubleTapped();
         void onPinchIn();
    }
}
