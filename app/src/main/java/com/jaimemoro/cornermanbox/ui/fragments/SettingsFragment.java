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
import com.jaimemoro.cornermanbox.core.application.usecases.GetUsuarioUseCase;
import com.jaimemoro.cornermanbox.core.application.usecases.UpdateUsuarioUseCase;
import com.jaimemoro.cornermanbox.core.domain.model.Usuario;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    private TextInputEditText etNombre, etRoundTime, etRestTime;
    private MaterialButton btnGuardar;
    private Usuario usuarioActual;

    @Inject
    public GetUsuarioUseCase getUsuarioUseCase;

    @Inject
    public UpdateUsuarioUseCase updateUsuarioUseCase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        etNombre = view.findViewById(R.id.etNombre);
        etRoundTime = view.findViewById(R.id.etRoundTime);
        etRestTime = view.findViewById(R.id.etRestTime);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        cargarDatosActuales();

        btnGuardar.setOnClickListener(v -> guardarAjustes());

        return view;
    }

    private void cargarDatosActuales() {
        getUsuarioUseCase.ejecutar(new com.jaimemoro.cornermanbox.core.domain.repository.IUsuarioRepository.RepositoryCallback<Usuario>() {
            @Override
            public void onSuccess(Usuario user) {
                usuarioActual = user;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (usuarioActual != null) {
                            etNombre.setText(usuarioActual.getNombre());
                            etRoundTime.setText(String.valueOf(usuarioActual.getRoundDurationSeconds()));
                            etRestTime.setText(String.valueOf(usuarioActual.getRestDurationSeconds()));
                        } else {
                            etNombre.setText("Boxeador");
                            etRoundTime.setText("180");
                            etRestTime.setText("60");
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        etNombre.setText("Boxeador");
                        etRoundTime.setText("180");
                        etRestTime.setText("60");
                    });
                }
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

        if (usuarioActual == null) {
            usuarioActual = new Usuario();
        }

        usuarioActual.setNombre(nombre);
        usuarioActual.setRoundDurationSeconds(Integer.parseInt(rTime));
        usuarioActual.setRestDurationSeconds(Integer.parseInt(dTime));

        updateUsuarioUseCase.ejecutar(usuarioActual, new com.jaimemoro.cornermanbox.core.domain.repository.IUsuarioRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
            }

            @Override
            public void onError(Exception e) {
            }
        });

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                ocultarTeclado();
                Toast.makeText(getContext(), "Ajustes guardados correctamente", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            });
        }
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