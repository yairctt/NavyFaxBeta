package modelo;

import java.awt.Rectangle;

public class DisparoEnemigo {
    private int x, y;
    private static final int VELOCIDAD = 5;

    public DisparoEnemigo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void mover() {
        y += VELOCIDAD; // Se mueve hacia abajo
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 5, 10);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
