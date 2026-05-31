package modelo;

import java.awt.*;

public class EnemigoRapido extends Enemigo {
    private int fase;

    public EnemigoRapido(int x, int y, int nivel) {
        super(x, y, 2 + nivel * 2);
        this.salud = 1;
        this.saludMaxima = 1;
        this.fase = 0;
    }

    @Override
    public void mover() {
        fase++;
        x += movimientoDerecha ? velocidad : -velocidad;
        y += (int)(4 * Math.sin(fase * 0.25));

        if (x > 550) movimientoDerecha = false;
        if (x < 0) movimientoDerecha = true;
        if (y > 500) y = 50;
        if (y < 30) y = 30;
    }

    @Override
    public TipoEnemigo getTipo() { return TipoEnemigo.RAPIDO; }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 20, 20);
    }
}
