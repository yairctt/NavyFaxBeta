package modelo.threads;

import modelo.Juego;
import modelo.DisparoEnemigo;

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
        System.out.println("Iniciando " + Thread.currentThread().getName());
        while (ejecutando && juego.isJuegoActivo()) {
            for (DisparoEnemigo disparo : juego.getDisparosEnemigos()) {
                disparo.mover();
                // Eliminar disparos que salen de la pantalla
                if (disparo.getY() > 600) {
                    juego.getDisparosEnemigos().remove(disparo);
                    System.out.println(Thread.currentThread().getName() + " - Disparo enemigo eliminado");
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