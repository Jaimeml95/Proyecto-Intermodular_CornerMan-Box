package com.jaimemoro.cornermanbox.infrastructure.external.spotify;

import android.content.Context;
import android.util.Log;

import com.jaimemoro.cornermanbox.core.domain.services.IMusicPlayer;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SpotifyAdapter implements IMusicPlayer {

    private static final String TAG = "SpotifyAdapter";
    private SpotifyAppRemote spotifyAppRemote;
    private final Context context;
    private MusicCallback currentCallback;
    private boolean isPlaying = false;

    public SpotifyAdapter(Context context) {
        this.context = context;
    }

    public void conectar(ConnectionCallback callback) {
        ConnectionParams connectionParams = new ConnectionParams.Builder(
            SpotifyConfig.CLIENT_ID)
            .setRedirectUri(SpotifyConfig.REDIRECT_URI)
            .showAuthView(true)
            .build();

        SpotifyAppRemote.connect(context, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                SpotifyAdapter.this.spotifyAppRemote = spotifyAppRemote;
                Log.d(TAG, "Conectado a Spotify");
                callback.onConnected();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Error de conexión: " + throwable.getMessage());
                callback.onFailure(throwable.getMessage());
            }
        });
    }

    public interface ConnectionCallback {
        void onConnected();
        void onFailure(String error);
    }

    @Override
    public void play(String trackId, MusicCallback callback) {
        if (spotifyAppRemote == null) {
            callback.onError("Spotify no conectado");
            return;
        }
        this.currentCallback = callback;
        spotifyAppRemote.getPlayerApi().play(trackId);
        isPlaying = true;
        callback.onStarted(trackId);
    }

    @Override
    public void pause() {
        if (spotifyAppRemote != null) {
            spotifyAppRemote.getPlayerApi().pause();
            isPlaying = false;
            if (currentCallback != null) currentCallback.onPaused();
        }
    }

    @Override
    public void resume() {
        if (spotifyAppRemote != null) {
            spotifyAppRemote.getPlayerApi().resume();
            isPlaying = true;
            if (currentCallback != null) currentCallback.onResumed();
        }
    }

    @Override
    public void stop() {
        if (spotifyAppRemote != null) {
            spotifyAppRemote.getPlayerApi().pause();
            isPlaying = false;
            if (currentCallback != null) currentCallback.onStopped();
        }
    }

    @Override
    public void setVolume(int volumePercent) {
        if (spotifyAppRemote != null) {
            spotifyAppRemote.getConnectApi().connectSetVolume(volumePercent / 100f);
        }
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    public void desconectar() {
        if (spotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote);
            spotifyAppRemote = null;
        }
    }
}