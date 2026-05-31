package modelo;

import java.awt.*;

public class PowerUp {
    public enum Tipo { DISPARO_DOBLE, ESCUDO_EXTRA, VELOCIDAD_DISPARO }

    private int x, y;
    private final Tipo tipo;
    private static final int VELOCIDAD = 3;
    static final int TAMAÑO = 22;

    public PowerUp(int x, int y, Tipo tipo) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
    }

    public void mover() {
        y += VELOCIDAD;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, TAMAÑO, TAMAÑO);
    }

    public Tipo getTipo() { return tipo; }
    public int getX() { return x; }
    public int getY() { return y; }
}
