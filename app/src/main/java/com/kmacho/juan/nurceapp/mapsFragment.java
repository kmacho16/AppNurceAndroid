package com.kmacho.juan.nurceapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kmacho.juan.nurceapp.entities.personalResponse;
import com.kmacho.juan.nurceapp.network.ApiService;
import com.kmacho.juan.nurceapp.network.RetrofitBuilder;
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
public class mapsFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View v;
    Circle circulo;
    int text_radio =1;
    Call<personalResponse> call;
    TokenManager tokenManager;
    ApiService service;
    private static final String TAG = "mapsFragment";

    Marker lastOpenned = null;

    @BindView(R.id.radio)
    TextView radio;

    private double lat,lng;


    public mapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this,v);
        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs",getContext().MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);


        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) v.findViewById(R.id.miMap);
        if (mMapView!=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

    }

    @OnClick(R.id.market)
    public void market(){

        //Toast.makeText(getActivity(), "Market Click "+text_radio, Toast.LENGTH_SHORT).show();
        if (circulo!=null){
            mGoogleMap.clear();
        }

        final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Cargando informacion");
        progressDialog.show();

        circulo = mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(lat,lng))
                .radius(text_radio * 1000)
                .strokeColor(Color.parseColor("#3291b1"))
                .fillColor(Color.parseColor("#5F2566ce"))
                .strokeWidth(10));

        call = service.findPersonal(lat,lng,text_radio);
        call.enqueue(new Callback<personalResponse>() {
            @Override
            public void onResponse(Call<personalResponse> call, Response<personalResponse> response) {
                BitmapDrawable bitmapdraw =(BitmapDrawable)getResources().getDrawable(R.mipmap.market_nurce) ;

                //tilName(response.body().getData().get(0).getName());
                //System.out.println("RESPUESTA NAME: "+response.body().getList());
                //nombreText.setText(response.body().getData().get(0).getName());
                // userFoto.setText(userFoto.getText() + response.body().getData().get(0).getFoto_perfil());
                //Picasso.with(getContext()).load("http://192.168.0.24:443/uploads/"+response.body().getData().get(0).getFoto_perfil()).into(foto_perfil);
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    for (int i=0; i<response.body().getData().size();i++){
                        //System.out.println("RESPUESTA NAME: "+response.body().getData().get(i).getName());
                        if(i==0){
                            bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.market_star);
                        }else{
                            bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.market_nurce);
                        }
                        double lat = response.body().getData().get(i).getLatitud();
                        double lng = response.body().getData().get(i).getLongitud();
                        int height = 140;
                        int width = 85;
                        Bitmap b = bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat,lng))
                                //.title("Titulo")
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                .snippet(response.body().getData().get(i).getId_user()+"%"+response.body().getData().get(i).getName()+"%"+response.body().getData().get(i).getDistancia()+"%"+response.body().getData().get(i).getImg_perfil())
                        );

                        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                Intent intent = new Intent(getActivity(),profile_user.class);
                                String[] datos = marker.getSnippet().split("%");
                                intent.putExtra("id",datos[0]);
                                startActivity(intent);
                            }
                        });

                        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                View v = getActivity().getLayoutInflater().inflate(R.layout.show_info,null);
                                TextView nombre = (TextView) v.findViewById(R.id.nombreText);
                                TextView distancia = (TextView) v.findViewById(R.id.dista);
                                //TextView perfil = (TextView) v.findViewById(R.id.perfil);
                                ImageView foto_perfil = (ImageView) v.findViewById(R.id.foto_perfil);
                                RelativeLayout linear = (RelativeLayout) v.findViewById(R.id.linear);
                                String[] datos = marker.getSnippet().split("%");
                                nombre.setText(datos[1]);
                                distancia.setText(datos[2].substring(0,4)+"Km");
                                //perfil.setText(datos[3]);

                                if(!datos[3].equals("null")){
                                    Picasso.with(getContext()).load("http://app-nurce-hero.herokuapp.com/uploads/"+datos[3]).into(foto_perfil);
                                    System.out.println(" "+datos[3]);
                                }



                                return v;
                            }
                        });

                        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                // Check if there is an open info window
                                if (lastOpenned != null) {
                                    // Close the info window
                                    lastOpenned.hideInfoWindow();

                                    // Is the marker the same marker that was already open
                                    if (lastOpenned.equals(marker)) {
                                        // Nullify the lastOpenned object
                                        lastOpenned = null;
                                        // Return so that the info window isn't openned again
                                        return true;
                                    }
                                }

                                // Open the info window for the marker
                                marker.showInfoWindow();
                                // Re-assign the last openned such that we can close it later
                                lastOpenned = marker;

                                // Event was handled by our code do not launch default behaviour.
                                return true;
                            }
                        });
                    }
                    Log.w(TAG, "onResponse1: " + response);
                } else {

                    getActivity().finish();
                    startActivity(new Intent(getActivity(), Login.class));
                    tokenManager.deleteToken();
                    Log.w(TAG, "onResponse2: "+response);
                    //handleErrors(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<personalResponse> call, Throwable t) {
                System.out.println("HAY UN ERROR: "+t.getMessage());

            }
        });


    }

    @OnClick(R.id.suma)
    public void sumar(){
        text_radio = Integer.parseInt(radio.getText().toString());
        if (text_radio>=20){
            text_radio = 1;
            radio.setText("1");
        }else{
            text_radio++;
            radio.setText(""+text_radio);
        }
    }

    @OnClick(R.id.resta)
    public void restar(){
        text_radio = Integer.parseInt(radio.getText().toString());
        if (text_radio<=1){
            text_radio = 20;
            radio.setText("20");
        }else{
            text_radio--;
            radio.setText(""+text_radio);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap= googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng casa = new LatLng(4.683427453930319,-74.09130120985105);
        CameraPosition usme = CameraPosition.builder().target(new LatLng(4.683427453930319,-74.09130120985105)).zoom(14).bearing(0).tilt(45).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(usme));
        /*circulo = mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(4.4734284,-74.1181233))
                .radius(2000)
                .strokeColor(Color.parseColor("#3291b1"))
                .fillColor(Color.parseColor("#5F2566ce"))
                .strokeWidth(10));*/

        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                /*Circle circulo = mGoogleMap.addCircle(new CircleOptions()
                        .center(new LatLng(cameraPosition.target.latitude,cameraPosition.target.longitude))
                        .radius(2000)
                        .strokeColor(Color.parseColor("#3291b1"))
                        .fillColor(Color.parseColor("#5F2566ce"))
                        .strokeWidth(10));*/
                lat = cameraPosition.target.latitude;
                lng = cameraPosition.target.longitude;
            }
        });




    }
}
