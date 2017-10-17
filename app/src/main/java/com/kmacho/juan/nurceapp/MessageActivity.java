package com.kmacho.juan.nurceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.widget.Toast;

import com.kmacho.juan.nurceapp.Recyclers.AdapterMensajes;
import com.kmacho.juan.nurceapp.Recyclers.MensajesList;
import com.kmacho.juan.nurceapp.entities.MenssageResponse;
import com.kmacho.juan.nurceapp.entities.infoResponse;
import com.kmacho.juan.nurceapp.entities.respuestasData;
import com.kmacho.juan.nurceapp.network.ApiService;
import com.kmacho.juan.nurceapp.network.RetrofitBuilder;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    String idChat;
    String to_id;
    public ProgressDialog progressDialog,progressDialog2;


    ApiService service;
    Call<respuestasData> callData;

    @BindView(R.id.mensaje)
    TextInputEditText mensajeInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        idChat = bundle.get("id_chat").toString();
        to_id = bundle.get("to_id_user").toString();

        Toast.makeText(this, "get "+idChat+" "+to_id, Toast.LENGTH_SHORT).show();

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));
        idUserPreferences = IdUserPreferences.getInstance(getSharedPreferences("Contex",MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
        mensajesLists = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerMesage);
        recyclerView.setLayoutManager(layoutManager);
        loadRecyclerViewData();
    }
@OnClick(R.id.btn_send)
public void sendMensaje(){
    //Toast.makeText(this, "Mensaje "+mensajeInput.getText()+" id_chat "+idChat+" To_idChat "+to_id, Toast.LENGTH_SHORT).show();
    progressDialog2 = new ProgressDialog(this);
    progressDialog2.setMessage("Cargando informacion");
    progressDialog2.show();
    callData = service.sendMensaje(idChat,to_id,mensajeInput.getText().toString());
    callData.enqueue(new Callback<respuestasData>() {
        @Override
        public void onResponse(Call<respuestasData> call, Response<respuestasData> response) {
            if (response.isSuccessful()){
                loadRecyclerViewData();
                mensajeInput.setText("");
                progressDialog2.dismiss();
            }else{
                System.out.println("Error "+response);

            }
        }

        @Override
        public void onFailure(Call<respuestasData> call, Throwable t) {
            System.out.println("ERROR "+t.getMessage());
        }
    });
}


    public void loadRecyclerViewData(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando informacion");
        progressDialog.show();
        call = service.mensajesPersonal(Integer.parseInt(idChat));
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
                        String mensaje = Html.fromHtml(response.body().getData().get(i).getMensaje()).toString();
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
