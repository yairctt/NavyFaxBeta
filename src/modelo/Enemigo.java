package modelo;

import java.awt.*;

public class Enemigo {
    private int x, y;
    private int velocidad;
    private boolean movimientoDerecha;

    public Enemigo(int x, int y, int velocidad) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.movimientoDerecha = true;
    }

    public void mover() {
        if (movimientoDerecha) {
            x += velocidad;
            if (x > 550) {
                movimientoDerecha = false;
                y += 20; // Baja una línea
            }
        } else {
            x -= velocidad;
            if (x < 0) {
                movimientoDerecha = true;
                y += 20; // Baja una línea
            }
        }

        // Para evitar que los enemigos bajen infinitamente
        if (y > 500) {
            y = 50; // Volver arriba si llegan muy abajo
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 30, 30);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}


