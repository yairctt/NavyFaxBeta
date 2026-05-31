package modelo;

import java.awt.*;

public class Boss extends Enemigo {
    public Boss(int nivel) {
        super(270, 50, 1 + nivel / 3);
        this.salud = 5 + nivel * 3;
        this.saludMaxima = this.salud;
    }

    @Override
    public void mover() {
        x += movimientoDerecha ? velocidad : -velocidad;
        if (x > 500) movimientoDerecha = false;
        if (x < 30)  movimientoDerecha = true;
    }

    @Override
    public TipoEnemigo getTipo() { return TipoEnemigo.BOSS; }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 60, 60);
    }
}
