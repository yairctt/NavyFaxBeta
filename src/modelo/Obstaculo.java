package modelo;

import java.awt.*;

public class Obstaculo {
    private int x, y;
    private boolean movimientoDerecha;
    private int velocidad = 6;
    private static final int ANCHO = 40;
    private static final int ALTO = 40;

    public Obstaculo(int x, int y) {
        this.x = x;
        this.y = y;
        this.movimientoDerecha = false;
    }

    public void mover() {
        if (movimientoDerecha) {
            x += velocidad;
            if (x > 550) {
                movimientoDerecha = false;
                y += 0; // Baja una línea
            }
        } else {
            x -= velocidad;
            if (x < 0) {
                movimientoDerecha = true;
                y += 0; // Baja una línea
            }
        }
    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, ANCHO, ALTO);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}

