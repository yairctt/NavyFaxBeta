package modelo;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;
import java.io.IOException;
import modelo.threads.*;

public class Juego {
    private final Dificultad dificultad;
    private Nave nave;
    private CopyOnWriteArrayList<Enemigo> enemigos;
    private CopyOnWriteArrayList<Disparo> disparos;
    private CopyOnWriteArrayList<Obstaculo> obstaculos;
    private CopyOnWriteArrayList<DisparoEnemigo> disparosEnemigos;
    private CopyOnWriteArrayList<Point> explosiones;
    private CopyOnWriteArrayList<PowerUp> powerUps;
    private int nivel;
    private int puntuacion;
    private volatile boolean juegoActivo;
    private volatile boolean jugadorVivo;
    private volatile boolean pausado;
    private int vidas;
    private Image iconoVida;
    private SoundManager soundManager;
    private Random random;

    // Cooldown de disparo del jugador
    private long ultimoDisparo;
    private static final int COOLDOWN_NORMAL_MS = 300;
    private static final int COOLDOWN_RAPIDO_MS = 120;
    private long tiempoInicioVelocidad;
    private static final int DURACION_VELOCIDAD_MS = 8000;

    // Formación de enemigos (dirección compartida con el hilo)
    private volatile int formacionDireccion = 1; // 1=derecha, -1=izquierda

    // Combo y eventos de kill para efectos visuales
    private volatile int combo = 0;
    private long ultimoKill = 0;
    private static final int VENTANA_COMBO_MS = 2000;
    private CopyOnWriteArrayList<int[]> eventosKill; // {x, y, puntos}

    // Vidas máximas según dificultad
    private int vidasMaximas;

    // Timestamp del último daño recibido (para efectos visuales)
    private volatile long ultimoDaño = 0;

    // Pantalla de nivel completado
    private volatile boolean nivelCompletado = false;
    private long tiempoFinNivel = 0;
    private static final int DURACION_PANTALLA_NIVEL_MS = 2500;

    private MovimientoEnemigosThread hiloEnemigos;
    private MovimientoDisparosThread hiloDisparos;
    private DetectorColisionesThread hiloColisiones;
    private MovimientoObstaculosThread hiloObstaculos;
    private DisparosEnemigosThread hiloDisparosEnemigos;
    private MovimientoDisparosEnemigosThread hiloMovimientoDisparosEnemigos;
    private ActualizacionEscudoThread hiloEscudo;
    private MovimientoPowerUpsThread hiloPowerUps;

    public Juego(Dificultad dificultad) {
        this.dificultad = dificultad;
        random = new Random();
        enemigos = new CopyOnWriteArrayList<>();
        disparos = new CopyOnWriteArrayList<>();
        obstaculos = new CopyOnWriteArrayList<>();
        disparosEnemigos = new CopyOnWriteArrayList<>();
        explosiones = new CopyOnWriteArrayList<>();
        powerUps = new CopyOnWriteArrayList<>();
        eventosKill = new CopyOnWriteArrayList<>();
        soundManager = new SoundManager();
        cargarIconoVida();
        reiniciarEstado();
    }

    private void reiniciarEstado() {
        vidasMaximas = dificultad.vidasIniciales;
        vidas = vidasMaximas;
        nivelCompletado = false;
        tiempoFinNivel  = 0;
        nivel = 1;
        puntuacion = 0;
        juegoActivo = true;
        jugadorVivo = true;
        pausado = false;
        ultimoDisparo = 0;
        tiempoInicioVelocidad = 0;
        formacionDireccion = 1;
        combo = 0;
        ultimoKill = 0;
        ultimoDaño = 0;
        nave = new Nave(300, 500);
        enemigos.clear();
        disparos.clear();
        obstaculos.clear();
        disparosEnemigos.clear();
        explosiones.clear();
        powerUps.clear();
        eventosKill.clear();
        inicializarEnemigos();
        inicializarObstaculos();
    }

    /** Llama esto tras construir el objeto para arrancar hilos y música. */
    public void iniciar() {
        iniciarHilos();
        soundManager.iniciarMusicaFondo();
    }

    /** Reinicia partida desde Game Over sin crear un nuevo objeto Juego. */
    public void reiniciar() {
        juegoActivo = false;
        detenerHilos();
        soundManager.detenerMusica();
        reiniciarEstado();
        iniciarHilos();
        soundManager.iniciarMusicaFondo();
    }

    // ── Pausa ────────────────────────────────────────────────────────────────

    public void pausar() {
        if (!juegoActivo) return;
        pausado = true;
        soundManager.pausarMusica();
    }

    public void reanudar() {
        pausado = false;
        soundManager.reanudarMusica();
    }

    public boolean isPausado() { return pausado; }

    // ── Inicialización ────────────────────────────────────────────────────────

