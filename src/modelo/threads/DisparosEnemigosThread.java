package modelo.threads;

import modelo.Juego;
import modelo.Enemigo;
import java.util.Random;

public class DisparosEnemigosThread extends Thread {
    private Juego juego;
    private volatile boolean ejecutando;
    private Random random;
    private static final int PROBABILIDAD_DISPARO = 50; // Porcentaje de probabilidad de disparo

    public DisparosEnemigosThread(Juego juego) {
        super("Hilo-Disparos-Enemigos");
        this.juego = juego;
        this.ejecutando = true;
        this.random = new Random();
    }

    @Override
    public void run() {
        System.out.println("Iniciando " + Thread.currentThread().getName());
        while (ejecutando && juego.isJuegoActivo()) {
            // Cada enemigo tiene una probabilidad de disparar
            for (Enemigo enemigo : juego.getEnemigos()) {
                if (random.nextInt(100) < PROBABILIDAD_DISPARO * juego.getNivel()) {
                    juego.disparoEnemigo(enemigo.getX() + 15, enemigo.getY() + 30);
                }
            }
            try {
                Thread.sleep(1000); // Espera 1 segundo entre intentos de disparo
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
