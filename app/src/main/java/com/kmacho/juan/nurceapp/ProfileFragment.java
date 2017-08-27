package com.kmacho.juan.nurceapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kmacho.juan.nurceapp.entities.infoResponse;
import com.kmacho.juan.nurceapp.network.ApiService;
import com.kmacho.juan.nurceapp.network.RetrofitBuilder;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    ApiService service;
    /*@BindView(R.id.nombre_user)
    TextView nomUser;*/

    TokenManager tokenManager;
    IdUserPreferences idUserPreferences;
    Call<infoResponse> call;

    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_lastname)
    TextView userLast;
    @BindView(R.id.user_telefono)
    TextView userTele;
    @BindView(R.id.user_email)
    TextView userMail;
    @BindView(R.id.foto_perfil)
    ImageView foto_perfil;

    @BindView(R.id.loadPage)
    LinearLayout loadPage;

    @BindView(R.id.layoutProfile)
    LinearLayout layoutProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this,v);

        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs",getContext().MODE_PRIVATE));
        idUserPreferences = IdUserPreferences.getInstance(this.getActivity().getSharedPreferences("Contex",getContext().MODE_PRIVATE));
        //Toast.makeText(getActivity(), "AHA "+idUserPreferences.getId(), Toast.LENGTH_SHORT).show();
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
        getPosts();

        return v;
    }

    void getPosts(){
        /*final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Cargando informacion");
        progressDialog.show();*/


        call = service.usuario();
        call.enqueue(new Callback<infoResponse>() {
            @Override
            public void onResponse(Call<infoResponse> call, Response<infoResponse> response) {
                Log.w(TAG, "onResponse: " + response.body());

                if (response.isSuccessful()) {
                    //progressDialog.dismiss();
                    loadPage.setVisibility(View.GONE);
                    layoutProfile.setVisibility(View.VISIBLE);
                    userName.setText(response.body().getData().get(0).getName());
                    userLast.setText(response.body().getData().get(0).getLast_name());
                    userTele.setText(response.body().getData().get(0).getTelefono());
                    userMail.setText(response.body().getData().get(0).getEmail());

                    TextView name = (TextView) getActivity().findViewById(R.id.my_name);
                    TextView email = (TextView) getActivity().findViewById(R.id.my_email);
                    ImageView foto = (ImageView) getActivity().findViewById(R.id.my_photo);
                   // userFoto.setText(userFoto.getText() + response.body().getData().get(0).getFoto_perfil());

                    name.setText(response.body().getData().get(0).getName());
                    email.setText(response.body().getData().get(0).getEmail());


                    Picasso.with(getContext()).load("http://app-nurce-hero.herokuapp.com/uploads/"+response.body().getData().get(0).getFoto_perfil()).memoryPolicy(MemoryPolicy.NO_CACHE).into(foto_perfil);
                    Picasso.with(getContext()).load("http://app-nurce-hero.herokuapp.com/uploads/"+response.body().getData().get(0).getFoto_perfil()).memoryPolicy(MemoryPolicy.NO_CACHE).into(foto);

                } else {
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), Login.class));
                    tokenManager.deleteToken();

                }
            }

            @Override
            public void onFailure(Call<infoResponse> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.btn_actualizar)
    void actualizar(){
        getActivity().setTitle("Segundo Fragment");
        UpdateProfile fragment = new UpdateProfile();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment,"Fragment 2");
        fragmentTransaction.commit();
    }

}
