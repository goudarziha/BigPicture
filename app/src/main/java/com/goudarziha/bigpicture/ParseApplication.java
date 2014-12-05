package com.goudarziha.bigpicture;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "ktgi5w5MN4urEsX8DqjZPtSagPzfOwFE75ejbisS", "5TqezMIkv08SKpLmwIBGCBiCoBGwSbqr18G5v81l");

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }
}
