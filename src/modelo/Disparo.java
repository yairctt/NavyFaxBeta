package modelo;

import java.awt.*;

public class Disparo {
    private int x, y;
    private static final int VELOCIDAD = 7;

    public Disparo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void mover() {
        y -= VELOCIDAD;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 5, 10);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
