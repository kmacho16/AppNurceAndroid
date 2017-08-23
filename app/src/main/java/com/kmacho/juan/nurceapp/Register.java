package com.kmacho.juan.nurceapp;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.common.base.Converter;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kmacho.juan.nurceapp.entities.AccessToken;
import com.kmacho.juan.nurceapp.entities.ApiError;
import com.kmacho.juan.nurceapp.network.ApiService;
import com.kmacho.juan.nurceapp.network.RetrofitBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.til_name)
    TextInputLayout tilName;
    @BindView(R.id.til_email)
    TextInputLayout tilEmail;
    @BindView(R.id.til_password)
    TextInputLayout tillPassword;

    ApiService service;
    Call<AccessToken> call;
    AwesomeValidation validator;
    TokenManager tokenManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));
        setUpRules();

        if(tokenManager.getToken().getAccessToken()!=null){
            startActivity(new Intent(Register.this,MainActivity.class));
            finish();
        }
    }

    @OnClick(R.id.btn_register)
    void register(){
        String name = tilName.getEditText().getText().toString();
        String email = tilEmail.getEditText().getText().toString();
        String password = tillPassword.getEditText().getText().toString();
        String token_firebase = FirebaseInstanceId.getInstance().getToken();

        tilName.setError(null);
        tilEmail.setError(null);
        tillPassword.setError(null);
        validator.clear();

        if(validator.validate()) {
            call = service.register(name, email, password,token_firebase);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                    if (response.isSuccessful()) {
                        Log.w(TAG, "onResponse1: " + response);
                        tokenManager.saveToken(response.body());
                        startActivity(new Intent(Register.this,MainActivity.class));
                        finish();
                    } else {

                        //Log.w(TAG, "onResponse2: "+response);
                        handleErrors(response.errorBody());


                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w(TAG, "ALGO MAL: " + t.getMessage());

                }
            });
        }

    }

    @OnClick(R.id.go_to_login)
    void go_Login(){
        startActivity(new Intent(Register.this,Login.class));
    }

    private void handleErrors(ResponseBody response){
        ApiError apiError = Utils.converErrors(response);
        for (Map.Entry<String,List<String>>error:apiError.getErrors().entrySet()){
            if(error.getKey().equals("name")){
                tilName.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("email")){
                tilEmail.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("password")){
                tillPassword.setError(error.getValue().get(0));
            }
        }
    }

    public void setUpRules(){
        validator.addValidation(this,R.id.til_name, RegexTemplate.NOT_EMPTY,R.string.err_name);
        validator.addValidation(this,R.id.til_email, Patterns.EMAIL_ADDRESS,R.string.err_email);
        validator.addValidation(this,R.id.til_password,"[a-zA-Z0-9]{6}",R.string.err_password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call!=null) {
            call.cancel();
            call=null;
        }

    }
}
