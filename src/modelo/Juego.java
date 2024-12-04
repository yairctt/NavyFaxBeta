package modelo;


import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import modelo.threads.*;

import javax.imageio.ImageIO;

public class Juego {
    private Nave nave;
    private CopyOnWriteArrayList<Enemigo> enemigos;
    private CopyOnWriteArrayList<Disparo> disparos;
    private CopyOnWriteArrayList<Obstaculo> obstaculos;
    private CopyOnWriteArrayList<DisparoEnemigo> disparosEnemigos;
    private int nivel;
    private int puntuacion;
    private boolean juegoActivo;
    private MovimientoEnemigosThread hiloEnemigos;
    private MovimientoDisparosThread hiloDisparos;
    private DetectorColisionesThread hiloColisiones;
    private MovimientoObstaculosThread hiloObstaculos;
    private DisparosEnemigosThread hiloDisparosEnemigos;
    private MovimientoDisparosEnemigosThread hiloMovimientoDisparosEnemigos;
    private boolean jugadorVivo;
    private int vidas;
    private Image iconoVida;
    private int velocidadEnemigos;
    private int frecuenciaDisparo;

    public Juego() {
        vidas = 3;
        nave = new Nave(300, 500);
        enemigos = new CopyOnWriteArrayList<>();
        disparos = new CopyOnWriteArrayList<>();
        obstaculos = new CopyOnWriteArrayList<>();
        disparosEnemigos = new CopyOnWriteArrayList<>();
        nivel = 1;
        puntuacion = 0;
        juegoActivo = true;
        jugadorVivo = true;
        cargarIconoVida();
        inicializarEnemigos();
        iniciarHilos();
        inicializarObstaculos();
    }

    private void cargarIconoVida() {
        try {
            iconoVida = ImageIO.read(getClass().getResourceAsStream("/recursos/imagenes/vida.png"))
                    .getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.out.println("Error al cargar icono de vida: " + e.getMessage());
        }
    }

    public void perderVida() {
        vidas--;
        if (vidas <= 0) {
            eliminarJugador();
        } else {
            // Reiniciar posición de la nave y limpiar disparos enemigos
            nave = new Nave(300, 500);
            disparosEnemigos.clear();
        }
    }

    public int getVidas() {
        return vidas;
    }

    public Image getIconoVida() {
        return iconoVida;
    }

    public void verificarNivel() {
        if (enemigos.isEmpty() && jugadorVivo) {
            nivel++;
            inicializarEnemigos();
            inicializarObstaculos();
        }
    }



    private void inicializarObstaculos() {
        obstaculos.clear();

        int numeroObstaculos = 5 + (nivel + 1);
        for (int i = 0; i < numeroObstaculos ; i++) {
            obstaculos.add(new Obstaculo(100 + i * 120, 300 + (i % 2) * 50));// Ejemplo de obstáculos distribuidos
            System.out.println("Oobstaculos iniciados");
        }
    }

    private void inicializarEnemigos() {
        enemigos.clear();
        System.out.println("Enemigos iniciados");
        int numeroEnemigos = 5 + (nivel * 2);
        int velocidad = 1 + nivel;

        for (int i = 0; i < numeroEnemigos; i++) {
            enemigos.add(new Enemigo(50 + (i * 80), 50, velocidad));
        }
    }

    private void iniciarHilos() {
        hiloEnemigos = new MovimientoEnemigosThread(this);
        hiloDisparos = new MovimientoDisparosThread(this);
        hiloColisiones = new DetectorColisionesThread(this);
        hiloObstaculos = new MovimientoObstaculosThread(this);
        hiloDisparosEnemigos = new DisparosEnemigosThread(this);
        hiloMovimientoDisparosEnemigos = new MovimientoDisparosEnemigosThread(this);


        hiloDisparosEnemigos.start();
        hiloEnemigos.start();
        hiloDisparos.start();
        hiloColisiones.start();
        hiloObstaculos.start();
        hiloMovimientoDisparosEnemigos.start();
    }

    public void detenerHilos() {
        hiloEnemigos.detener();
        hiloDisparos.detener();
        hiloColisiones.detener();
        hiloObstaculos.detener();
        hiloDisparosEnemigos.detener();
        hiloMovimientoDisparosEnemigos.detener();
    }

    public void disparoEnemigo(int x, int y) {
        disparosEnemigos.add(new DisparoEnemigo(x, y));
    }

    public CopyOnWriteArrayList<DisparoEnemigo> getDisparosEnemigos() {
        return disparosEnemigos;
    }

    public boolean isJugadorVivo() {
        return jugadorVivo;
    }

    public void eliminarJugador() {
        jugadorVivo = false;
        juegoActivo = false;
    }


    // Getters y setters
    public Nave getNave() { return nave; }
    public CopyOnWriteArrayList<Obstaculo> getObstaculos() { return obstaculos; }
    public CopyOnWriteArrayList<Enemigo> getEnemigos() { return enemigos; }
    public CopyOnWriteArrayList<Disparo> getDisparos() { return disparos; }
    public int getNivel() { return nivel; }
    public int getPuntuacion() { return puntuacion; }
    public boolean isJuegoActivo() { return juegoActivo; }
    public void incrementarPuntuacion() { puntuacion += 100; }

    public void disparar() {
        disparos.add(new Disparo(nave.getX() + 15, nave.getY()));
    }
}

