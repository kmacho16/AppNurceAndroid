package com.kmacho.juan.nurceapp.entities;

import com.squareup.moshi.Json;

/**
 * Created by Videos on 29/07/2017.
 */

public class respuestasData {
    @Json(name="respuesta")
    String respuesta;
    @Json(name="state")
    String state;

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
