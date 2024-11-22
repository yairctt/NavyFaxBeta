package modelo.threads;

import modelo.*;
import java.awt.*;

public class DetectorColisionesThread extends Thread {
    private Juego juego;
    private volatile boolean ejecutando;

    public DetectorColisionesThread(Juego juego) {
        super("Hilo-Colisiones"); // Nombre del hilo
        this.juego = juego;
        this.ejecutando = true;
    }

    @Override
    public void run() {
        System.out.println("Iniciando " + Thread.currentThread().getName());
        while (ejecutando && juego.isJuegoActivo()) {
            for (Disparo disparo : juego.getDisparos()) {
                for (Enemigo enemigo : juego.getEnemigos()) {
                    if (colision(disparo.getBounds(), enemigo.getBounds())) {
                        juego.getDisparos().remove(disparo);
                        juego.getEnemigos().remove(enemigo);
                        juego.incrementarPuntuacion();
                        System.out.println(Thread.currentThread().getName() +
                                " - ¡Colisión detectada! Puntuación: " +
                                juego.getPuntuacion() +
                                " Enemigos restantes: " +
                                juego.getEnemigos().size());
                        juego.verificarNivel();
                        break;
                    }
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " interrumpido");
            }
        }
        System.out.println(Thread.currentThread().getName() + " finalizado");
    }

    private boolean colision(Rectangle r1, Rectangle r2) {
        return r1.intersects(r2);
    }

    public void detener() {
        System.out.println("Deteniendo " + Thread.currentThread().getName());
        ejecutando = false;
    }
}