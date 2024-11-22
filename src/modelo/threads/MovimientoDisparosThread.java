package modelo.threads;
import modelo.*;

public class MovimientoDisparosThread extends Thread {
    private Juego juego;
    private volatile boolean ejecutando;

    public MovimientoDisparosThread(Juego juego) {
        super("Hilo-Disparos"); // nombre del hilo
        this.juego = juego;
        this.ejecutando = true;
    }

    @Override
    public void run() {
        System.out.println("Iniciando " + Thread.currentThread().getName());
        while (ejecutando && juego.isJuegoActivo()) {
            if (!juego.getDisparos().isEmpty()) {
                System.out.println(Thread.currentThread().getName() + " - Moviendo " + juego.getDisparos().size() + " disparos");
            }
            for (Disparo disparo : juego.getDisparos()) {
                disparo.mover();
                if (disparo.getY() < 0) {
                    juego.getDisparos().remove(disparo);
                    System.out.println(Thread.currentThread().getName() + " - Disparo eliminado fuera de pantalla");
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " interrumpido");
            }
        }
        System.out.println(Thread.currentThread().getName() + " finalizado");
    }

    public void detener() {
        System.out.println("Deteniendo " + Thread.currentThread().getName());
        ejecutando = false;
    }
}
