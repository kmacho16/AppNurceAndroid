package com.kmacho.juan.nurceapp.network;


import com.kmacho.juan.nurceapp.Recyclers.ChatList;
import com.kmacho.juan.nurceapp.entities.AccessToken;
import com.kmacho.juan.nurceapp.entities.MenssageResponse;
import com.kmacho.juan.nurceapp.entities.UbicacionesResponse;
import com.kmacho.juan.nurceapp.entities.chatResponse;
import com.kmacho.juan.nurceapp.entities.datos;
import com.kmacho.juan.nurceapp.entities.eventosResponse;
import com.kmacho.juan.nurceapp.entities.infoResponse;
import com.kmacho.juan.nurceapp.entities.personalResponse;
import com.kmacho.juan.nurceapp.entities.respuestasData;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Videos on 22/07/2017.
 */

public interface ApiService {

    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("name") String name, @Field("email")String email, @Field("password")String password,@Field("token_firebase")String token_firebase);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username,@Field("password")String password,@Field("token_firebase")String token_firebase);

    @GET("usuario")
    Call<infoResponse> usuario();

    @POST("usuario/editar")
    @FormUrlEncoded
    Call<respuestasData> updateProfile(@Field("name") String name, @Field("last_name") String last_name, @Field("email")String email, @Field("telefono") String telefono, @Field("password") String password,@Field("foto_perfil") String imagen);

    @POST("personal/find")
    @FormUrlEncoded
    Call<personalResponse> findPersonal(@Field("lat") double lat, @Field("lng") double lng, @Field("radio") int radio);

    @POST("personal/profile")
    @FormUrlEncoded
    Call<infoResponse> findProfile(@Field("id_profile") int id);

    @GET("chat/all")
    Call<chatResponse> chatAll();

    @GET("ubicaciones/personal")
    Call<UbicacionesResponse> ubicacionesAll();

    @POST("chat/personal")
    @FormUrlEncoded
    Call<MenssageResponse> mensajesPersonal(@Field("id") int id_chat);

    @POST("chat/mensaje")
    @FormUrlEncoded
    Call<respuestasData> sendMensaje(@Field("id_chat") String id_chat, @Field("to_id_user") String to_id_user, @Field("mensaje")String mensaje);

    @POST("ubicaciones/store")
    @FormUrlEncoded
    Call<respuestasData> ubicacionesStore(@Field("nombre") String name,@Field("latitud") double lat, @Field("longitud") double lng);

    @POST("ubicaciones/delete")
    @FormUrlEncoded
    Call<respuestasData> ubicacionesDelete(@Field("id") String name);

    @GET("eventos/personal")
    Call<eventosResponse> eventosAll();

    @POST("eventos/day")
    @FormUrlEncoded
    Call<eventosResponse> eventosDay(@Field("fecha") String fecha);

}
