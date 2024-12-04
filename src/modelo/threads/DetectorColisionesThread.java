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
            // Detectar colisiones entre disparos y enemigos
            for (Disparo disparo : juego.getDisparos()) {
                for (Enemigo enemigo : juego.getEnemigos()) {
                    if (colision(disparo.getBounds(), enemigo.getBounds())) {
                        juego.getDisparos().remove(disparo);
                        juego.getEnemigos().remove(enemigo);
                        juego.agregarExplosion(enemigo.getX(), enemigo.getY());
                        juego.reproducirExplosion();
                        juego.incrementarPuntuacion();
                        System.out.println(Thread.currentThread().getName() +
                                " - ¡Colisión con enemigo detectada! Puntuación: " +
                                juego.getPuntuacion() +
                                " Enemigos restantes: " +
                                juego.getEnemigos().size());
                        juego.verificarNivel();
                        break;
                    }
                }
            }

            // Detectar colisiones entre disparos y obstáculos
            for (Disparo disparo : juego.getDisparos()) {
                for (Obstaculo obstaculo : juego.getObstaculos()) {
                    if (colision(disparo.getBounds(), obstaculo.getBounds())) {
                        juego.getDisparos().remove(disparo);
                        System.out.println(Thread.currentThread().getName() +
                                " - ¡Colisión con obstáculo detectada!");
                        break; // Sale del bucle de obstáculos si hubo una colisión
                    }
                }
            }

            for (DisparoEnemigo disparo : juego.getDisparosEnemigos()) {
                if (colision(disparo.getBounds(), juego.getNave().getBounds())) {
                    juego.getDisparosEnemigos().remove(disparo);
                    if (!juego.getNave().tieneEscudo()) {
                        juego.perderVida();
                        // Activar escudo temporal después de perder una vida
                        juego.getNave().activarEscudo();
                    }
                    break;
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

    // Método para verificar si dos rectángulos colisionan
    private boolean colision(Rectangle r1, Rectangle r2) {
        return r1.intersects(r2);
    }

    // Método para detener el hilo
    public void detener() {
        System.out.println("Deteniendo " + Thread.currentThread().getName());
        ejecutando = false;
    }
}