    private void inicializarEnemigos() {
        enemigos.clear();
        formacionDireccion = 1; // reiniciar dirección al crear nueva oleada
        int total = 5 + (nivel * 2);
        int velocidadBase = (int) Math.max(1, Math.round((1 + nivel) * dificultad.multVelocidad));

        // Nivel múltiplo de 5 → agrega un Boss y reduce el resto de enemigos
        if (nivel % 5 == 0) {
            enemigos.add(new Boss(nivel));
            total = Math.max(3, total - 5);
        }

        for (int i = 0; i < total; i++) {
            int col = i % 8;
            int row = i / 8;
            int xPos = 40 + col * 65;
            int yPos = 90 + row * 55;

            Enemigo e;
            if (nivel >= 4 && i % 5 == 4) {
                e = new EnemigoTanque(xPos, yPos, nivel);
            } else if (nivel >= 2 && i % 4 == 3) {
                e = new EnemigoRapido(xPos, yPos, nivel);
            } else {
                e = new Enemigo(xPos, yPos, velocidadBase);
            }
            enemigos.add(e);
        }
    }

    private void inicializarObstaculos() {
        obstaculos.clear();
        int numero = 4 + nivel;
        for (int i = 0; i < numero; i++) {
            obstaculos.add(new Obstaculo(80 + (i * 90) % 480, 280 + (i % 3) * 60));
        }
    }

    private void iniciarHilos() {
        hiloEnemigos = new MovimientoEnemigosThread(this);
        hiloDisparos = new MovimientoDisparosThread(this);
        hiloColisiones = new DetectorColisionesThread(this);
        hiloObstaculos = new MovimientoObstaculosThread(this);
        hiloDisparosEnemigos = new DisparosEnemigosThread(this);
        hiloMovimientoDisparosEnemigos = new MovimientoDisparosEnemigosThread(this);
        hiloEscudo = new ActualizacionEscudoThread(this);
        hiloPowerUps = new MovimientoPowerUpsThread(this);

        hiloEnemigos.start();
        hiloDisparos.start();
        hiloColisiones.start();
        hiloObstaculos.start();
        hiloDisparosEnemigos.start();
        hiloMovimientoDisparosEnemigos.start();
        hiloEscudo.start();
        hiloPowerUps.start();
    }

    public void detenerHilos() {
        if (hiloEnemigos != null) hiloEnemigos.detener();
        if (hiloDisparos != null) hiloDisparos.detener();
        if (hiloColisiones != null) hiloColisiones.detener();
        if (hiloObstaculos != null) hiloObstaculos.detener();
        if (hiloDisparosEnemigos != null) hiloDisparosEnemigos.detener();
        if (hiloMovimientoDisparosEnemigos != null) hiloMovimientoDisparosEnemigos.detener();
        if (hiloEscudo != null) hiloEscudo.detener();
        if (hiloPowerUps != null) hiloPowerUps.detener();
        soundManager.detenerMusica();
    }

    // ── Disparo del jugador ───────────────────────────────────────────────────

    public void disparar() {
        long ahora = System.currentTimeMillis();
        int cooldown = tieneVelocidadActiva() ? COOLDOWN_RAPIDO_MS : COOLDOWN_NORMAL_MS;
        if (ahora - ultimoDisparo < cooldown) return;
        ultimoDisparo = ahora;

        switch (nave.getTipoDisparo()) {
            case TRIPLE:
                disparos.add(new Disparo(nave.getX() - 8, nave.getY()));
                disparos.add(new Disparo(nave.getX() + 15, nave.getY()));
                disparos.add(new Disparo(nave.getX() + 38, nave.getY()));
                break;
            case DOBLE:
                disparos.add(new Disparo(nave.getX() + 5, nave.getY()));
                disparos.add(new Disparo(nave.getX() + 25, nave.getY()));
                break;
            default:
                disparos.add(new Disparo(nave.getX() + 15, nave.getY()));
        }
        soundManager.reproducirDisparo();
    }

    private boolean tieneVelocidadActiva() {
        return System.currentTimeMillis() - tiempoInicioVelocidad < DURACION_VELOCIDAD_MS;
    }

    public boolean isVelocidadDisparoActiva() { return tieneVelocidadActiva(); }

    public void disparoEnemigo(int x, int y) {
        disparosEnemigos.add(new DisparoEnemigo(x, y));
    }

    // ── Power-ups ─────────────────────────────────────────────────────────────

    public void spawnearPowerUp(int x, int y) {
        if (random.nextInt(100) < 25) {
            PowerUp.Tipo[] tipos = PowerUp.Tipo.values();
            powerUps.add(new PowerUp(x + 5, y + 5, tipos[random.nextInt(tipos.length)]));
        }
    }

    public void activarPowerUp(PowerUp.Tipo tipo) {
        switch (tipo) {
            case DISPARO_DOBLE:
                nave.activarDisparoDoble();
                break;
            case ESCUDO_EXTRA:
                nave.activarEscudo();
                break;
            case VELOCIDAD_DISPARO:
                tiempoInicioVelocidad = System.currentTimeMillis();
                break;
        }
    }

