package com.jaimemoro.cornermanbox.data.local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.jaimemoro.cornermanbox.data.entities.Entrenamiento;
import com.jaimemoro.cornermanbox.data.entities.Tecnica;
import com.jaimemoro.cornermanbox.data.entities.Usuario;
import com.jaimemoro.cornermanbox.data.local.UsuarioDao;
import com.jaimemoro.cornermanbox.data.local.EntrenamientoDao;
import com.jaimemoro.cornermanbox.data.local.TecnicaDao;
import com.jaimemoro.cornermanbox.data.DataSeeder;
import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;
import android.util.Log;

// Funcionamiento de Room por versiones
@Database(entities = {Usuario.class, Entrenamiento.class, Tecnica.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UsuarioDao usuarioDao();
    public abstract EntrenamientoDao entrenamientoDao();

    // Descomentado y habilitado para gestionar la biblioteca técnica
    public abstract TecnicaDao tecnicaDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "cornerman-db")
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);

                                    // ¿Se está creando la base de datos?
                                    Log.d("CornerManDB", "--- onCreate: Creando DB por primera vez ---");

                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        try {
                                            List<Tecnica> semillas = DataSeeder.getTecnicasSemilla();

                                            // ¿Cuántos datos vamos a meter?
                                            Log.d("CornerManDB", "--- Semillero: Intentando insertar " + semillas.size() + " técnicas ---");

                                            getInstance(context).tecnicaDao().insertarVarias(semillas);

                                            // ¿Ha terminado la inserción?
                                            Log.d("CornerManDB", "--- Semillero: ¡Inserción completada con éxito! ---");
                                        } catch (Exception e) {
                                            // Por si algo falla en la siembra
                                            Log.e("CornerManDB", "--- ERROR en la siembra: " + e.getMessage());
                                        }
                                    });
                                }
                            })
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}