package com.kmacho.juan.nurceapp;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.kmacho.juan.nurceapp.Recyclers.EventosAdapter;
import com.kmacho.juan.nurceapp.Recyclers.EventosList;
import com.kmacho.juan.nurceapp.entities.eventosResponse;
import com.kmacho.juan.nurceapp.network.ApiService;
import com.kmacho.juan.nurceapp.network.RetrofitBuilder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class FragmentCalendario extends Fragment {
    View v;

    CompactCalendarView compactCalendarView;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM-YYYY", Locale.getDefault());

    TokenManager tokenManager;
    IdUserPreferences idUserPreferences;
    ApiService service;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    List<EventosList> eventosLists;
    Call<eventosResponse> call;

    @BindView(R.id.textMes)
    TextView textMes;

    @BindView(R.id.eventos)
    LinearLayout eventos;

    @BindView(R.id.btn_add_event)
    Button btn_add;

    long milisegundos;
    SimpleDateFormat formato;


    public FragmentCalendario() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_calendario, container, false);
        ButterKnife.bind(this,v);

        compactCalendarView = (CompactCalendarView) v.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(true);

        String fechaActual = "2017-08-30 16:03:00";
        formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs",getContext().MODE_PRIVATE));
        idUserPreferences = IdUserPreferences.getInstance(this.getActivity().getSharedPreferences("Contex",getContext().MODE_PRIVATE));

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerEvents);
        recyclerView.setLayoutManager(layoutManager);
        eventosLists = new ArrayList<>();

        try {
            Date mDate = formato.parse(fechaActual);
            milisegundos = mDate.getTime();
            System.out.println("milis "+milisegundos);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Event event = new Event(Color.GREEN,1503932829000L,"PRUEBA DE ALGO");
        //compactCalendarView.addEvent(event2,true);

        List<Event> events = compactCalendarView.getEvents(1503932829000L);
        loadEventos();

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                if (!events.isEmpty()){
                    //eventos.setVisibility(View.VISIBLE);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Toast.makeText(getContext(), ""+ formatter.format(dateClicked) + ": "+events.get(0).getData() , Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(), "Cita del dia "+ dateClicked + " "+events.get(0).getData() , Toast.LENGTH_SHORT).show();
                    String fechaClick = formatter.format(dateClicked);
                    btn_add.setText("Agregar evento el "+fechaClick);
                    recyclerData(fechaClick);

                }else {
                    Toast.makeText(getContext(), "No hay eventos", Toast.LENGTH_SHORT).show();

                    int size = eventosLists.size();
                    if(size>0){
                        adapter.notifyItemRangeRemoved(0, size);
                        eventosLists.clear();
                    }
                }
                eventos.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                //getActivity().setTitle("Segundo Fragment");
                textMes.setText(simpleDateFormat.format(firstDayOfNewMonth));
                int size = eventosLists.size();
                if(size>0){
                    adapter.notifyItemRangeRemoved(0, size);
                    eventosLists.clear();
                }
            }
        });
        return v;
    }

    public void loadEventos(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando informacion");
        progressDialog.show();
        call = service.eventosAll();

        System.out.println("AQUI");
        call.enqueue(new Callback<eventosResponse>() {
            @Override
            public void onResponse(Call<eventosResponse> call, Response<eventosResponse> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();

                    for (int i=0;i<response.body().getData().size();i++){
                        Date mDate = null;
                        try {
                            mDate = formato.parse(response.body().getData().get(i).getFecha_inicio());
                            milisegundos = mDate.getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Event event = new Event(Color.parseColor("#"+response.body().getData().get(i).getColor()),milisegundos,response.body().getData().get(i).getNombre_evento());
                        //EventosList item = new EventosList("Uno","","eee",fecha,"",1,2,3);
                        compactCalendarView.addEvent(event,true);
                    }
                }else{
                    System.out.println("ERROR aqui"+response);
                }
            }

            @Override
            public void onFailure(Call<eventosResponse> call, Throwable t) {
                System.out.println("ERROR GRANDE");
            }
        });
    }

    public void recyclerData(String fechaClick){
        eventosLists.clear();
        recyclerView.removeAllViewsInLayout();
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando informacion");
        progressDialog.show();
        call = service.eventosDay(fechaClick);
        call.enqueue(new Callback<eventosResponse>() {
            @Override
            public void onResponse(Call<eventosResponse> call, Response<eventosResponse> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    for (int i =0; i<response.body().getData().size();i++){
                        String nombre = response.body().getData().get(i).getNombre_evento();
                        String color = response.body().getData().get(i).getColor();
                        String fecha_inicio = response.body().getData().get(i).getFecha_inicio();
                        int id = response.body().getData().get(i).getId();

                        EventosList item = new EventosList(nombre,"",color,fecha_inicio,"",id,0,0);
                        eventosLists.add(item);
                        adapter = new EventosAdapter(eventosLists,getActivity());
                        recyclerView.setAdapter(adapter);
                    }
                }else {
                    System.out.println("ERROR PEQUE "+response);

                }
            }

            @Override
            public void onFailure(Call<eventosResponse> call, Throwable t) {
                System.out.println("ERRROR GRANDE"+t.getMessage());
            }
        });
    }

}
