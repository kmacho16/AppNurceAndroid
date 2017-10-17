package com.kmacho.juan.nurceapp.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kmacho.juan.nurceapp.R;

import java.util.List;

/**
 * Created by Videos on 30/08/2017.
 */

public class EventosAdapter extends RecyclerView.Adapter<EventosAdapter.ViewHolder> {

    List<EventosList> listEventos;
    private Context context;
    Intent intent;
    int idUser;

    public EventosAdapter(List<EventosList> listEventos, Context context) {
        this.listEventos = listEventos;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eventos, parent, false);
        this.context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final EventosList eventosList;
        eventosList = listEventos.get(position);
        holder.nombreEvento.setText(eventosList.getNombre_evento());
        holder.fechaEvento.setText(eventosList.getFecha_inicio().substring(0,eventosList.getFecha_inicio().length()-3));

        if (eventosList.getColor().isEmpty() || eventosList.getColor().equals("")){
            holder.tagColor.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }else{
            holder.tagColor.setCardBackgroundColor(Color.parseColor("#"+eventosList.getColor()));
        }
       // holder.tagColor.setRadius(60f);

    }

    @Override
    public int getItemCount() {
        return listEventos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView fechaEvento,nombreEvento;
        public CardView tagColor;

        public ViewHolder(View itemView) {
            super(itemView);
            fechaEvento = (TextView) itemView.findViewById(R.id.fechaEvento);
            nombreEvento = (TextView) itemView.findViewById(R.id.nombreEvento);
            tagColor = (CardView) itemView.findViewById(R.id.tagColor);
        }
    }
}
