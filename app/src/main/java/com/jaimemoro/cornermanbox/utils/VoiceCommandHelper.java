package com.jaimemoro.cornermanbox.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.StorageService;

import java.io.IOException;

public class VoiceCommandHelper implements RecognitionListener {

    public interface VoiceCommandListener {
        void onCommandDetected(String comando);
    }

    private final VoiceCommandListener listener;
    private Model model;
    private SpeechService speechService;
    private final Context context;

    public VoiceCommandHelper(Context context, VoiceCommandListener listener) {
        this.context = context;
        this.listener = listener;
        initModel();
    }

    private void initModel() {
        // Usamos StorageService para mover el modelo de assets a la memoria interna
        // 'model-es' es tu carpeta en assets
        // 'model' es el nombre de la carpeta de destino en el teléfono
        StorageService.unpack(context, "model-es", "model",
                (model) -> {
                    this.model = model;
                    Log.d("VOSK", "¡Modelo cargado con éxito! Ya puedes empezar.");
                },
                (exception) -> {
                    Log.e("VOSK", "Error crítico al desempaquetar: " + exception.getMessage());
                    // Si ves este error, es que la estructura de carpetas en assets sigue mal
                });
    }

    public void startListening() {
        if (model == null) {
            Log.e("VOSK", "El modelo aún no está cargado");
            return;
        }

        try {
            // Creamos el reconocedor con una frecuencia de muestreo de 16000Hz (estándar)
            Recognizer rec = new Recognizer(model, 16000.0f);
            speechService = new SpeechService(rec, 16000.0f);
            speechService.startListening(this);
            Log.d("VOSK", "Escucha activa y silenciosa");
        } catch (IOException e) {
            Log.e("VOSK", "Error al iniciar el servicio de voz: " + e.getMessage());
        }
    }

    public void stopListening() {
        if (speechService != null) {
            speechService.stop();
            speechService = null;
        }
    }

    public void destroy() {
        if (speechService != null) {
            speechService.shutdown();
        }
    }

    // --- MÉTODOS DE LA INTERFAZ RecognitionListener ---

    @Override
    public void onResult(String hypothesis) {
        // Este método se dispara cuando Vosk cree que has terminado una frase
        procesarTexto(hypothesis);
    }

    @Override
    public void onPartialResult(String hypothesis) {
        // Resultados en tiempo real (mientras hablas)
        // Podríamos usarlo para una respuesta más rápida, pero onResult es más estable
    }

    @Override
    public void onFinalResult(String hypothesis) {
        procesarTexto(hypothesis);
    }

    @Override
    public void onError(Exception exception) {
        Log.e("VOSK", "Error: " + exception.getMessage());
    }

    @Override
    public void onTimeout() {
        // No se usa normalmente con SpeechService
    }

    private void procesarTexto(String json) {
        try {
            // Vosk devuelve los resultados en formato JSON: {"text": "box"}
            JSONObject obj = new JSONObject(json);
            String text = obj.optString("text").toLowerCase();

            if (!text.isEmpty()) {
                Log.d("VOSK", "Detectado: " + text);
                listener.onCommandDetected(text);
            }
        } catch (JSONException e) {
            Log.e("VOSK", "Error al parsear JSON: " + e.getMessage());
        }
    }
}