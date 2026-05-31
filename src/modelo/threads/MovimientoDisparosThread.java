package modelo.threads;

import modelo.Disparo;
import modelo.Juego;

public class MovimientoDisparosThread extends Thread {
    private Juego juego;
    private volatile boolean ejecutando;

    public MovimientoDisparosThread(Juego juego) {
        super("Hilo-Disparos");
        this.juego = juego;
        this.ejecutando = true;
    }

    @Override
    public void run() {
        while (ejecutando && juego.isJuegoActivo()) {
            esperarSiPausado();
            for (Disparo disparo : juego.getDisparos()) {
                disparo.mover();
                if (disparo.getY() < 0) juego.getDisparos().remove(disparo);
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
