package modelo.threads;

import modelo.Juego;

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
            // Siempre avanza el timer de nivel completado (incluso si está pausado)
            juego.checkAvanzarNivel();

            if (!juego.isPausado()) {
                juego.getNave().actualizarEscudo();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void detener() { ejecutando = false; }
}
