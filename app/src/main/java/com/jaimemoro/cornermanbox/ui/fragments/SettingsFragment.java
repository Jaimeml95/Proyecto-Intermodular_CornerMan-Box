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
import com.jaimemoro.cornermanbox.core.domain.model.Usuario;
import com.jaimemoro.cornermanbox.ui.viewmodel.SettingsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
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

        viewModel.getUsuario().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                etNombre.setText(user.getNombre());
                etRoundTime.setText(String.valueOf(user.getRoundDurationSeconds()));
                etRestTime.setText(String.valueOf(user.getRestDurationSeconds()));
            } else {
                etNombre.setText("Boxeador");
                etRoundTime.setText("180");
                etRestTime.setText("60");
            }
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                ocultarTeclado();
                Toast.makeText(getContext(), "Ajustes guardados correctamente", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(getContext(), "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.cargarUsuario();

        btnGuardar.setOnClickListener(v -> guardarAjustes());

        return view;
    }

    private void guardarAjustes() {
        String nombre = etNombre.getText().toString().trim();
        String rTime = etRoundTime.getText().toString().trim();
        String dTime = etRestTime.getText().toString().trim();

        if (nombre.isEmpty() || rTime.isEmpty() || dTime.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = viewModel.getUsuario().getValue();
        if (usuario == null) {
            usuario = new Usuario();
        }

        usuario.setNombre(nombre);
        usuario.setRoundDurationSeconds(Integer.parseInt(rTime));
        usuario.setRestDurationSeconds(Integer.parseInt(dTime));

        viewModel.guardarUsuario(usuario);
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