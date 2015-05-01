package com.example.bharathramh.xplore;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bharathramh on 4/12/15.
 */
public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "wsxBRCY3S4jRe0zF1oI3caziMInrpwqA2sQ1Jtsp", "uiMVpEFeEWq7zTHEprAySuYctEzRbsNsCLkFvs2N");
        ParseFacebookUtils.initialize(this);
        ParseUser.enableRevocableSessionInBackground();
        printHaskKey();
    }

    public void printHaskKey(){

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.bharathramh.xplore",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

}
