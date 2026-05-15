package com.jaimemoro.cornermanbox.infrastructure.persistence.room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.jaimemoro.cornermanbox.data.entities.Entrenamiento;
import com.jaimemoro.cornermanbox.data.entities.Tecnica;
import com.jaimemoro.cornermanbox.data.entities.Usuario;
import com.jaimemoro.cornermanbox.infrastructure.persistence.DataSeeder;
import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;
import android.util.Log;

@Database(entities = {Usuario.class, Entrenamiento.class, Tecnica.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UsuarioDao usuarioDao();
    public abstract EntrenamientoDao entrenamientoDao();
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

                                    Log.d("CornerManDB", "--- onCreate: Creando DB por primera vez ---");

                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        try {
                                            List<Tecnica> semillas = DataSeeder.getTecnicasSemilla();

                                            Log.d("CornerManDB", "--- Semillero: Intentando insertar " + semillas.size() + " técnicas ---");

                                            getInstance(context).tecnicaDao().insertarVarias(semillas);

                                            Log.d("CornerManDB", "--- Semillero: ¡Inserción completada con éxito! ---");
                                        } catch (Exception e) {
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