package com.kmacho.juan.nurceapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kmacho.juan.nurceapp.entities.infoResponse;
import com.kmacho.juan.nurceapp.network.ApiService;
import com.kmacho.juan.nurceapp.network.RetrofitBuilder;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kmacho.juan.nurceapp.R.id.foto_perfil;

public class profile_user extends AppCompatActivity {
    TokenManager tokenManager;
    Call<infoResponse> call;
    ApiService service;
    IdUserPreferences idUserPreferences;
    Dialog settings;
    String foto_;

    @BindView(R.id.user_name)
    TextView userName;

    @BindView(R.id.user_email)
    TextView userMail;

    @BindView(R.id.user_lastname)
    TextView userLast;

    @BindView(R.id.user_telefono)
    TextView userTele;

    @BindView(R.id.foto_perfil)
    ImageView foto_perfil;
    String mid;

    @BindView(R.id.loadPage)
    LinearLayout loadPage;

    @BindView(R.id.showProfile)
    LinearLayout showProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        Intent intent = getIntent();
        mid = intent.getStringExtra("id");
        ButterKnife.bind(this);


        //Toast.makeText(this, "Este es el ID "+mid, Toast.LENGTH_LONG).show();
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));
        idUserPreferences = IdUserPreferences.getInstance(getSharedPreferences("Contex",MODE_PRIVATE));
        //Toast.makeText(this, "AHA "+idUserPreferences.getId(), Toast.LENGTH_SHORT).show();
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
        getPosts();
    }

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
@OnClick(R.id.card)
public void ShowIfo(){
    //Toast.makeText(this, "CLICK ON SHAPE", Toast.LENGTH_SHORT).show();
    settings = new Dialog(this);
    settings.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    LinearLayout lin = (LinearLayout) getLayoutInflater().inflate(R.layout.popup_layout,null);
    ImageView img = (ImageView) lin.findViewById(R.id.foto_perfilII);
    if(foto_!=null){
        Picasso.with(getApplicationContext()).load("http://app-nurce-hero.herokuapp.com/uploads/"+foto_).into(img);
    }
    settings.setContentView(lin);
    settings.show();
}


public void dismissListener(View v){
    settings.dismiss();
}


    void getPosts(){

        call = service.findProfile(Integer.parseInt(mid));
        call.enqueue(new Callback<infoResponse>() {
            public static final String TAG = "aqui" ;

            @Override
            public void onResponse(Call<infoResponse> call, Response<infoResponse> response) {
                Log.w(TAG, "onResponse: " + response.body());

                if (response.isSuccessful()) {
                    loadPage.setVisibility(View.GONE);
                    showProfile.setVisibility(View.VISIBLE);
                    userName.setText(response.body().getData().get(0).getName());
                    userLast.setText(response.body().getData().get(0).getLast_name());
                    userTele.setText(response.body().getData().get(0).getTelefono());
                    userMail.setText(response.body().getData().get(0).getEmail());
                    foto_=response.body().getData().get(0).getFoto_perfil();
                    // userFoto.setText(userFoto.getText() + response.body().getData().get(0).getFoto_perfil());
                    if(response.body().getData().get(0).getFoto_perfil()!=null){
                        Picasso.with(getApplicationContext()).load("http://app-nurce-hero.herokuapp.com/uploads/"+response.body().getData().get(0).getFoto_perfil()).into(foto_perfil);
                    }


                } else {
                    finish();
                    startActivity(new Intent(profile_user.this,Login.class));
                    tokenManager.deleteToken();

                }
            }

            @Override
            public void onFailure(Call<infoResponse> call, Throwable t) {

            }
        });
    }
}
