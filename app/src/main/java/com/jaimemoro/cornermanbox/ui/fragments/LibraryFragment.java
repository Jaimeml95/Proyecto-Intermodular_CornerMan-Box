package com.jaimemoro.cornermanbox.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.jaimemoro.cornermanbox.R;
import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private List<TextView> categorias;
    private int colorNeon;
    private int colorApagado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_library, container, false);

        // Inicializar colores
        colorNeon = ContextCompat.getColor(getContext(), R.color.green_boxing);
        colorApagado = ContextCompat.getColor(getContext(), R.color.white); // O un gris suave

        // Localizar vistas y guardarlas en una lista para manejarlas fácil
        categorias = new ArrayList<>();
        categorias.add(root.findViewById(R.id.tv_cat_golpes));
        categorias.add(root.findViewById(R.id.tv_cat_defensa));
        categorias.add(root.findViewById(R.id.tv_cat_pasos));
        categorias.add(root.findViewById(R.id.tv_cat_combos));

        // Asignar OnClickListener a cada una
        for (TextView tv : categorias) {
            tv.setOnClickListener(v -> seleccionarCategoria((TextView) v));
        }

        // Foco por defecto: Golpes
        seleccionarCategoria(categorias.get(0));

        return root;
    }

    private void seleccionarCategoria(TextView seleccionada) {
        // Recorremos todas para resetear su estado
        for (TextView tv : categorias) {
            if (tv == seleccionada) {
                // Encendemos el Neón
                tv.setTextColor(colorNeon);
                tv.setTextSize(18); // Opcional: aumentar un poco el tamaño para dar énfasis
                // Aquí podrías añadir una sombra neón si usas setShadowLayer
            } else {
                // Apagamos el Neón
                tv.setTextColor(colorApagado);
                tv.setTextSize(16);
            }
        }

        // TODO: Aquí cargaremos los datos (vídeos/textos) correspondientes a la categoría
        cargarContenido(seleccionada.getId());
    }

    private void cargarContenido(int id) {
        // Lógica futura para rellenar la biblioteca según la pestaña
    }
}