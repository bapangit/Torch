package com.bapan.torch;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class SharedPrepData {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPrepData(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("myData",context.MODE_PRIVATE);
        editor = sp.edit();
    }
    public void setTorchType(boolean type){
        editor.putBoolean("torchTypeKey",type);
        editor.apply();
    }
    public boolean getTorchType(){
        return sp.getBoolean("torchTypeKey",true);
    }
    
    public void setGuideStatus(Boolean status){
        editor.putBoolean("guideKey",status);
        editor.apply();
    }
    public boolean getGuideStatus(){
    return sp.getBoolean("guideKey",true);
    }
    
    public void setGuideInt(int times){
        editor.putInt("guideIntKey",times);
        editor.apply();
    }
    public int getGuideInt(){
    return sp.getInt("guideIntKey",0);
    }

    public void setAdWaitTimes(int times){
        editor.putInt("adWaitKey",times);
        editor.apply();
    }
    public int getAdWaitTimes(){
        return sp.getInt("adWaitKey",32);
    }
}
