package com.jaimemoro.cornermanbox.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.data.entities.Tecnica;

import java.util.List;

public class TechAdapter extends RecyclerView.Adapter<TechAdapter.TechViewHolder> {

    private List<Tecnica> listaTecnicas;

    public TechAdapter(List<Tecnica> listaTecnicas) {
        this.listaTecnicas = listaTecnicas;
    }

    @NonNull
    @Override
    public TechViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el diseño de la tarjeta que creamos anteriormente (item_tech.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tech, parent, false);
        return new TechViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TechViewHolder holder, int position) {
        Tecnica tecnica = listaTecnicas.get(position);

        // Asignamos los datos a la vista
        holder.tvTitulo.setText(tecnica.nombre);
        holder.tvDescripcion.setText(tecnica.descripcion);

        // Seteamos la imagen (por ahora usarás ic_launcher_foreground o la que hayas definido)
        if (tecnica.imagenResId != 0) {
            holder.ivImagen.setImageResource(tecnica.imagenResId);
        } else {
            holder.ivImagen.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    @Override
    public int getItemCount() {
        return listaTecnicas != null ? listaTecnicas.size() : 0;
    }

    /**
     * Método fundamental para que los filtros funcionen.
     * Cuando el ViewModel cambia la lista, avisamos al adaptador.
     */
    public void updateList(List<Tecnica> nuevaLista) {
        this.listaTecnicas = nuevaLista;
        notifyDataSetChanged(); // Notifica que los datos han cambiado para refrescar la UI
    }

    // Clase interna para sujetar las vistas de cada tarjeta
    static class TechViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion;
        ImageView ivImagen;

        public TechViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_tech_title);
            tvDescripcion = itemView.findViewById(R.id.tv_tech_desc);
            ivImagen = itemView.findViewById(R.id.iv_tech_image);
        }
    }
}