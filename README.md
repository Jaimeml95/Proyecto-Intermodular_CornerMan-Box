CornerMan Box 🥊
CornerMan Box es una aplicación Android diseñada para optimizar el entrenamiento de boxeo de forma autónoma. El proyecto busca centralizar la gestión de tiempos, la formación técnica y la motivación del atleta en una única herramienta "manos libres".

Este proyecto se desarrolla como Proyecto Intermodular para el Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM).

🚀 Estado del Proyecto: Fase 2 Finalizada
Actualmente, el repositorio refleja la culminación de la Fase de Lógica de Negocio y Persistencia (20/04 - 28/04).

Implementaciones actuales:
Motor de Entrenamiento Pro: Cronómetro de alta precisión gestionado mediante un Foreground Service. Soporta asaltos de 3 minutos, descansos de 1 minuto y sistema de validación (el entrenamiento solo computa tras completar el primer asalto).

Sincronización en Tiempo Real: Implementación de un protocolo de comunicación entre el Servicio y la UI para evitar parpadeos y pérdida de estado al navegar entre fragmentos.

Persistencia Avanzada con Room: Gestión integral del perfil de usuario, puntos acumulados y fecha del último entrenamiento.

Sistema de Gamificación: Lógica de rachas basada en días naturales (calendario) compatible con API 24, premiando la constancia diaria del atleta.

Biblioteca Técnica Dinámica: Interfaz optimizada con efectos de iluminación neón y filtrado dinámico de categorías (Golpes, Defensa, Pasos, Combos).

Navegación Robusta: Sincronización total entre el BottomNavigationView y acciones contextuales (como el botón "Empezar ya" del Dashboard).

✨ Características Principales (Roadmap)
[x] Estructura y Navegación Base.

[x] Motor de Entrenamiento: Cronómetro de alta precisión mediante Foreground Service.

[x] Persistencia y Gamificación: Sistema de puntos y rachas de entrenamiento mediante Room.

[ ] Control Hands-Free: Comandos de voz mediante Google Speech-to-Text. (Próximamente)

[ ] Feedback Auditivo: Implementación de sonidos de campana y avisos de tiempo. (Próximamente)

[ ] Sincronización Musical: Integración con Spotify App Remote SDK.

[ ] Biblioteca Técnica: Catálogo interactivo y detallado de técnicas.

🛠️ Tecnologías utilizadas
Lenguaje: Java 8+

IDE: Android Studio (Ladybug / Jellyfish)

Base de datos: Room Persistence Library

Arquitectura: Estructura de paquetes siguiendo patrones MVVM y Servicios de Primer Plano.

API Mínima: Compatible desde Android 7.0 (API 24).

Control de versiones: Git & GitHub

Autor: Jaime Moro
Centro: IES AUGUSTÓBRIGA
Curso: 2025/2026