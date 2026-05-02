package com.jaimemoro.cornermanbox.utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class VoiceCommandHelper {

    public interface VoiceCommandListener {
        void onCommandDetected(String command);
    }

    private final Context context;
    private final VoiceCommandListener listener;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private AudioManager audioManager;
    private boolean isEnabled = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public VoiceCommandHelper(Context context, VoiceCommandListener listener) {
        this.context = context;
        this.listener = listener;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initRecognizer();
    }

    private void initRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);

        // Ajustes de sensibilidad
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1500);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) { Log.d("VOICE", "Listo"); }
            @Override public void onBeginningOfSpeech() { Log.d("VOICE", "Sonido detectado"); }
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                setMute(false);
                if (isEnabled) {
                    mHandler.postDelayed(() -> startListening(), 500);
                }
            }

            @Override
            public void onResults(Bundle results) {
                setMute(false);
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    listener.onCommandDetected(matches.get(0).toLowerCase());
                }
                if (isEnabled) startListening();
            }

            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    public void startListening() {
        isEnabled = true;
        mHandler.post(() -> {
            try {
                setMute(true);
                speechRecognizer.startListening(speechIntent);
            } catch (Exception e) {
                Log.e("VOICE", "Error start: " + e.getMessage());
            }
        });
    }

    public void stopListening() {
        isEnabled = false;
        speechRecognizer.stopListening();
        speechRecognizer.cancel();
        setMute(false);
    }

    public void destroy() {
        isEnabled = false; // Detenemos la bandera de bucle inmediatamente

        // Limpiamos cualquier reinicio de escucha que esté en cola
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (speechRecognizer != null) {
            try {
                speechRecognizer.stopListening();
                speechRecognizer.cancel();
                speechRecognizer.destroy();
            } catch (Exception e) {
                Log.e("VOICE", "Error al destruir recognizer: " + e.getMessage());
            }
            speechRecognizer = null;
        }
        setMute(false); // Aseguramos que el sonido del sistema vuelva
    }

    private void setMute(boolean mute) {
        int mode = mute ? AudioManager.ADJUST_MUTE : AudioManager.ADJUST_UNMUTE;
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, mode, 0);
        audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, mode, 0);
    }
}