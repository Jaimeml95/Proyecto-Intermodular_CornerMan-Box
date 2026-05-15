package com.jaimemoro.cornermanbox.infrastructure.external.voice;

import android.content.Context;
import android.util.Log;

import com.jaimemoro.cornermanbox.core.domain.services.IVoiceAssistant;
import org.json.JSONException;
import org.json.JSONObject;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.StorageService;

import java.io.IOException;

public class VoiceAdapter implements IVoiceAssistant, RecognitionListener {

    private static final String TAG = "VoiceAdapter";
    private Model model;
    private SpeechService speechService;
    private VoiceCallback callback;
    private boolean isListening = false;

    public VoiceAdapter(Context context) {
        initModel(context);
    }

    private void initModel(Context context) {
        StorageService.unpack(context, "model-es", "model",
            (model) -> {
                this.model = model;
                Log.d(TAG, "Modelo cargado con éxito");
            },
            (exception) -> {
                Log.e(TAG, "Error al cargar modelo: " + exception.getMessage());
            });
    }

    @Override
    public void startListening(VoiceCallback callback) {
        if (model == null) {
            callback.onError("Modelo no cargado");
            return;
        }
        this.callback = callback;
        try {
            Recognizer rec = new Recognizer(model, 16000.0f);
            speechService = new SpeechService(rec, 16000.0f);
            speechService.startListening(this);
            isListening = true;
            callback.onReadyForSpeech();
            Log.d(TAG, "Escucha activa");
        } catch (IOException e) {
            Log.e(TAG, "Error al iniciar: " + e.getMessage());
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void stopListening() {
        if (speechService != null) {
            speechService.stop();
            speechService = null;
            isListening = false;
        }
    }

    @Override
    public boolean isListening() {
        return isListening;
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
        if (callback != null) {
            callback.onError(exception.getMessage());
        }
    }

    @Override
    public void onTimeout() {}

    private void procesarTexto(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String text = obj.optString("text").toLowerCase();
            if (!text.isEmpty() && callback != null) {
                Log.d(TAG, "Detectado: " + text);
                callback.onResult(text);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error al parsear: " + e.getMessage());
        }
    }
}