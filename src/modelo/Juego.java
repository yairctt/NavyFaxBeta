package modelo;


import java.util.concurrent.CopyOnWriteArrayList;

import modelo.threads.MovimientoEnemigosThread;
import modelo.threads.MovimientoDisparosThread;
import modelo.threads.DetectorColisionesThread;

public class Juego {
    private Nave nave;
    private CopyOnWriteArrayList<Enemigo> enemigos;
    private CopyOnWriteArrayList<Disparo> disparos;
    private int nivel;
    private int puntuacion;
    private boolean juegoActivo;
    private MovimientoEnemigosThread hiloEnemigos;
    private MovimientoDisparosThread hiloDisparos;
    private DetectorColisionesThread hiloColisiones;

    public Juego() {
        nave = new Nave(300, 500);
        enemigos = new CopyOnWriteArrayList<>();
        disparos = new CopyOnWriteArrayList<>();
        nivel = 1;
        puntuacion = 0;
        juegoActivo = true;
        inicializarEnemigos();
        iniciarHilos();
    }

    private void inicializarEnemigos() {
        enemigos.clear();
        int numeroEnemigos = 5 + (nivel * 2);
        int velocidad = 1 + nivel;

        for (int i = 0; i < numeroEnemigos; i++) {
            enemigos.add(new Enemigo(50 + (i * 80), 50, velocidad));
        }
    }

    private void iniciarHilos() {
        hiloEnemigos = new MovimientoEnemigosThread(this);
        hiloDisparos = new MovimientoDisparosThread(this);
        hiloColisiones = new DetectorColisionesThread(this);

        hiloEnemigos.start();
        hiloDisparos.start();
        hiloColisiones.start();
    }

    public void detenerHilos() {
        hiloEnemigos.detener();
        hiloDisparos.detener();
        hiloColisiones.detener();
    }

    public void verificarNivel() {
        if (enemigos.isEmpty()) {
            nivel++;
            if (nivel <= 3) {
                inicializarEnemigos();
            } else {
                juegoActivo = false;
                detenerHilos();
            }
        }
    }

    // Getters y setters
    public Nave getNave() { return nave; }
    public CopyOnWriteArrayList<Enemigo> getEnemigos() { return enemigos; }
    public CopyOnWriteArrayList<Disparo> getDisparos() { return disparos; }
    public int getNivel() { return nivel; }
    public int getPuntuacion() { return puntuacion; }
    public boolean isJuegoActivo() { return juegoActivo; }
    public void incrementarPuntuacion() { puntuacion += 100; }

    public void disparar() {
        disparos.add(new Disparo(nave.getX() + 15, nave.getY()));
    }
}

