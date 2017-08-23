package com.kmacho.juan.nurceapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.kmacho.juan.nurceapp.entities.AccessToken;
import com.kmacho.juan.nurceapp.entities.ApiError;
import com.kmacho.juan.nurceapp.entities.infoResponse;
import com.kmacho.juan.nurceapp.entities.respuestasData;
import com.kmacho.juan.nurceapp.network.ApiService;
import com.kmacho.juan.nurceapp.network.RetrofitBuilder;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProfile extends Fragment {

    private static final String TAG = "UpdateProfile";
    
    ApiService service;
    /*@BindView(R.id.nombre_user)
    TextView nomUser;*/
    TokenManager tokenManager;
    Call<infoResponse> call;

    Call<respuestasData> callAccess;
    

    @BindView(R.id.til_name)
    TextInputLayout tilName;
    @BindView(R.id.til_laste)
    TextInputLayout tilLast;
    @BindView(R.id.til_email)
    TextInputLayout tilEmail;
    @BindView(R.id.til_phone)
    TextInputLayout tilPhone;
    @BindView(R.id.til_password)
    TextInputLayout tillPassword;

    /****************************************/
    @BindView(R.id.nombreText)
    TextInputEditText nombreText;
    @BindView(R.id.apellidoText)
    TextInputEditText apellidoText;
    @BindView(R.id.telefonoText)
    TextInputEditText telefonoText;
    @BindView(R.id.emailText)
    TextInputEditText emailText;
    @BindView(R.id.passwordText)
    TextInputEditText passwordText;

    @BindView(R.id.imagen)
    ImageView miImagen;

    @BindView(R.id.btn_actualizar)
    Button btn_actualizar;


    AwesomeValidation validator;
    private static final int PICK_IMAGE = 100;
    private Bitmap bitmap;

    Uri imageUri = null;



    /********************************************/

    public UpdateProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_update_profile, container, false);
        ButterKnife.bind(this,v);
        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs",getContext().MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setUpRules();




        getDatos();

        return v;
    }

    void getDatos(){
        final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Cargando informacion");
        progressDialog.show();
        call = service.usuario();
        call.enqueue(new Callback<infoResponse>() {
            @Override
            public void onResponse(Call<infoResponse> call, Response<infoResponse> response) {
                Log.w(TAG, "onResponse: " + response.body());

                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    //tilName(response.body().getData().get(0).getName());
                    nombreText.setText(response.body().getData().get(0).getName());
                    apellidoText.setText(response.body().getData().get(0).getLast_name());
                    telefonoText.setText(response.body().getData().get(0).getTelefono());
                    emailText.setText(response.body().getData().get(0).getEmail());
                    // userFoto.setText(userFoto.getText() + response.body().getData().get(0).getFoto_perfil());
                    //Picasso.with(getContext()).load("http://192.168.0.24:443/uploads/"+response.body().getData().get(0).getFoto_perfil()).into(foto_perfil);


                } else {
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
    void sedData(){
        String name = tilName.getEditText().getText().toString();
        String last_name = tilLast.getEditText().getText().toString();
        String phone = tilPhone.getEditText().getText().toString();
        String email = tilEmail.getEditText().getText().toString();
        String password = tillPassword.getEditText().getText().toString();
        //String imagen = imageToString();

       /* File archivo =

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),archivo);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file",archivo.getName(),requestFile);*/




        tilName.setError(null);
        tilEmail.setError(null);
        tillPassword.setError(null);
        validator.clear();
        if(validator.validate()){
        callAccess = service.updateProfile(name,last_name,email,phone,password);
        callAccess.enqueue(new Callback<respuestasData>() {
            @Override
            public void onResponse(Call<respuestasData> call, Response<respuestasData> response) {
                if (response.isSuccessful()) {
                    Log.w(TAG, "onResponse1: " + response);
                    getActivity().setTitle("Primer Fragment");
                    ProfileFragment fragment = new ProfileFragment();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame,fragment,"Fragment 1");
                    fragmentTransaction.commit();
                } else {
                    Log.w(TAG, "onResponse2: "+response);
                    handleErrors(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<respuestasData> call, Throwable t) {

            }
        });
        }
    }

    @OnClick(R.id.imagen)
    public void SubirImagen(){
        Toast.makeText(getContext(), "SUBIR IMAGEN", Toast.LENGTH_SHORT).show();
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE){
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),imageUri);
                miImagen.setImageURI(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            Toast.makeText(getActivity(), "sin imagen seleccionada", Toast.LENGTH_SHORT).show();
        }
    }

    private String imageToString(){
        ByteArrayOutputStream byteArrayString = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayString);
        byte[] imgByte = byteArrayString.toByteArray();
        return Base64.encodeToString(imgByte,Base64.DEFAULT);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void setUpRules(){
        validator.addValidation(getActivity(),R.id.til_name, RegexTemplate.NOT_EMPTY,R.string.err_name);
        validator.addValidation(getActivity(),R.id.til_email, Patterns.EMAIL_ADDRESS,R.string.err_email);
        validator.addValidation(getActivity(),R.id.til_password,"[a-zA-Z0-9]{6}",R.string.err_password);
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


}
