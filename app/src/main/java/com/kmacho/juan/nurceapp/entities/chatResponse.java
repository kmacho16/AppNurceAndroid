package com.kmacho.juan.nurceapp.entities;

import com.kmacho.juan.nurceapp.Recyclers.ChatList;

import java.util.List;

/**
 * Created by Videos on 21/08/2017.
 */

public class chatResponse {
    List<ChatList> data;

    public List<ChatList> getData() {
        return data;
    }

    public void setData(List<ChatList> data) {
        this.data = data;
    }
}
