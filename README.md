# NavyFax

Un juego arcade estilo Space Invaders desarrollado en Java usando Swing y programación concurrente con múltiples hilos.

## Acerca del proyecto

NavyFax es un shooter arcade 2D donde el jugador controla una nave espacial y debe eliminar oleadas de enemigos antes de que lleguen a la parte inferior de la pantalla. En cada nivel los enemigos se mueven más rápido y aparecen en mayor cantidad. El jugador cuenta con 10 vidas y gana 100 puntos por cada enemigo destruido.

## Jugabilidad

- Mueve tu nave a izquierda y derecha para esquivar disparos enemigos
- Dispara a los enemigos para ganar puntos
- Los obstáculos bloquean disparos — úsalos estratégicamente
- Después de recibir daño, se activa un escudo temporal que te da unos segundos de recuperación
- Elimina a todos los enemigos para avanzar al siguiente nivel
- Sobrevive el mayor tiempo posible y supera tu puntuación máxima

### Controles

| Tecla        | Acción           |
|--------------|------------------|
| Flecha Izq.  | Mover izquierda  |
| Flecha Der.  | Mover derecha    |
| Espacio      | Disparar         |

## Características

- **Progresión de niveles** — cada nivel genera más enemigos y aumenta su velocidad
- **IA de enemigos** — los enemigos se mueven en un patrón de ola en zigzag y también disparan al jugador
- **Sistema de escudo** — invencibilidad temporal después de recibir daño, mostrada con una barra de duración
- **Explosiones animadas** — animación de explosión de 8 frames al destruir enemigos
- **Efectos de sonido** — disparos, daño, explosiones, game over y música de fondo en bucle
- **HUD** — puntuación en tiempo real, contador de nivel e íconos visuales de vidas en pantalla

## Arquitectura

El proyecto sigue el patrón **MVC (Modelo-Vista-Controlador)**:

```text
src/
├── controlador/
│   └── JuegoControlador.java   # Entrada del teclado, gestión de ventana y temporizador de renderizado
├── modelo/
│   ├── Juego.java              # Estado principal del juego y lógica
│   ├── Nave.java               # Nave del jugador (movimiento, escudo)
│   ├── Enemigo.java            # Entidad enemiga (patrón de movimiento en ola)
│   ├── Disparo.java            # Proyectil del jugador
│   ├── DisparoEnemigo.java     # Proyectil enemigo
│   ├── Obstaculo.java          # Entidad obstáculo
│   ├── SoundManager.java       # Reproducción de audio (WAV)
│   └── threads/
│       ├── MovimientoEnemigosThread.java           # Bucle de movimiento de enemigos
│       ├── MovimientoDisparosThread.java           # Movimiento de balas del jugador
│       ├── MovimientoDisparosEnemigosThread.java   # Movimiento de balas enemigas
│       ├── MovimientoObstaculosThread.java         # Movimiento de obstáculos
│       ├── DisparosEnemigosThread.java             # Lógica de disparos enemigos
│       ├── DetectorColisionesThread.java           # Detección de colisiones
│       └── ActualizacionEscudoThread.java          # Actualización del temporizador del escudo
└── vista/
    └── JuegoPanel.java         # Panel de renderizado en Swing
```

### Concurrencia

El juego ejecuta **7 hilos dedicados** simultáneamente, cada uno encargado de una parte específica del game loop:

| Hilo | Responsabilidad |
|---|---|
| `MovimientoEnemigosThread` | Mueve todos los enemigos en cada actualización |
| `MovimientoDisparosThread` | Mueve las balas del jugador hacia arriba |
| `MovimientoDisparosEnemigosThread` | Mueve las balas enemigas hacia abajo |
| `MovimientoObstaculosThread` | Mueve los obstáculos por la pantalla |
| `DisparosEnemigosThread` | Genera disparos enemigos aleatorios |
| `DetectorColisionesThread` | Detecta intersecciones de hitboxes |
| `ActualizacionEscudoThread` | Gestiona la duración del escudo |

Todo el estado compartido del juego utiliza `CopyOnWriteArrayList` para acceso seguro entre hilos sin bloqueos.

## Tecnologías utilizadas

- **Lenguaje:** Java
- **Interfaz gráfica:** Java Swing
- **Concurrencia:** Java Threads (`java.lang.Thread`)
- **Audio:** Java Sound API (`javax.sound.sampled`)
- **IDE:** IntelliJ IDEA

## Cómo ejecutar

1. Abre el proyecto en IntelliJ IDEA
2. Configura `src` como source root
3. Ejecuta `controlador/JuegoControlador.java` — el método `main` es el punto de entrada

```java
// Punto de entrada
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        Juego modelo = new Juego();
        JuegoPanel vista = new JuegoPanel(modelo);
        new JuegoControlador(modelo, vista);
    });
}
```

## Capturas de pantalla

![Captura del Juego](./screenshots/ss_lvl1.png)
![Captura del Juego](./screenshots/ss_lvl2.png)
![Captura del Juego](./screenshots/ss_gameover.png)

## Autor

Desarrollado como proyecto personal para practicar concurrencia en Java, patrones de diseño orientados a objetos y fundamentos de desarrollo de videojuegos.
