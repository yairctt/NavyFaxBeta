package modelo.threads;

import modelo.Juego;
import modelo.Obstaculo;

public class MovimientoObstaculosThread extends Thread {
    private Juego juego;
    private volatile boolean ejecutando;

    public MovimientoObstaculosThread(Juego juego) {
        super("Hilo-Obstaculos");
        this.juego = juego;
        this.ejecutando = true;
    }

    @Override
    public void run() {
        while (ejecutando && juego.isJuegoActivo()) {
            esperarSiPausado();
            for (Obstaculo obstaculo : juego.getObstaculos()) {
                obstaculo.mover();
            }
            try {
                Thread.sleep(100);
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
