package com.kmacho.juan.nurceapp.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kmacho.juan.nurceapp.IdUserPreferences;
import com.kmacho.juan.nurceapp.MessageActivity;
import com.kmacho.juan.nurceapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

/**
 * Created by Videos on 21/08/2017.
 */

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder> {
    List<ChatList> listChat;
    private Context context;
    Intent intent;
    int idUser;
    SharedPreferences idUserPreferences;



    public AdapterChat(List<ChatList> listChat, Context context) {
        this.listChat = listChat;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        this.context = parent.getContext();
        idUserPreferences = parent.getContext().getSharedPreferences("Contex",MODE_PRIVATE);
        idUser = Integer.parseInt(idUserPreferences.getAll().get("ID_USER").toString());

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChatList mList;
        mList = listChat.get(position);

        holder.textoUser.setText(mList.getFrom_nombre());
        holder.textMensaje.setText(mList.getMensaje());
        holder.textoFecha.setText(mList.getCreated_at());
        Picasso.with(context).load("http://app-nurce-hero.herokuapp.com/uploads/"+mList.getFrom_img()).into(holder.foto_user);
        holder.cardChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(context, MessageActivity.class);
                intent.putExtra("id_chat",mList.getId_chat());

                if (idUser==mList.getTo_id_user()){
                    intent.putExtra("to_id_user",mList.getId_user());
                }else {
                    intent.putExtra("to_id_user",mList.getTo_id_user());
                }

                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textoUser,textMensaje, textoFecha;
        public ImageView foto_user;
        public CardView cardChat;


        public ViewHolder(View itemView) {
            super(itemView);
            textMensaje = (TextView) itemView.findViewById(R.id.textMensaje);

            textoFecha = (TextView) itemView.findViewById(R.id.textFecha);
            textoUser = (TextView) itemView.findViewById(R.id.textUser);
            foto_user = (ImageView) itemView.findViewById(R.id.foto_user);
            cardChat = (CardView) itemView.findViewById(R.id.cardChat);
        }
    }
}
