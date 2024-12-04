package modelo.threads;

import modelo.*;

public class MovimientoEnemigosThread extends Thread {
    private Juego juego;
    private volatile boolean ejecutando;

    public MovimientoEnemigosThread(Juego juego) {
        super("Hilo-Enemigos"); // nombre del hilo
        this.juego = juego;
        this.ejecutando = true;
    }

    @Override
    public void run() {
        System.out.println("Iniciando " + Thread.currentThread().getName());
        while (ejecutando && juego.isJuegoActivo()) {
            System.out.println(Thread.currentThread().getName() + " - Moviendo " + juego.getEnemigos().size() + " enemigos");
            for (Enemigo enemigo : juego.getEnemigos()) {
                enemigo.mover();
            }
            try {
                int tiempoEspera = Math.max(20, 50 - (juego.getNivel() * 3));
                Thread.sleep(tiempoEspera);
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
