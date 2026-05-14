package com.jaimemoro.cornermanbox.data;

import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.data.entities.Tecnica;
import java.util.ArrayList;
import java.util.List;

public class DataSeeder {

    public static List<Tecnica> getTecnicasSemilla() {
        List<Tecnica> semillas = new ArrayList<>();

        // --- GOLPES ---
        semillas.add(new Tecnica("Jab", "Golpe recto con la mano delantera. Rápido y para medir distancia.", "GOLPES", R.drawable.img_jab, "url_video"));
        semillas.add(new Tecnica("Cross", "Golpe directo potente con la mano trasera.", "GOLPES", R.drawable.img_cross, "url_video"));
        semillas.add(new Tecnica("Hook", "Gancho lateral dirigido a la mandíbula o sienes.", "GOLPES", R.drawable.img_hook, "url_video"));
        semillas.add(new Tecnica("Uppercut", "Gancho ascendente desde abajo hacia el mentón del rival.", "GOLPES", R.drawable.img_uppercut, "url_video"));

        // --- DEFENSA ---
        semillas.add(new Tecnica("Esquiva de tronco", "Movimiento de cintura para evitar golpes rectos.", "DEFENSA", R.drawable.img_slip, "url_video"));
        semillas.add(new Tecnica("Parada", "Uso de la palma para desviar el impacto del Jab.", "DEFENSA", R.drawable.img_parry, "url_video"));

        // --- PASOS ---
        semillas.add(new Tecnica("Paso Péndulo", "Balanceo constante para entrar y salir de la zona de fuego.", "PASOS", R.drawable.img_pendulo, "url_video"));
        semillas.add(new Tecnica("Paso Lateral", "Desplazamiento a izquierda o derecha manteniendo la guardia.", "PASOS", R.drawable.img_sidestep, "url_video"));
        semillas.add(new Tecnica("Pivote", "Giro sobre el pie delantero para cambiar el ángulo de ataque.", "PASOS", R.drawable.img_pivot, "url_video"));

        // --- COMBOS ---
        semillas.add(new Tecnica("Uno-Dos (1-2)", "Combinación básica de Jab seguido de un Directo de derecha.", "COMBOS", R.drawable.img_combo, "url_video"));
        semillas.add(new Tecnica("Combo Clásico (1-2-3)", "Jab, Directo y Crochet de izquierda. El combo fundamental.", "COMBOS", R.drawable.img_combo, "url_video"));

        return semillas;
    }
}