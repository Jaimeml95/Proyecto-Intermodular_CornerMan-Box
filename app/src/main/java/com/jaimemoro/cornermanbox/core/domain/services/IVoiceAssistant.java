package com.jaimemoro.cornermanbox.core.domain.services;

public interface IVoiceAssistant {

    void startListening(VoiceCallback callback);

    void stopListening();

    boolean isListening();

    interface VoiceCallback {
        void onResult(String command);
        void onError(String message);
        void onReadyForSpeech();
    }
}