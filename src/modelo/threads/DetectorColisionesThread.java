package modelo.threads;

import modelo.*;
import java.awt.*;

public class DetectorColisionesThread extends Thread {
    private Juego juego;
    private volatile boolean ejecutando;

    public DetectorColisionesThread(Juego juego) {
        super("Hilo-Colisiones");
        this.juego = juego;
        this.ejecutando = true;
    }

    @Override
    public void run() {
        while (ejecutando && juego.isJuegoActivo()) {
            esperarSiPausado();
            detectarDisparosVsEnemigos();
            detectarDisparosVsObstaculos();
            detectarDisparosEnemigosVsNave();
            detectarPowerUpsVsNave();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void detectarDisparosVsEnemigos() {
        for (Disparo disparo : juego.getDisparos()) {
            for (Enemigo enemigo : juego.getEnemigos()) {
                if (colision(disparo.getBounds(), enemigo.getBounds())) {
                    juego.getDisparos().remove(disparo);
                    boolean muerto = enemigo.recibirDaño();
                    if (muerto) {
                        juego.getEnemigos().remove(enemigo);
                        juego.agregarExplosion(enemigo.getX(), enemigo.getY());
                        juego.reproducirExplosion();
                        juego.incrementarPuntuacion(enemigo);
                        juego.spawnearPowerUp(enemigo.getX(), enemigo.getY());
                        juego.verificarNivel();
                    }
                    break;
                }
            }
        }
    }

    private void detectarDisparosVsObstaculos() {
        for (Disparo disparo : juego.getDisparos()) {
            for (Obstaculo obstaculo : juego.getObstaculos()) {
                if (colision(disparo.getBounds(), obstaculo.getBounds())) {
                    juego.getDisparos().remove(disparo);
                    break;
                }
            }
        }
    }

    private void detectarDisparosEnemigosVsNave() {
        for (DisparoEnemigo disparo : juego.getDisparosEnemigos()) {
            if (colision(disparo.getBounds(), juego.getNave().getBounds())) {
                juego.getDisparosEnemigos().remove(disparo);
                if (!juego.getNave().tieneEscudo()) {
                    juego.perderVida();
                    juego.getNave().activarEscudo();
                }
                break;
            }
        }
    }

    private void detectarPowerUpsVsNave() {
        for (PowerUp powerUp : juego.getPowerUps()) {
            if (colision(powerUp.getBounds(), juego.getNave().getBounds())) {
                juego.getPowerUps().remove(powerUp);
                juego.activarPowerUp(powerUp.getTipo());
            }
        }
    }

    private boolean colision(Rectangle r1, Rectangle r2) {
        return r1.intersects(r2);
    }

    private void esperarSiPausado() {
        while (juego.isPausado() && ejecutando) {
            try { Thread.sleep(50); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public void detener() { ejecutando = false; }
}
