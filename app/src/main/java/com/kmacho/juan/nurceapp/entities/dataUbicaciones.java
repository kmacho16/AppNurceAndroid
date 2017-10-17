package com.kmacho.juan.nurceapp.entities;

/**
 * Created by Videos on 27/08/2017.
 */

public class dataUbicaciones {
    String id,id_user,nombre;
    double latitud,longitud;

    public dataUbicaciones(String id, String id_user, double latitud, double longitud, String nombre) {
        this.id = id;
        this.id_user = id_user;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
