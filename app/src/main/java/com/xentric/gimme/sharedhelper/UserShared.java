package com.xentric.gimme.sharedhelper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kousik on 15/3/16.
 */
public class UserShared {
    SharedPreferences userPref;
    Context context;
    SharedPreferences.Editor editor;

    private static final String PREFERENCE_NAME = "user_preference";

    private static final String SHARE_USER_LOGIN = "IsLoggedin";
    private static final String SHARE_USER_ID = "Id";
    private static final String SHARE_USER_USERNAME = "UserName";
    private static final String SHARE_USER_PASSWORD = "Password";
    private static final String SHARE_USER_EMAIL = "UserEmailAddress";
    private static final String SHARE_USER_FNAME = "FirstName";
    private static final String SHARE_USER_LNAME = "LastName";
    private static final String SHARE_USER_PHONENUMBER = "PhoneNumber";

    public UserShared(Context context) {
        this.context = context;
        userPref = context.getSharedPreferences(PREFERENCE_NAME,0);
    }

    //Set Login
    public void setLogin(String Id,String Username,String Password,String UserEmailAddress,String FirstName,String LastName,String PhoneNumber){
        SharedPreferences.Editor editor = userPref.edit();
        editor.putBoolean(SHARE_USER_LOGIN,true);
        editor.putString(SHARE_USER_ID,Id);
        editor.putString(SHARE_USER_USERNAME,Username);
        editor.putString(SHARE_USER_PASSWORD,Password);
        editor.putString(SHARE_USER_EMAIL,UserEmailAddress);
        editor.putString(SHARE_USER_FNAME,FirstName);
        editor.putString(SHARE_USER_LNAME,LastName);
        editor.putString(SHARE_USER_PHONENUMBER,PhoneNumber);
        editor.commit();
    }

    //Set Logout
    public void setLogout(){
        SharedPreferences.Editor editor = userPref.edit();
        editor.clear();
        editor.commit();
    }
    //Get User Full Name
    public String getUserFullName(){
        return userPref.getString(SHARE_USER_FNAME,"")+" "+ userPref.getString(SHARE_USER_LNAME,"");
    }
    //Get User Id
    public String getUserId(){
        return userPref.getString(SHARE_USER_ID,"");
    }

    //ISloggedin
    public boolean isLoggedin(){
        return userPref.getBoolean(SHARE_USER_LOGIN,false);
    }

}
