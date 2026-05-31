package modelo.threads;

import modelo.Enemigo;
import modelo.Juego;

public class MovimientoEnemigosThread extends Thread {
    private Juego juego;
    private volatile boolean ejecutando;

    public MovimientoEnemigosThread(Juego juego) {
        super("Hilo-Enemigos");
        this.juego = juego;
        this.ejecutando = true;
    }

    @Override
    public void run() {
        while (ejecutando && juego.isJuegoActivo()) {
            esperarSiPausado();
            moverFormacion();
            moverIndependientes();
            try {
                int espera = Math.max(20, 50 - (juego.getNivel() * 3));
                Thread.sleep(espera);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /** Mueve todos los enemigos de formación (Normal + Tanque) como bloque unificado. */
    private void moverFormacion() {
        int velocidad = 1 + juego.getNivel();
        int dx = velocidad * juego.getFormacionDireccion();

        // Detectar si algún enemigo de formación tocaría el borde
        boolean rebotar = false;
        for (Enemigo e : juego.getEnemigos()) {
            if (esFormacion(e)) {
                int nx = e.getX() + dx;
                int ancho = e.getBounds().width;
                if (nx < 0 || nx + ancho > 600) { rebotar = true; break; }
            }
        }

        if (rebotar) {
            // Invertir dirección y bajar toda la formación
            juego.setFormacionDireccion(-juego.getFormacionDireccion());
            for (Enemigo e : juego.getEnemigos()) {
                if (esFormacion(e)) e.moverFormacion(0, 20);
            }
        } else {
            for (Enemigo e : juego.getEnemigos()) {
                if (esFormacion(e)) e.moverFormacion(dx, 0);
            }
        }
    }

    /** Los enemigos Rápidos y el Boss se mueven con su propia lógica. */
    private void moverIndependientes() {
        for (Enemigo e : juego.getEnemigos()) {
            if (!esFormacion(e)) e.mover();
        }
    }

    private boolean esFormacion(Enemigo e) {
        Enemigo.TipoEnemigo t = e.getTipo();
        return t == Enemigo.TipoEnemigo.NORMAL || t == Enemigo.TipoEnemigo.TANQUE;
    }

    private void esperarSiPausado() {
        while ((juego.isPausado() || juego.isNivelCompletado()) && ejecutando) {
            try { Thread.sleep(50); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public void detener() { ejecutando = false; }
}
