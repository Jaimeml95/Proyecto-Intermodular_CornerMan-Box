package com.jaimemoro.cornermanbox.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.core.domain.model.Tecnica;

import java.util.List;

public class TechAdapter extends RecyclerView.Adapter<TechAdapter.TechViewHolder> {

    private List<Tecnica> listaTecnicas;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Tecnica tecnica);
    }

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

        holder.tvTitulo.setText(tecnica.getNombre());
        holder.tvDescripcion.setText(tecnica.getDescripcion());

        if (tecnica.getImagenResId() != 0) {
            holder.ivImagen.setImageResource(tecnica.getImagenResId());
        } else {
            holder.ivImagen.setImageResource(R.drawable.ic_launcher_foreground);
        }

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