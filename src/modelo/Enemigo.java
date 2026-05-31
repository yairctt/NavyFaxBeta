package modelo;

import java.awt.*;

public class Enemigo {
    protected int x, y;
    protected int velocidad;
    protected boolean movimientoDerecha;
    protected int salud;
    protected int saludMaxima;

    public enum TipoEnemigo { NORMAL, RAPIDO, TANQUE, BOSS }

    public Enemigo(int x, int y, int velocidad) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.movimientoDerecha = true;
        this.salud = 1;
        this.saludMaxima = 1;
    }

    public void mover() {
        if (movimientoDerecha) {
            x += velocidad;
            if (x > 550) {
                movimientoDerecha = false;
                y += 20;
            }
        } else {
            x -= velocidad;
            if (x < 0) {
                movimientoDerecha = true;
                y += 20;
            }
        }
        if (y > 500) y = 50;
    }

    /** Mueve al enemigo como parte de una formación (usado por el hilo de enemigos). */
    public void moverFormacion(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /** Aplica un punto de daño. Retorna true si el enemigo muere. */
    public boolean recibirDaño() {
        salud--;
        return salud <= 0;
    }

    public int getSalud() { return salud; }
    public int getSaludMaxima() { return saludMaxima; }

    public TipoEnemigo getTipo() { return TipoEnemigo.NORMAL; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 30, 30);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
