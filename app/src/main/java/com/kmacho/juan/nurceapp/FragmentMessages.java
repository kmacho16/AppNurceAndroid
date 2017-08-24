package com.kmacho.juan.nurceapp;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kmacho.juan.nurceapp.Recyclers.AdapterChat;
import com.kmacho.juan.nurceapp.Recyclers.ChatList;
import com.kmacho.juan.nurceapp.entities.chatResponse;
import com.kmacho.juan.nurceapp.network.ApiService;
import com.kmacho.juan.nurceapp.network.RetrofitBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentMessages extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ChatList> chatLists;
    Call<chatResponse> call;
    TokenManager tokenManager;
    IdUserPreferences idUserPreferences;
    ApiService service;
    String nombre;
    String foto_perfil;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_messages, container, false);
        ButterKnife.bind(this,v);

        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs",getContext().MODE_PRIVATE));
        idUserPreferences = IdUserPreferences.getInstance(this.getActivity().getSharedPreferences("Contex",getContext().MODE_PRIVATE));

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerMessage);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        chatLists = new ArrayList<>();
        loadChatRecycler();

        return v;
    }

    public void loadChatRecycler(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Cargando informacion");
        progressDialog.show();
        call = service.chatAll();
        call.enqueue(new Callback<chatResponse>() {
            @Override
            public void onResponse(Call<chatResponse> call, Response<chatResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    System.out.println("RESULTADO TAL "+response);
                    for(int i = 0; i<response.body().getData().size();i++){
                        if (idUserPreferences.getId()==response.body().getData().get(i).getTo_id_user()){
                            nombre = response.body().getData().get(i).getFrom_nombre();
                            foto_perfil = response.body().getData().get(i).getFrom_img();
                        }else{
                            nombre = response.body().getData().get(i).getTo_nombre();
                            foto_perfil = response.body().getData().get(i).getTo_img();
                        }

                       /* if(idUserPreferences.getId()==response.body().getData().get(i).getTo_id_user()){

                        }*/

                        ChatList item = new ChatList(nombre,response.body().getData().get(i).getTo_nombre(), response.body().getData().get(i).getCreated_at(),response.body().getData().get(i).getMensaje(),foto_perfil,"to_img",response.body().getData().get(i).getId_chat(),response.body().getData().get(i).getId_user(),response.body().getData().get(i).getTo_id_user());


                        chatLists.add(item);
                        adapter = new AdapterChat(chatLists,getActivity());
                        //recyclerView.scrollToPosition(chatLists.size()-1);
                        recyclerView.setAdapter(adapter);
                    }

                }else{
                    progressDialog.dismiss();
                    System.out.println("ERROR PEQUE "+response);
                }
            }

            @Override
            public void onFailure(Call<chatResponse> call, Throwable t) {
                System.out.println("ERROR  GRANDE");
            }
        });


        //adapter = new AdapterMensajes(mensajesLists,MessageActivity.this);

    }

}
