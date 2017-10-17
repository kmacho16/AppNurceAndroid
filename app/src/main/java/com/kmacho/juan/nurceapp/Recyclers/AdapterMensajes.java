package com.kmacho.juan.nurceapp.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kmacho.juan.nurceapp.IdUserPreferences;
import com.kmacho.juan.nurceapp.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Videos on 17/08/2017.
 */

public class AdapterMensajes  extends RecyclerView.Adapter<AdapterMensajes.ViewHolder> {
    private List<MensajesList> listMensajes;
    private Context context;
    Intent intent;
    SharedPreferences idUserPreferences;

    float radius = (float) 50.0;

    public AdapterMensajes(List<MensajesList> listMensajes, Context context) {
        this.listMensajes = listMensajes;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
        idUserPreferences = parent.getContext().getSharedPreferences("Contex",MODE_PRIVATE);

        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RelativeLayout.LayoutParams r1 = (RelativeLayout.LayoutParams) holder.cardView.getLayoutParams();
        MensajesList mList;
        int color = Color.parseColor("#FFCFDACF");

        mList = listMensajes.get(position);
        holder.textMensaje.setText(mList.getMensaje());
        holder.textoFecha.setText(mList.getCreated_at());
        int idUser = Integer.parseInt(idUserPreferences.getAll().get("ID_USER").toString());
        System.out.println("esta aqui "+idUser);
        if (mList.getTo_id_user() == idUser){
            holder.cardView.setBackgroundColor(color);
        }else{
            //holder.cardView.setBackgroundColor(Color.parseColor("#b0ffaf"));
            holder.textMensaje.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.textMensaje.setGravity(View.TEXT_ALIGNMENT_TEXT_END);
        }
        holder.cardView.setRadius(radius);
    }

    @Override
    public int getItemCount() {
        return listMensajes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textMensaje, textoFecha;
        public CardView cardView;


        public ViewHolder(View itemView) {
            super(itemView);
            textMensaje = (TextView) itemView.findViewById(R.id.textMensaje);
            textoFecha = (TextView) itemView.findViewById(R.id.textFecha);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }

    }
}