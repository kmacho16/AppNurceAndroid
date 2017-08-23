package com.kmacho.juan.nurceapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kmacho.juan.nurceapp.Recyclers.AdapterMensajes;
import com.kmacho.juan.nurceapp.Recyclers.MensajesList;
import com.kmacho.juan.nurceapp.entities.MenssageResponse;
import com.kmacho.juan.nurceapp.network.ApiService;
import com.kmacho.juan.nurceapp.network.RetrofitBuilder;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    TokenManager tokenManager;
    IdUserPreferences idUserPreferences;
    Call<MenssageResponse> call;
    List<MensajesList> mensajesLists;
    ApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));
        idUserPreferences = IdUserPreferences.getInstance(getSharedPreferences("Contex",MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
        mensajesLists = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerMesage);
        recyclerView.setLayoutManager(layoutManager);
        loadRecyclerViewData();
    }

    public void loadRecyclerViewData(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando informacion");
        progressDialog.show();
        call = service.mensajesPersonal(1);
        call.enqueue(new Callback<MenssageResponse>() {
            @Override
            public void onResponse(Call<MenssageResponse> call, Response<MenssageResponse> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    for (int i=response.body().getData().size()-1; i>=0;i--){
                        System.out.println(response);
                        int id = response.body().getData().get(i).getId();
                        int id_chat = response.body().getData().get(i).getId_chat();
                        int id_user = response.body().getData().get(i).getId_user();
                        int to_id_user = response.body().getData().get(i).getTo_id_user();
                        int leido = response.body().getData().get(i).isLeido();
                        String mensaje = response.body().getData().get(i).getMensaje();
                        String fecha = response.body().getData().get(i).getCreated_at();
                        MensajesList item = new MensajesList(id,id_chat,id_user,to_id_user,leido,mensaje,fecha);
                        mensajesLists.add(item);
                        adapter = new AdapterMensajes(mensajesLists,MessageActivity.this);
                        recyclerView.scrollToPosition(mensajesLists.size()-1);
                        recyclerView.setAdapter(adapter);
                    }
                }else{
                    progressDialog.dismiss();
                    System.out.println("ERROR "+response);

                };
            }

            @Override
            public void onFailure(Call<MenssageResponse> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("ERROR GRANDE "+t.getMessage());

            }
        });





        }
        //adapter = new AdapterMensajes(mensajesLists,MessageActivity.this);

    }
