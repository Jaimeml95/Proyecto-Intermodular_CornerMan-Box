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
import androidx.room.Room;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.data.entities.Usuario;
import com.jaimemoro.cornermanbox.data.local.AppDatabase;

public class SettingsFragment extends Fragment {

    private TextInputEditText etNombre, etRoundTime, etRestTime;
    private MaterialButton btnGuardar;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        etNombre = view.findViewById(R.id.etNombre);
        etRoundTime = view.findViewById(R.id.etRoundTime);
        etRestTime = view.findViewById(R.id.etRestTime);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "cornerman-db").build();

        cargarDatosActuales();

        btnGuardar.setOnClickListener(v -> guardarAjustes());

        return view;
    }

    private void cargarDatosActuales() {
        new Thread(() -> {
            Usuario user = db.usuarioDao().getUsuario();
            if (user != null) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        etNombre.setText(user.nombre);
                        etRoundTime.setText(String.valueOf(user.roundDurationSeconds));
                        etRestTime.setText(String.valueOf(user.restDurationSeconds));
                    });
                }
            }
        }).start();
    }

    private void guardarAjustes() {
        String nombre = etNombre.getText().toString().trim();
        String rTime = etRoundTime.getText().toString().trim();
        String dTime = etRestTime.getText().toString().trim();

        if (nombre.isEmpty() || rTime.isEmpty() || dTime.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            Usuario user = db.usuarioDao().getUsuario();
            if (user != null) {
                user.nombre = nombre;
                user.roundDurationSeconds = Integer.parseInt(rTime);
                user.restDurationSeconds = Integer.parseInt(dTime);
                db.usuarioDao().updateUsuario(user);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Escondemos el teclado para que no moleste en la siguiente pantalla
                        ocultarTeclado();

                        // Avisamos al usuario
                        Toast.makeText(getContext(), "Ajustes guardados correctamente", Toast.LENGTH_SHORT).show();

                        // Volvemos automáticamente al fragmento anterior (Dashboard)
                        Navigation.findNavController(requireView()).navigateUp();
                    });
                }
            }
        }).start();
    }

    /**
     * Método auxiliar para cerrar el teclado virtual de forma limpia.
     */
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