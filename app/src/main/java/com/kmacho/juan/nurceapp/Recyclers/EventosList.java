package com.kmacho.juan.nurceapp.Recyclers;

/**
 * Created by Videos on 30/08/2017.
 */

public class EventosList {
    String nombre_evento, descripcion,color,fecha_inicio,fecha_fin;
    int id,id_user, to_id_user;

    public EventosList(String nombre_evento, String descripcion, String color, String fecha_inicio, String fecha_fin, int id, int id_user, int to_id_user) {
        this.nombre_evento = nombre_evento;
        this.descripcion = descripcion;
        this.color = color;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.id = id;
        this.id_user = id_user;
        this.to_id_user = to_id_user;
    }

    public String getNombre_evento() {
        return nombre_evento;
    }

    public void setNombre_evento(String nombre_evento) {
        this.nombre_evento = nombre_evento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getTo_id_user() {
        return to_id_user;
    }

    public void setTo_id_user(int to_id_user) {
        this.to_id_user = to_id_user;
    }
}
