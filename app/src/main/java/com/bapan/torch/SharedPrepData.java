package com.bapan.torch;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class SharedPrepData {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context context;
    //Keys
   private String guideKey = "guide";
   private String guideIntKey = "intKey";

    public SharedPrepData(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("myData",context.MODE_PRIVATE);
        editor = sp.edit();
    }
    public void setGuideStatus(Boolean status){
        editor.putBoolean("myKey",status);
        editor.apply();
    }
    public boolean getGuideStatus(){
    return sp.getBoolean("myKey",true);
    }
    
    public void setGuideInt(int times){
        editor.putInt("myTimesKey",times);
        editor.apply();
    }
    public int getGuideInt(){
    return sp.getInt("myTimesKey",0);
    }
}
