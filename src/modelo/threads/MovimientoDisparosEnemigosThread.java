package modelo.threads;

import modelo.DisparoEnemigo;
import modelo.Juego;

public class MovimientoDisparosEnemigosThread extends Thread {
    private Juego juego;
    private volatile boolean ejecutando;

    public MovimientoDisparosEnemigosThread(Juego juego) {
        super("Hilo-Movimiento-Disparos-Enemigos");
        this.juego = juego;
        this.ejecutando = true;
    }

    @Override
    public void run() {
        while (ejecutando && juego.isJuegoActivo()) {
            esperarSiPausado();
            for (DisparoEnemigo disparo : juego.getDisparosEnemigos()) {
                disparo.mover();
                if (disparo.getY() > 620) juego.getDisparosEnemigos().remove(disparo);
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void esperarSiPausado() {
        while ((juego.isPausado() || juego.isNivelCompletado()) && ejecutando) {
            try { Thread.sleep(50); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public void detener() { ejecutando = false; }
}
