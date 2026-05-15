package com.jaimemoro.cornermanbox.infrastructure.external.voice;

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
        StorageService.unpack(context, "model-es", "model",
                (model) -> {
                    this.model = model;
                    Log.d("VOSK", "Modelo cargado con éxito. Ya puedes empezar.");
                },
                (exception) -> {
                    Log.e("VOSK", "Error al desempaquetar: " + exception.getMessage());
                });
    }

    public void startListening() {
        if (model == null) {
            Log.e("VOSK", "El modelo aún no está cargado");
            return;
        }

        try {
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

    @Override
    public void onResult(String hypothesis) {
        procesarTexto(hypothesis);
    }

    @Override
    public void onPartialResult(String hypothesis) {}

    @Override
    public void onFinalResult(String hypothesis) {
        procesarTexto(hypothesis);
    }

    @Override
    public void onError(Exception exception) {
        Log.e("VOSK", "Error: " + exception.getMessage());
    }

    @Override
    public void onTimeout() {}

    private void procesarTexto(String json) {
        try {
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