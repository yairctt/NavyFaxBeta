package modelo;

public enum Dificultad {
    FACIL  ("Fácil",   0.65, 0.5, 12),
    NORMAL ("Normal",  1.0,  1.0, 10),
    DIFICIL("Difícil", 1.45, 1.6,  7);

    public final String nombre;
    /** Multiplicador sobre la velocidad base de enemigos (< 1 = más lento). */
    public final double multVelocidad;
    /** Multiplicador sobre la probabilidad de disparo enemigo. */
    public final double multDisparo;
    /** Vidas con las que arranca el jugador. */
    public final int vidasIniciales;

    Dificultad(String nombre, double multVelocidad, double multDisparo, int vidasIniciales) {
        this.nombre        = nombre;
        this.multVelocidad = multVelocidad;
        this.multDisparo   = multDisparo;
        this.vidasIniciales = vidasIniciales;
    }
}
