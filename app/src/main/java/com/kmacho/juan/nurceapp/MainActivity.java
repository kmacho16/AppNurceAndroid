package com.kmacho.juan.nurceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kmacho.juan.nurceapp.entities.datos;
import com.kmacho.juan.nurceapp.entities.infoResponse;
import com.kmacho.juan.nurceapp.network.ApiService;
import com.kmacho.juan.nurceapp.network.RetrofitBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    ApiService service;
    /*@BindView(R.id.nombre_user)
    TextView nomUser;*/

    TokenManager tokenManager;
    IdUserPreferences idUserPreferences;
    Call<infoResponse> call;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        String Token = FirebaseInstanceId.getInstance().getToken();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        


        System.out.println("El token "+ Token);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));
        idUserPreferences = IdUserPreferences.getInstance(getSharedPreferences("Contex",MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
        getPosts();

        setTitle("Fragment Maps");
        mapsFragment fragment = new mapsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment,"Fragment 2");
        fragmentTransaction.commit();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hideItem();
    }

    //AQUI SE OCULTAN LOS ICONOS
    private void hideItem()
    {
        Menu nav_Menu = navigationView.getMenu();
        //nav_Menu.findItem(R.id.event_manage).setVisible(false);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    void getPosts(){
    final ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Cargando informacion");
    progressDialog.show();
    call = service.usuario();
    call.enqueue(new Callback<infoResponse>() {
        @Override
        public void onResponse(Call<infoResponse> call, Response<infoResponse> response) {

            Log.w(TAG, "onResponse: " + response.body());

            if (response.isSuccessful()) {
                progressDialog.dismiss();
                TextView name = (TextView) findViewById(R.id.my_name);
                TextView email = (TextView) findViewById(R.id.my_email);
                ImageView foto = (ImageView) findViewById(R.id.my_photo);
                foto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTitle("Primer Fragment");
                        ProfileFragment fragment = new ProfileFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame,fragment,"Fragment 1");
                        fragmentTransaction.commit();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);

                    }
                });


                name.setText(response.body().getData().get(0).getName());
                email.setText(response.body().getData().get(0).getEmail());
                int id = response.body().getData().get(0).getId_rol();
                System.out.println("estos datos "+response.body().getData().get(0).getName()+" - "+response.body().getData().get(0).getId()+" - "+response.body().getData().get(0).getId_rol());

                idUserPreferences.saveId(response.body().getData().get(0).getId());
                //Toast.makeText(MainActivity.this, "AHA "+idUserPreferences.getId(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                //Picasso.with(getContext()).load("http://app-nurce-hero.herokuapp.com/uploads/"+response.body().getData().get(0).getFoto_perfil()).into(foto_perfil);
                Picasso.with(getApplicationContext()).load("http://app-nurce-hero.herokuapp.com/uploads/"+response.body().getData().get(0).getFoto_perfil()).into(foto);
            } else {
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
                tokenManager.deleteToken();

            }
        }

        @Override
        public void onFailure(Call<infoResponse> call, Throwable t) {
            Log.w(TAG, "onFailure: "+t.getMessage());
        }
    });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call!=null){
            call.cancel();
            call=null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            setTitle("Primer Fragment");
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame,fragment,"Fragment 1");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_search) {
            setTitle("Fragment Maps");
            mapsFragment fragment = new mapsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame,fragment,"Fragment maps");
            fragmentTransaction.commit();

        }else if(id == R.id.event_manage){
            setTitle("Fragment Chat");
            FragmentCalendario fragment = new FragmentCalendario();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame,fragment,"Fragment Eventos");
            fragmentTransaction.commit();

        }else if (id == R.id.nav_manage) {

            setTitle("Fragment Chat");
            FragmentMessages fragment = new FragmentMessages();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame,fragment,"Fragment chat");
            fragmentTransaction.commit();

            /*Intent intent = new Intent(this,MessageActivity.class);
            startActivity(intent);*/
        }else if(id == R.id.posiciones_manage) {
            setTitle("Fragment Ubicaciones");
            Fragmentubicaciones fragment = new Fragmentubicaciones();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame,fragment,"Fragment Ubicaciones");
            fragmentTransaction.commit();

        }else if (id == R.id.nav_logout) {
            tokenManager.deleteToken();
            finish();
            startActivity(new Intent(MainActivity.this, Login.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
