package my.edu.tarc.madassignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by SX on 2018-01-07.
 */

public class SaveSharedPreferences {

    static final String PREF_USER_NAME= "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_LOGIN_TYPE = "type";
    private static final String PREF_USER_ID = "id";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserID(Context context, String id){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USER_ID, id);
        editor.commit();
    }
    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static void setPassword(Context context, String password){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_PASSWORD, password);
        editor.commit();
    }
    public static void setLoginType(Context context, String type){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_LOGIN_TYPE, type);
        editor.commit();
    }

    public static String getPrefUserId(Context context){
        return getSharedPreferences(context).getString(PREF_USER_ID, "");
    }
    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }
    public static String getPrefPassword(Context context){
        return getSharedPreferences(context).getString(PREF_PASSWORD, "");
    }
    public static String getPrefLoginType(Context context){
        return getSharedPreferences(context).getString(PREF_LOGIN_TYPE, "");
    }
    public static void clear(Context context)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }

}
