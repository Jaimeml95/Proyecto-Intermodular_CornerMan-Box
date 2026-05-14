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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.viewmodel.TechViewModel;
import com.jaimemoro.cornermanbox.ui.adapters.TechAdapter;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private List<TextView> categorias;
    private int colorNeon;
    private int colorApagado;

    private int idCategoriaActual = R.id.tv_cat_golpes; // Por defecto empezamos en Golpes

    // Nuevos campos para la lógica de datos
    private TechViewModel viewModel;
    private RecyclerView recyclerView;
    private TechAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_library, container, false);

        // Inicializar colores y vistas
        colorNeon = ContextCompat.getColor(getContext(), R.color.green_boxing);
        colorApagado = ContextCompat.getColor(getContext(), R.color.white);

        recyclerView = root.findViewById(R.id.rv_tech_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        categorias = new ArrayList<>();
        categorias.add(root.findViewById(R.id.tv_cat_golpes));
        categorias.add(root.findViewById(R.id.tv_cat_defensa));
        categorias.add(root.findViewById(R.id.tv_cat_pasos));
        categorias.add(root.findViewById(R.id.tv_cat_combos));

        for (TextView tv : categorias) {
            tv.setOnClickListener(v -> seleccionarCategoria((TextView) v));
        }

        // Foco por defecto
        seleccionarCategoria(categorias.get(0));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar el ViewModel
        viewModel = new ViewModelProvider(this).get(TechViewModel.class);

        // Observar los cambios en la lista de técnicas
        viewModel.getListaTecnicas().observe(getViewLifecycleOwner(), tecnicas -> {
            if (adapter == null) {
                adapter = new TechAdapter(tecnicas);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateList(tecnicas);
            }
        });

        // Configurar el Buscador en tiempo real
        android.widget.EditText etBuscar = view.findViewById(R.id.et_buscar_tecnica);
        etBuscar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                if (query.isEmpty()) {
                    // Si el buscador está vacío, volvemos a filtrar por la pestaña activa
                    cargarContenido(idCategoriaActual);
                } else {
                    // Si hay texto, buscamos (aquí busca en toda la DB)
                    viewModel.buscarTecnica(query);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
    }

    private void seleccionarCategoria(TextView seleccionada) {
        // Guardamos el ID de la categoría que acaba de recibir el foco
        this.idCategoriaActual = seleccionada.getId();
        // Localizar y limpiar el buscador
        // Usamos getView() porque estamos dentro de un Fragment
        if (getView() != null) {
            android.widget.EditText etBuscar = getView().findViewById(R.id.et_buscar_tecnica);
            if (etBuscar != null) {
                etBuscar.setText("");
            }
        }
        // Lógica de colores neón
        for (TextView tv : categorias) {
            if (tv == seleccionada) {
                tv.setTextColor(colorNeon);
                tv.setTextSize(18);
            } else {
                tv.setTextColor(colorApagado);
                tv.setTextSize(16);
            }
        }
        // Cargar los datos de la categoría elegida
        cargarContenido(idCategoriaActual);
    }

    private void cargarContenido(int id) {
        // Mapeamos el ID del TextView a la categoría de la base de datos
        if (viewModel == null) return;

        if (id == R.id.tv_cat_golpes) {
            viewModel.filtrarPorCategoria("GOLPES");
        } else if (id == R.id.tv_cat_defensa) {
            viewModel.filtrarPorCategoria("DEFENSA");
        } else if (id == R.id.tv_cat_pasos) {
            viewModel.filtrarPorCategoria("PASOS");
        } else if (id == R.id.tv_cat_combos) {
            viewModel.filtrarPorCategoria("COMBOS");
        }
    }
}