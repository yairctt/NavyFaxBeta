package modelo;

import java.awt.*;

public class EnemigoTanque extends Enemigo {
    public EnemigoTanque(int x, int y, int nivel) {
        super(x, y, Math.max(1, nivel / 2));
        this.salud = 3;
        this.saludMaxima = 3;
    }

    @Override
    public TipoEnemigo getTipo() { return TipoEnemigo.TANQUE; }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 45, 45);
    }
}
