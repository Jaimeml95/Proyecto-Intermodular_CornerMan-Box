
# CornerMan Box 🥊

CornerMan Box es una aplicación Android de alto rendimiento diseñada para optimizar el entrenamiento de boxeo de forma autónoma. La aplicación centraliza la gestión de tiempos, la formación técnica y la motivación del atleta mediante un sistema totalmente manos libres, integrando reconocimiento de voz local y control musical inteligente.

Este proyecto se desarrolla como Proyecto Intermodular para el Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM).

## 🚀 Estado del Proyecto: Fase 3 Finalizada (Integración y Control Hands-Free)
El repositorio actual refleja la implementación de sistemas avanzados de control y servicios de terceros, eliminando la dependencia de servicios en la nube para las funciones principales.

Implementaciones Destacadas:
Control de Voz Offline (Vosk SDK): Sustitución del motor de Google por Vosk. Esto permite un reconocimiento de comandos ("Continuar", "Tiempo", "Música", "Silencio") totalmente privado, sin cortes en el audio y sin los ruidos de sistema ("beeps") habituales.

Integración con Spotify SDK: Control total de la música mediante el Spotify App Remote SDK. La música se sincroniza con el estado del entrenamiento y responde a comandos de voz.

Lógica de Reproducción Inteligente: Implementación de un sistema de "retroceso inteligente" que detecta la posición de la canción (umbral de 10s) para decidir entre reiniciar el track o saltar al anterior mediante comandos de voz.

Automatización con Gradle (Kotlin DSL): Tarea personalizada para la generación dinámica de archivos uuid del modelo de lenguaje, asegurando la integridad del sistema de archivos de Vosk en cada compilación.

Motor de Entrenamiento Pro: Foreground Service de alta precisión que gestiona el cronómetro, los avisos sonoros (campana) y ahora los flujos de audio y voz de forma simultánea.

Persistencia y Gamificación: Gestión de perfiles y rachas de entrenamiento mediante Room, con validación de constancia diaria.

## ✨ Características Principales (Roadmap)
[x] Estructura y Navegación Base: Arquitectura de Fragmentos y BottomNavigationView.

[x] Motor de Entrenamiento: Cronómetro de precisión mediante Foreground Service.

[x] Persistencia y Gamificación: Sistema de puntos y rachas mediante Room.

[x] Control Hands-Free: Comandos de voz offline mediante Vosk (Privado y Silencioso).

[x] Sincronización Musical: Integración avanzada con Spotify App Remote.

[x] Biblioteca Técnica: Catálogo dinámico con efectos neón, filtrado por categorías.

## 🛠️ Tecnologías utilizadas
Lenguaje: Java 8+ / Gradle Kotlin DSL

Reconocimiento de Voz: Vosk-Android SDK (Offline Speech Recognition)

Música: Spotify App Remote SDK

Base de Datos: Room Persistence Library

Arquitectura: Patrón MVVM, Servicios de primer plano (Foreground Services) y Callbacks asíncronos.

API Mínima: Android 7.0 (API 24) para compatibilidad extendida.

## 📦 Instalación y Configuración del Modelo
Para que el sistema de voz funcione, el proyecto incluye una tarea automática en el build.gradle.kts que gestiona el modelo de lenguaje en la carpeta assets/model-es. Es necesario que el desarrollador proporcione los archivos del modelo de Vosk en dicha ruta para su correcta sincronización en el dispositivo.

### Autor: Jaime Moro López

#### Centro: IES AUGUSTÓBRIGA

#### Curso: 2025/2026
