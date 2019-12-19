
package com.example.projectlocalizador.Adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectlocalizador.Entidades.ActivoEntidad;
import com.example.projectlocalizador.R;

import java.util.List;

public class ActivosAdapter extends RecyclerView.Adapter<ActivosAdapter.ActivosHolder>{
    List<ActivoEntidad> listaActivos;



    public ActivosAdapter(List<ActivoEntidad> listaActivos) {
        this.listaActivos = listaActivos;
    }

    @NonNull
    @Override
    public ActivosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_activos,parent,false);
        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);


        return new ActivosHolder(vista);



    }

    @Override
    public void onBindViewHolder(@NonNull ActivosHolder holder, int position) {

        holder.idDescripcion.setText(listaActivos.get(position).getDescripcion());
        holder.idSerie.setText(listaActivos.get(position).getSerie());
        holder.idModelo.setText(listaActivos.get(position).getModelo());
        holder.idMarca.setText(listaActivos.get(position).getMarca());

        //////
        if (listaActivos.get(position).getImagen()!=null){
            holder.imagen.setImageBitmap(listaActivos.get(position).getImagen());
        }else{
            holder.imagen.setImageResource(R.drawable.img_base);
        }
    }

    @Override
    public int getItemCount() {
        return listaActivos.size();
    }


    public class ActivosHolder extends RecyclerView.ViewHolder{

        TextView idDescripcion,idSerie,idModelo,idMarca;
        ImageView imagen;

        public ActivosHolder(View itemView) {
            super(itemView);
            idDescripcion= (TextView) itemView.findViewById(R.id.idDescripcion);
            idSerie= (TextView) itemView.findViewById(R.id.idSerie);
            idModelo= (TextView) itemView.findViewById(R.id.idModelo);
            idMarca= (TextView) itemView.findViewById(R.id.idMarca);
            ///////
            imagen=(ImageView) itemView.findViewById(R.id.idImagen);
        }
    }
}
