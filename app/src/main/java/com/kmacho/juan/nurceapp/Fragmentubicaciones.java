package com.kmacho.juan.nurceapp;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kmacho.juan.nurceapp.entities.UbicacionesResponse;
import com.kmacho.juan.nurceapp.entities.personalResponse;
import com.kmacho.juan.nurceapp.entities.respuestasData;
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
public class Fragmentubicaciones extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View v;
    Call<UbicacionesResponse> call;
    Call<respuestasData> callResponse;
    TokenManager tokenManager;
    ApiService service;
    Dialog settings;
    String id_ubicacion;
    private static final String TAG = "mapsFragment";

    Marker lastOpenned = null;

    @BindView(R.id.addUbicacion)
    LinearLayout addUbicacion;

    @BindView(R.id.guardarUbicacion)
    LinearLayout guardarUbicacion;

    @BindView(R.id.close)
    TextView btnClose;

    @BindView(R.id.market)
    ImageView market;

    private double lat,lng;



    public Fragmentubicaciones() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_ubicaciones, container, false);
        ButterKnife.bind(this,v);
        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs",getContext().MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
        ubicacionesPersonal();


        return v;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) v.findViewById(R.id.map_ubicaciones);
        if (mMapView!=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    public void ubicacionesPersonal(){
        btnClose.setVisibility(View.GONE);
        market.setVisibility(View.GONE);
        addUbicacion.setVisibility(View.VISIBLE);
        guardarUbicacion.setVisibility(View.GONE);

        if (mGoogleMap!=null){
            mGoogleMap.clear();
        }
        final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Cargando informacion");
        progressDialog.show();
        call = service.ubicacionesAll();
        call.enqueue(new Callback<UbicacionesResponse>() {
            @Override
            public void onResponse(final Call<UbicacionesResponse> call, final Response<UbicacionesResponse> response) {
                BitmapDrawable bitmapdraw =(BitmapDrawable)getResources().getDrawable(R.mipmap.market_nurce) ;
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    for (int i=0; i<response.body().getData().size();i++){
                        double lat = response.body().getData().get(i).getLatitud();
                        double lng = response.body().getData().get(i).getLongitud();
                        int height = 140;
                        int width = 85;
                        Bitmap b = bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        //System.out.println("bodyResponse ");
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat,lng))
                                //.title("Titulo")
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                .snippet(response.body().getData().get(i).getId()+"%"+response.body().getData().get(i).getNombre())
                        );

                        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                String[] datos = marker.getSnippet().split("%");
                                //Toast.makeText(getContext(), "Id "+ datos[0], Toast.LENGTH_SHORT).show();
                                id_ubicacion=datos[0];
                                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                alert.setTitle("Borrar");
                                alert.setMessage("¿Segugo que quieres Eliminar esta ubicacion?");

                                alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(getContext(), "CLICK Si", Toast.LENGTH_SHORT).show();
                                        callResponse = service.ubicacionesDelete(id_ubicacion);
                                        callResponse.enqueue(new Callback<respuestasData>() {
                                            @Override
                                            public void onResponse(Call<respuestasData> call, Response<respuestasData> response) {
                                                if (response.isSuccessful()){
                                                    Toast.makeText(getContext(), "Ubicacion eliminada", Toast.LENGTH_SHORT).show();
                                                    ubicacionesPersonal();
                                                }else{
                                                    System.out.println("respuesta "+response);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<respuestasData> call, Throwable t) {
                                                System.out.println("respuesta MAL");
                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                });

                                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alert.show();
                            }
                        });

                        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                View v = getActivity().getLayoutInflater().inflate(R.layout.show_info_ubicacion,null);
                                TextView nombre = (TextView) v.findViewById(R.id.nombreText);

                                String[] datos = marker.getSnippet().split("%");
                                nombre.setText(datos[1].toString());
                                return v;
                            }
                        });
                    }

                }else{
                    System.out.println("RESPUESTA 1"+response);

                }
            }
            @Override
            public void onFailure(Call<UbicacionesResponse> call, Throwable t) {
                System.out.println("RESPUESTA Grande "+t.getMessage());

            }
        });

    }

    @OnClick(R.id.close)
    public void restaurar(){

        btnClose.setVisibility(View.GONE);
        market.setVisibility(View.GONE);
        addUbicacion.setVisibility(View.VISIBLE);
        guardarUbicacion.setVisibility(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap= googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng casa = new LatLng(4.683427453930319,-74.09130120985105);
        CameraPosition usme = CameraPosition.builder().target(new LatLng(4.683427453930319,-74.09130120985105)).zoom(13).bearing(0).tilt(45).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(usme));

        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                lat = cameraPosition.target.latitude;
                lng = cameraPosition.target.longitude;
            }
        });


    }


    @OnClick(R.id.addUbicacion)
    public void verMarker(){
        btnClose.setVisibility(View.VISIBLE);
        market.setVisibility(View.VISIBLE);
        addUbicacion.setVisibility(View.GONE);
        guardarUbicacion.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.guardarUbicacion)
    public void btnGuardarUbicacion(){
        //Toast.makeText(getContext(), "XXX", Toast.LENGTH_SHORT).show();
        settings = new Dialog(getContext());
        settings.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        LinearLayout lin = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.popup_ubicacion,null);
        final TextInputEditText nombreUbicacion = (TextInputEditText) lin.findViewById(R.id.nombreUbicacion);
        Button guardar = (Button) lin.findViewById(R.id.btnGuardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "LatLng "+nombreUbicacion.getText(), Toast.LENGTH_SHORT).show();
                callResponse = service.ubicacionesStore(nombreUbicacion.getText().toString(),lat,lng);
                callResponse.enqueue(new Callback<respuestasData>() {
                    @Override
                    public void onResponse(Call<respuestasData> call, Response<respuestasData> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(getContext(), "Ubicacion guardada correctamente", Toast.LENGTH_SHORT).show();
                            ubicacionesPersonal();
                        }else{
                            System.out.println("Algo aqui salio mal"+response);
                        }
                    }

                    @Override
                    public void onFailure(Call<respuestasData> call, Throwable t) {
                        System.out.println("Aqui SUPER MAL");

                    }
                });
                settings.dismiss();
            }
        });
        settings.setContentView(lin);
        settings.show();
    }


}
