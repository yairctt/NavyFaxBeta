package modelo.threads;

import modelo.Juego;
import modelo.PowerUp;

public class MovimientoPowerUpsThread extends Thread {
    private Juego juego;
    private volatile boolean ejecutando;

    public MovimientoPowerUpsThread(Juego juego) {
        super("Hilo-PowerUps");
        this.juego = juego;
        this.ejecutando = true;
    }

    @Override
    public void run() {
        while (ejecutando && juego.isJuegoActivo()) {
            esperarSiPausado();
            for (PowerUp p : juego.getPowerUps()) {
                p.mover();
                if (p.getY() > 620) juego.getPowerUps().remove(p);
            }
            try {
                Thread.sleep(30);
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
