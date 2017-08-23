package com.kmacho.juan.nurceapp.Recyclers;

/**
 * Created by Videos on 21/08/2017.
 */

public class ChatList {
    String from_nombre,to_nombre,created_at,mensaje,from_img,to_img;
    int id_chat,id_user,to_id_user;

    public ChatList(String from_nombre, String to_nombre, String created_at, String mensaje, String from_img, String to_img, int id_chat, int id_user, int to_id_user) {
        this.from_nombre = from_nombre;
        this.to_nombre = to_nombre;
        this.created_at = created_at;
        this.mensaje = mensaje;
        this.from_img = from_img;
        this.to_img = to_img;
        this.id_chat = id_chat;
        this.id_user = id_user;
        this.to_id_user = to_id_user;
    }

    public String getFrom_nombre() {
        return from_nombre;
    }

    public void setFrom_nombre(String from_nombre) {
        this.from_nombre = from_nombre;
    }

    public String getTo_nombre() {
        return to_nombre;
    }

    public void setTo_nombre(String to_nombre) {
        this.to_nombre = to_nombre;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFrom_img() {
        return from_img;
    }

    public void setFrom_img(String from_img) {
        this.from_img = from_img;
    }

    public String getTo_img() {
        return to_img;
    }

    public void setTo_img(String to_img) {
        this.to_img = to_img;
    }

    public int getId_chat() {
        return id_chat;
    }

    public void setId_chat(int id_chat) {
        this.id_chat = id_chat;
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
