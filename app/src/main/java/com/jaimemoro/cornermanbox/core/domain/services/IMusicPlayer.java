package com.jaimemoro.cornermanbox.core.domain.services;

public interface IMusicPlayer {

    void play(String trackId, MusicCallback callback);

    void pause();

    void resume();

    void stop();

    void setVolume(int volumePercent);

    boolean isPlaying();

    interface MusicCallback {
        void onStarted(String trackId);
        void onPaused();
        void onResumed();
        void onStopped();
        void onError(String message);
    }
}