package com.jaimemoro.cornermanbox.data.spotify;

import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
// Importante para detectar la canción
import com.spotify.protocol.types.Track;

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

    // --- NUEVO: MÉTODO PARA QUE EL FRAGMENT SE SUSCRIBA A LA CANCIÓN ---
    public void suscribirseACancion(TrackCallback callback) {
        if (mSpotifyAppRemote == null || !mSpotifyAppRemote.isConnected()) {
            Log.e(TAG, "No se puede suscribir: Remote no conectado");
            return;
        }

        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    Track track = playerState.track;
                    if (track != null && callback != null) {
                        // Avisamos al Fragment con el nombre y artista
                        callback.onTrackChanged(track.name, track.artist.name);
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

    public void reanudarMusica() {
        if (mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected()) {
            mSpotifyAppRemote.getPlayerApi().resume();
        }
    }

    public void saltarSiguiente() {
        if (mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected()) {
            mSpotifyAppRemote.getPlayerApi().skipNext();
        }
    }

    public void saltarAnterior() {
        if (mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected()) {
            mSpotifyAppRemote.getPlayerApi().skipPrevious();
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

    public void retrocederCancionInteligente() {
        if (mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected()) {
            // Pedimos el estado actual de la reproducción
            mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
                long posicionMS = playerState.playbackPosition; // Tiempo actual en milisegundos

                if (posicionMS < 10000) { // Si lleva más de 10 segundos (10,000 ms)
                    Log.d("SPOTIFY_BOX", "Lleva más de 10s. Ejecutando doble retroceso.");

                    // Primer salto: reinicia la canción
                    mSpotifyAppRemote.getPlayerApi().skipPrevious();

                    // Pequeña pausa de 300ms para que Spotify procese el primer comando
                    // y luego lanzamos el segundo para ir a la canción anterior
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        mSpotifyAppRemote.getPlayerApi().skipPrevious();
                    }, 300);

                } else {
                    Log.d("SPOTIFY_BOX", "Lleva menos de 10s. Salto simple.");
                    // Salto simple: va directo a la anterior
                    mSpotifyAppRemote.getPlayerApi().skipPrevious();
                }
            });
        }
    }

    // --- INTERFACES ---

    public interface SpotifyConnectionListener {
        void onConnected();
        void onFailure(Throwable error);
    }

    // Nueva interfaz para enviar datos al Fragment
    public interface TrackCallback {
        void onTrackChanged(String titulo, String artista);
    }
}