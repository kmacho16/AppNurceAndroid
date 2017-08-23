package com.kmacho.juan.nurceapp;

import android.content.SharedPreferences;

import com.kmacho.juan.nurceapp.entities.AccessToken;

/**
 * Created by Videos on 21/08/2017.
 */

public class IdUserPreferences {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static IdUserPreferences INSTANCE = null;

    private IdUserPreferences(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    static synchronized IdUserPreferences getInstance(SharedPreferences prefs){
        if (INSTANCE==null){
            INSTANCE = new IdUserPreferences(prefs);
        }
        return INSTANCE;
    }

    void saveId(int id_user){
        editor.putInt("ID_USER",id_user);
        editor.commit();
    }

    int getId(){
        return prefs.getInt("ID_USER",0);
    }



}
