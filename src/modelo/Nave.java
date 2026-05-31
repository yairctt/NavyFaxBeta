package modelo;

import java.awt.*;

public class Nave {
    private int x, y;
    private static final int VELOCIDAD = 10;
    private boolean escudoActivo;
    private long tiempoInicioEscudo;
    private static final int DURACION_ESCUDO = 3000; // 3 segundos

    public enum TipoDisparo { SIMPLE, DOBLE, TRIPLE }
    private TipoDisparo tipoDisparo = TipoDisparo.SIMPLE;
    private long tiempoInicioDisparoEspecial;
    private static final int DURACION_POWER_UP_DISPARO = 10000; // 10 segundos

    public Nave(int x, int y) {
        this.x = x;
        this.y = y;
        this.escudoActivo = false;
        this.tipoDisparo = TipoDisparo.SIMPLE;
    }

    public void activarEscudo() {
        escudoActivo = true;
        tiempoInicioEscudo = System.currentTimeMillis();
    }

    public void actualizarEscudo() {
        if (escudoActivo && System.currentTimeMillis() - tiempoInicioEscudo > DURACION_ESCUDO) {
            escudoActivo = false;
        }
        if (tipoDisparo != TipoDisparo.SIMPLE
                && System.currentTimeMillis() - tiempoInicioDisparoEspecial > DURACION_POWER_UP_DISPARO) {
            tipoDisparo = TipoDisparo.SIMPLE;
        }
    }

    public void activarDisparoDoble() {
        tipoDisparo = TipoDisparo.DOBLE;
        tiempoInicioDisparoEspecial = System.currentTimeMillis();
    }

    public void activarDisparoTriple() {
        tipoDisparo = TipoDisparo.TRIPLE;
        tiempoInicioDisparoEspecial = System.currentTimeMillis();
    }

    public TipoDisparo getTipoDisparo() { return tipoDisparo; }

    public boolean tieneEscudo() { return escudoActivo; }

    public double getPorcentajeEscudoRestante() {
        if (!escudoActivo) return 0;
        long elapsed = System.currentTimeMillis() - tiempoInicioEscudo;
        return Math.max(0, 1.0 - ((double) elapsed / DURACION_ESCUDO));
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
