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
    private OnItemClickListener listener; // Corregido: Usamos nuestra propia interfaz

    // Interfaz para el click
    public interface OnItemClickListener {
        void onItemClick(Tecnica tecnica);
    }

    // Constructor corregido y cerrado
    public TechAdapter(List<Tecnica> listaTecnicas, OnItemClickListener listener) {
        this.listaTecnicas = listaTecnicas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TechViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tech, parent, false);
        return new TechViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TechViewHolder holder, int position) {
        Tecnica tecnica = listaTecnicas.get(position);

        holder.tvTitulo.setText(tecnica.nombre);
        holder.tvDescripcion.setText(tecnica.descripcion);

        if (tecnica.imagenResId != 0) {
            holder.ivImagen.setImageResource(tecnica.imagenResId);
        } else {
            holder.ivImagen.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Configuramos el click en la tarjeta (itemView)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(tecnica);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaTecnicas != null ? listaTecnicas.size() : 0;
    }

    public void updateList(List<Tecnica> nuevaLista) {
        this.listaTecnicas = nuevaLista;
        notifyDataSetChanged();
    }

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