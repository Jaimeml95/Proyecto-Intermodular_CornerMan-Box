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
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.data.entities.Usuario;
import com.jaimemoro.cornermanbox.data.local.AppDatabase;

import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {

    private TextInputEditText etNombre, etRoundTime, etRestTime;
    private MaterialButton btnGuardar;
    private AppDatabase db;
    private Usuario usuarioActual; // Guardamos una referencia local

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        etNombre = view.findViewById(R.id.etNombre);
        etRoundTime = view.findViewById(R.id.etRoundTime);
        etRestTime = view.findViewById(R.id.etRestTime);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        // USAMOS EL SINGLETON (Importante para evitar errores de integridad)
        db = AppDatabase.getInstance(requireContext());

        cargarDatosActuales();

        btnGuardar.setOnClickListener(v -> guardarAjustes());

        return view;
    }

    private void cargarDatosActuales() {
        Executors.newSingleThreadExecutor().execute(() -> {
            usuarioActual = db.usuarioDao().getUsuario();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (usuarioActual != null) {
                        // Si existe, ponemos sus datos
                        etNombre.setText(usuarioActual.nombre);
                        etRoundTime.setText(String.valueOf(usuarioActual.roundDurationSeconds));
                        etRestTime.setText(String.valueOf(usuarioActual.restDurationSeconds));
                    } else {
                        // Si es null (primer inicio), ponemos los valores por defecto manuales
                        etNombre.setText("Boxeador");
                        etRoundTime.setText("180");
                        etRestTime.setText("60");
                    }
                });
            }
        });
    }

    private void guardarAjustes() {
        String nombre = etNombre.getText().toString().trim();
        String rTime = etRoundTime.getText().toString().trim();
        String dTime = etRestTime.getText().toString().trim();

        if (nombre.isEmpty() || rTime.isEmpty() || dTime.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            // Si el usuario no existe todavía en la DB, creamos uno nuevo
            if (usuarioActual == null) {
                usuarioActual = new Usuario();
            }

            usuarioActual.nombre = nombre;
            usuarioActual.roundDurationSeconds = Integer.parseInt(rTime);
            usuarioActual.restDurationSeconds = Integer.parseInt(dTime);

            // Insertamos o actualizamos según corresponda
            if (usuarioActual.id == 0) {
                db.usuarioDao().insertUsuario(usuarioActual);
            } else {
                db.usuarioDao().updateUsuario(usuarioActual);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    ocultarTeclado();
                    Toast.makeText(getContext(), "Ajustes guardados correctamente", Toast.LENGTH_SHORT).show();

                    // VOLVER AL DASHBOARD
                    Navigation.findNavController(requireView()).navigateUp();
                });
            }
        });
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