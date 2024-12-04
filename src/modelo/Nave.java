package modelo;

import java.awt.*;

public class Nave {
    private int x, y;
    private static final int VELOCIDAD = 10;
    private boolean escudoActivo;
    private long tiempoInicioEscudo;
    private static final int DURACION_ESCUDO = 10;

    public Nave(int x, int y) {
        this.x = x;
        this.y = y;
        this.escudoActivo = false;
    }

    public void activarEscudo() {
        escudoActivo = true;
        tiempoInicioEscudo = System.currentTimeMillis();
    }

    public void actualizarEscudo() {
        if (escudoActivo) {
            long tiempoActual = System.currentTimeMillis();
            if (tiempoActual - tiempoInicioEscudo > DURACION_ESCUDO) {
                escudoActivo = false;
            }
        }
    }

    public boolean tieneEscudo() {
        return escudoActivo;
    }

    public double getPorcentajeEscudoRestante() {
        if (!escudoActivo) return 0;

        long tiempoActual = System.currentTimeMillis();
        long tiempoTranscurrido = tiempoActual - tiempoInicioEscudo;
        double porcentaje = 1.0 - ((double)tiempoTranscurrido / DURACION_ESCUDO);
        return Math.max(0, Math.min(1, porcentaje)); // Asegurar que esté entre 0 y 1
    }

    public void moverIzquierda() {
        if (x > 0) x -= VELOCIDAD;
    }

    public void moverDerecha() {
        if (x < 550) x += VELOCIDAD;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 30, 30);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}

