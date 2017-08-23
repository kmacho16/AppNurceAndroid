package com.kmacho.juan.nurceapp.entities;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Videos on 15/08/2017.
 */

public class FirebaseInstance extends FirebaseInstanceIdService {
    private static final String REG_TOKEN = "REG_TOKEN";
    @Override
    public void onTokenRefresh() {
        String recen_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN,recen_token);
    }
}
