package com.kmacho.juan.nurceapp.entities;

import com.kmacho.juan.nurceapp.Recyclers.MensajesList;

import java.util.List;

/**
 * Created by Videos on 22/08/2017.
 */

public class MenssageResponse {
    List<MensajesList> data;

    public List<MensajesList> getData() {
        return data;
    }

    public void setData(List<MensajesList> data) {
        this.data = data;
    }
}
