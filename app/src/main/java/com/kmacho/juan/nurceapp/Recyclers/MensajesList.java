package com.kmacho.juan.nurceapp.Recyclers;

/**
 * Created by Videos on 17/08/2017.
 */

public class MensajesList {
    int id,id_chat,id_user,to_id_user;
    int leido;
    String mensaje,created_at;

    public MensajesList(int id, int id_chat, int id_user, int to_id_user, int leido, String mensaje, String created_at) {
        this.id = id;
        this.id_chat = id_chat;
        this.id_user = id_user;
        this.to_id_user = to_id_user;
        this.leido = leido;
        this.mensaje = mensaje;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int isLeido() {
        return leido;
    }

    public void setLeido(int leido) {
        this.leido = leido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
