# NavyFax Project Guide

Este documento contiene las convenciones, arquitectura y comandos esenciales para el desarrollo de NavyFax.

## 🚀 Información del Proyecto
NavyFax es un arcade shooter 2D desarrollado en Java Swing que utiliza un modelo de **concurrencia intensiva** (7+ hilos independientes) para gestionar el estado del juego en tiempo real.

## 🛠 Comandos Vitales
- **Compilar:** `javac -d out -sourcepath src src/controlador/JuegoControlador.java`
- **Ejecutar:** `java -cp out controlador.JuegoControlador`
- **Estructura de Carpetas:** El proyecto usa `src` como Source Root. Los recursos deben estar en `src/recursos/`.

## 🏗 Arquitectura Técnica
El proyecto sigue un patrón **MVC (Modelo-Vista-Controlador)** estricto:

1.  **Modelo (`modelo/`)**:
    - Contiene el estado (`Juego.java`) y las entidades (`Nave`, `Enemigo`, etc.).
    - **Hilos (`modelo/threads/`)**: Cada sistema (movimiento, colisiones, disparos) corre en su propio `Thread` con un bucle `while(ejecutando)`.
    - **Thread Safety**: Se utiliza `CopyOnWriteArrayList` para todas las colecciones compartidas entre hilos para evitar `ConcurrentModificationException`.

2.  **Vista (`vista/`)**:
    - `JuegoPanel.java` se encarga exclusivamente del renderizado (`paintComponent`) y animaciones visuales usando un `javax.swing.Timer`.

3.  **Controlador (`controlador/`)**:
    - `JuegoControlador.java` gestiona la ventana (`JFrame`), los inputs del teclado (`KeyListener`) y el ciclo de refresco de la vista.

## 📏 Convenciones de Código
- **Nomenclatura**:
    - Clases: `PascalCase` (ej. `MovimientoEnemigosThread`).
    - Métodos/Variables: `camelCase` (ej. `inicializarEnemigos()`).
    - Constantes: `UPPER_SNAKE_CASE` (ej. `TOTAL_FRAMES`).
- **Idioma**: El código (clases, métodos, variables) debe estar en **Español** (ej. `getDisparosEnemigos`), pero los comentarios técnicos pueden ser mixtos.
- **Concurrencia**:
    - Siempre usar `CopyOnWriteArrayList` para entidades dinámicas.
    - Los hilos deben implementar un método `detener()` que cambie un flag `boolean`.
    - Mantener el `Thread.sleep()` en los hilos para no saturar la CPU (típicamente entre 10ms y 50ms).

## 💡 Consideraciones para la IA
- **Recursos**: Siempre verifica si los recursos (imágenes/sonidos) existen antes de usarlos para evitar `NullPointerException`. Usa `getClass().getResourceAsStream()`.
- **Nuevas Funcionalidades**: Si vas a añadir una lógica de movimiento nueva, crea un hilo separado en `modelo/threads/` y regístralo en el método `iniciarHilos()` de `Juego.java`.
- **Coordenadas**: El área de juego estándar es de `600x600` píxeles.