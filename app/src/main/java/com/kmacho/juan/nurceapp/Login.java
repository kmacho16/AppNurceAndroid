package com.kmacho.juan.nurceapp;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kmacho.juan.nurceapp.entities.AccessToken;
import com.kmacho.juan.nurceapp.entities.ApiError;
import com.kmacho.juan.nurceapp.network.ApiService;
import com.kmacho.juan.nurceapp.network.RetrofitBuilder;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    @BindView(R.id.til_email)
    TextInputLayout tilEmail;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.loader)
    ProgressBar loader;
    @BindView(R.id.btn_login)
    Button btn;

    ApiService service;
    TokenManager tokenManager;

    AwesomeValidation validator;

    Call<AccessToken> call;

    private static final String TAG = "Login";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setUpRules();

        if(tokenManager.getToken().getAccessToken()!=null){
            startActivity(new Intent(Login.this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
    }

    @OnClick(R.id.btn_login)
    void login(){

        loader.setVisibility(View.VISIBLE);
        btn.setEnabled(false);
        tilEmail.setError(null);
        tilPassword.setError(null);
        validator.clear();

        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();
        String token_firebase = FirebaseInstanceId.getInstance().getToken();
        if (validator.validate()) {
            call = service.login(email, password,token_firebase);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    Log.w(TAG, "onResponse: " + response);
                    if (response.isSuccessful()) {
                        tokenManager.saveToken(response.body());
                        startActivity(new Intent(Login.this,MainActivity.class));
                        finish();
                    } else {
                        if (response.code() == 422) {
                            handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = Utils.converErrors(response.errorBody());
                            Toast.makeText(Login.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                            loader.setVisibility(View.GONE);
                            btn.setEnabled(true);
                        }
                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());
                }
            });
        }else {
            loader.setVisibility(View.GONE);
            btn.setEnabled(true);
        }

    }

    @OnClick(R.id.go_to_register)
    void go_register(){
        startActivity(new Intent(Login.this,Register.class));
    }

    private void handleErrors(ResponseBody response){
        ApiError apiError = Utils.converErrors(response);
        for (Map.Entry<String,List<String>>error:apiError.getErrors().entrySet()){
            if(error.getKey().equals("username")){
                tilEmail.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("password")){
                tilPassword.setError(error.getValue().get(0));
            }
        }
        loader.setVisibility(View.GONE);
        btn.setEnabled(true);
    }

    public void setUpRules(){
        validator.addValidation(this,R.id.til_email, Patterns.EMAIL_ADDRESS,R.string.err_email);
        validator.addValidation(this,R.id.til_password,RegexTemplate.NOT_EMPTY,R.string.err_password);
    }


}