    // ── Vida del jugador ──────────────────────────────────────────────────────

    public void perderVida() {
        ultimoDaño = System.currentTimeMillis();
        vidas--;
        if (vidas <= 0) {
            soundManager.reproducirGameOver();
            soundManager.detenerMusica();
            GestorPuntuaciones.guardarPuntuacion(puntuacion);
            eliminarJugador();
        } else {
            soundManager.reproducirDaño();
            nave = new Nave(300, 500);
            disparosEnemigos.clear();
        }
    }

    public void eliminarJugador() {
        jugadorVivo = false;
        juegoActivo = false;
    }

    // ── Puntuación y nivel ────────────────────────────────────────────────────

    public void incrementarPuntuacion(Enemigo e) {
        long ahora = System.currentTimeMillis();
        if (ahora - ultimoKill < VENTANA_COMBO_MS) combo++;
        else combo = 1;
        ultimoKill = ahora;

        int base;
        switch (e.getTipo()) {
            case BOSS:   base = 500; break;
            case TANQUE: base = 250; break;
            case RAPIDO: base = 150; break;
            default:     base = 100;
        }
        int ganancia = base * Math.min(combo, 8);
        puntuacion += ganancia;
        eventosKill.add(new int[]{e.getX(), e.getY(), ganancia});
    }

    /** Llamado por DetectorColisionesThread al eliminar el último enemigo. */
    public void verificarNivel() {
        if (enemigos.isEmpty() && jugadorVivo && !nivelCompletado) {
            nivelCompletado = true;
            tiempoFinNivel  = System.currentTimeMillis();
            powerUps.clear();
            disparosEnemigos.clear();
            soundManager.reproducirNivelCompleto();
        }
    }

    /** Llamado periódicamente por ActualizacionEscudoThread para avanzar cuando expira el timer. */
    public void checkAvanzarNivel() {
        if (nivelCompletado && System.currentTimeMillis() - tiempoFinNivel > DURACION_PANTALLA_NIVEL_MS) {
            nivelCompletado = false;
            nivel++;
            inicializarEnemigos();
            inicializarObstaculos();
        }
    }

    public boolean isNivelCompletado() { return nivelCompletado; }

    // ── Explosiones ───────────────────────────────────────────────────────────

    public void agregarExplosion(int x, int y) { explosiones.add(new Point(x, y)); }
    public void removerExplosion(Point p)       { explosiones.remove(p); }
    public void reproducirExplosion()           { soundManager.reproducirExplosion(); }

    // ── Recursos ──────────────────────────────────────────────────────────────

    private void cargarIconoVida() {
        try {
            iconoVida = ImageIO.read(getClass().getResourceAsStream("/recursos/imagenes/vida.png"))
                    .getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        } catch (IOException | IllegalArgumentException e) {
            iconoVida = null;
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Nave getNave()                                            { return nave; }
    public CopyOnWriteArrayList<Enemigo> getEnemigos()              { return enemigos; }
    public CopyOnWriteArrayList<Disparo> getDisparos()              { return disparos; }
    public CopyOnWriteArrayList<Obstaculo> getObstaculos()          { return obstaculos; }
    public CopyOnWriteArrayList<DisparoEnemigo> getDisparosEnemigos(){ return disparosEnemigos; }
    public CopyOnWriteArrayList<Point> getExplosiones()             { return explosiones; }
    public CopyOnWriteArrayList<PowerUp> getPowerUps()              { return powerUps; }
    public CopyOnWriteArrayList<int[]> getEventosKill()             { return eventosKill; }
    public int getNivel()                                            { return nivel; }
    public int getPuntuacion()                                       { return puntuacion; }
    public int getVidas()                                            { return vidas; }
    public int getVidasMaximas()                                     { return vidasMaximas; }
    public Dificultad getDificultad()                                { return dificultad; }
    public double getMultiplicadorDisparo()                          { return dificultad.multDisparo; }
    public int getCombo()                                            { return combo; }
    public Image getIconoVida()                                      { return iconoVida; }
    public boolean isJuegoActivo()                                   { return juegoActivo; }
    public boolean isJugadorVivo()                                   { return jugadorVivo; }
    public long getUltimoDaño()                                      { return ultimoDaño; }

    public int getFormacionDireccion()                               { return formacionDireccion; }
    public void setFormacionDireccion(int d)                         { formacionDireccion = d; }

    /** Progreso de recarga del disparo (0.0 = recargando, 1.0 = listo). */
    public double getProgresoRecarga() {
        int cooldown = tieneVelocidadActiva() ? COOLDOWN_RAPIDO_MS : COOLDOWN_NORMAL_MS;
        return Math.min(1.0, (double)(System.currentTimeMillis() - ultimoDisparo) / cooldown);
    }
}
