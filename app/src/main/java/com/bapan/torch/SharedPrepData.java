package com.bapan.torch;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrepData {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPrepData(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("myData",context.MODE_PRIVATE);
        editor = sp.edit();
    }
    public void setSideType(boolean type){
        editor.putBoolean("torchTypeKey",type);
        editor.apply();
    }
    public boolean getSideType(){
        return sp.getBoolean("torchTypeKey",true);
    }
    
    public void setGuideInt(int times){
        editor.putInt("guideIntKey",times);
        editor.apply();
    }
    public int getGuideInt(){
    return sp.getInt("guideIntKey",5);
    }

    
    public void setNotificationType(boolean type){
        editor.putBoolean("NotificationTypeKey",type);
        editor.apply();
    }
    public boolean getNotificationType(){
        return sp.getBoolean("NotificationTypeKey",false);
    }
    public void setSwitchSound(boolean type){
        editor.putBoolean("switchSound",type);
        editor.apply();
    }
    public boolean getSwitchSound(){
        return sp.getBoolean("switchSound",true);
    }
}
