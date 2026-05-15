package com.jaimemoro.cornermanbox.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.viewmodel.SettingsViewModel;

public class SettingsFragment extends Fragment {

    private TextInputEditText etNombre, etRoundTime, etRestTime;
    private MaterialButton btnGuardar;
    private SettingsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        etNombre = view.findViewById(R.id.etNombre);
        etRoundTime = view.findViewById(R.id.etRoundTime);
        etRestTime = view.findViewById(R.id.etRestTime);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        viewModel.getUsuario().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                etNombre.setText(usuario.nombre);
                etRoundTime.setText(String.valueOf(usuario.roundDurationSeconds));
                etRestTime.setText(String.valueOf(usuario.restDurationSeconds));
            } else {
                etNombre.setText("Boxeador");
                etRoundTime.setText("180");
                etRestTime.setText("60");
            }
        });

        viewModel.getGuardadoExitoso().observe(getViewLifecycleOwner(), exitoso -> {
            if (Boolean.TRUE.equals(exitoso)) {
                ocultarTeclado();
                Toast.makeText(getContext(), "Ajustes guardados correctamente", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        viewModel.getErrorMensaje().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String rTime = etRoundTime.getText().toString().trim();
            String dTime = etRestTime.getText().toString().trim();
            viewModel.guardarUsuario(nombre, rTime, dTime);
        });

        return view;
    }

    private void ocultarTeclado() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}