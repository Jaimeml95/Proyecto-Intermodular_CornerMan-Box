package com.jaimemoro.cornermanbox.data.spotify;

import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SpotifyManager {
    private static final String TAG = "SpotifyManager";
    private SpotifyAppRemote mSpotifyAppRemote;
    private final Context context;

    public SpotifyManager(Context context) {
        this.context = context;
    }

    public void conectar(SpotifyConnectionListener listener) {
        ConnectionParams connectionParams = new ConnectionParams.Builder(SpotifyConfig.CLIENT_ID)
                .setRedirectUri(SpotifyConfig.REDIRECT_URI)
                .showAuthView(true)
                .build();

        SpotifyAppRemote.connect(context, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d(TAG, "¡Conectado a Spotify!");
                if (listener != null) listener.onConnected();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Error de conexión: " + throwable.getMessage());
                if (listener != null) listener.onFailure(throwable);
            }
        });
    }

    // --- ACCIONES DE BOXEO ---

    public void reproducirMusica(String spotifyUri) {
        if (mSpotifyAppRemote != null) {
            mSpotifyAppRemote.getPlayerApi().play(spotifyUri);
        }
    }

    public void pausarMusica() {
        if (mSpotifyAppRemote != null) {
            mSpotifyAppRemote.getPlayerApi().pause();
        }
    }

    public void ajustarVolumen(float nivel) {
        if (mSpotifyAppRemote != null) {
            mSpotifyAppRemote.getConnectApi().connectSetVolume(nivel);
        }
    }

    public void desconectar() {
        if (mSpotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
            mSpotifyAppRemote = null;
        }
    }

    public interface SpotifyConnectionListener {
        void onConnected();
        void onFailure(Throwable error);
    }
}