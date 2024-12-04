package modelo.threads;

import modelo.*;

public class ActualizacionEscudoThread extends Thread {
    private Juego juego;
    private volatile boolean ejecutando;

    public ActualizacionEscudoThread(Juego juego) {
        super("Hilo-Escudo");
        this.juego = juego;
        this.ejecutando = true;
    }

    @Override
    public void run() {
        while (ejecutando && juego.isJuegoActivo()) {
            juego.getNave().actualizarEscudo();
            try {
                Thread.sleep(100); // Actualizar cada 100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void detener() {
        ejecutando = false;
    }
}