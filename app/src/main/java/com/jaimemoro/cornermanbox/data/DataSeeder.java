package com.jaimemoro.cornermanbox.data;

import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.data.entities.Tecnica;
import java.util.ArrayList;
import java.util.List;

public class DataSeeder {

    public static List<Tecnica> getTecnicasSemilla() {
        List<Tecnica> semillas = new ArrayList<>();

        // GOLPES
        semillas.add(new Tecnica("Jab", "Golpe recto con la mano delantera. Es la base de la distancia.", "GOLPES", R.drawable.ic_launcher_foreground));
        semillas.add(new Tecnica("Cross", "Golpe directo potente con la mano trasera.", "GOLPES", R.drawable.ic_launcher_foreground));
        semillas.add(new Tecnica("Crochet", "Golpe curvo lateral para media y corta distancia.", "GOLPES", R.drawable.ic_launcher_foreground));

        // DEFENSA
        semillas.add(new Tecnica("Esquiva de tronco", "Movimiento lateral para evitar golpes rectos.", "DEFENSA", R.drawable.ic_launcher_foreground));
        semillas.add(new Tecnica("Parada", "Bloqueo con la palma para desviar el Jab.", "DEFENSA", R.drawable.ic_launcher_foreground));

        // PASOS
        semillas.add(new Tecnica("Paso Péndulo", "Balanceo para entrar y salir del rango de ataque.", "PASOS", R.drawable.ic_launcher_foreground));

        return semillas;
    }
}