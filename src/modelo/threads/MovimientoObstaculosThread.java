package modelo.threads;

import modelo.*;

public class MovimientoObstaculosThread extends Thread {
    private Juego juego;
    private volatile boolean ejecutando;

    public MovimientoObstaculosThread(Juego juego) {
        super("Hilo-MovimientoObstaculos");
        this.juego = juego;
        this.ejecutando = true;
    }

    @Override
    public void run() {
        System.out.println("Iniciando " + Thread.currentThread().getName());
        while (ejecutando && juego.isJuegoActivo()) {
            System.out.println(Thread.currentThread().getName() + " - Moviendo " + juego.getObstaculos().size() + " obstaculos");
            // Mueve cada obstáculo de izquierda a derecha
            for (Obstaculo obstaculo : juego.getObstaculos()) {
                obstaculo.mover(); // Mueve el obstáculo hacia la derecha
            }

            // Puedes agregar una pequeña pausa para controlar la velocidad de los obstáculos
            try {
                Thread.sleep(100); // Espera 100 ms antes de mover los obstáculos nuevamente
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


