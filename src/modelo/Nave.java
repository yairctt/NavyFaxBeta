package modelo;

import java.awt.*;

public class Nave {
    private int x, y;
    private static final int VELOCIDAD = 10;

    public Nave(int x, int y) {
        this.x = x;
        this.y = y;
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

