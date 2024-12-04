package vista;

import modelo.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class JuegoPanel extends JPanel {
    private Juego modelo;
    private Image fondoImage;
    private Image naveImage;
    private Image enemigoImage;
    private Image obstaculoImage;
    private Image escudoImage;
    private Image[] framesExplosion;
    private Map<Point, Integer> animacionesExplosion;
    private static final int TOTAL_FRAMES = 8;
    private Timer timerAnimacion;

    public JuegoPanel(Juego modelo) {
        this.modelo = modelo;
        setPreferredSize(new Dimension(600, 600));
        this.animacionesExplosion = new HashMap<>();
        cargarImagenes();
        iniciarTimerAnimacion();
    }

    private void cargarImagenes() {
        try {
            // Cargar las imágenes desde los recursos
            fondoImage = ImageIO.read(getClass().getResourceAsStream("/recursos/imagenes/fondo.jpg"));
            naveImage = ImageIO.read(getClass().getResourceAsStream("/recursos/imagenes/nave.png"));
            enemigoImage = ImageIO.read(getClass().getResourceAsStream("/recursos/imagenes/enemigo.png"));
            obstaculoImage = ImageIO.read(getClass().getResourceAsStream("/recursos/imagenes/obstaculo.png"));
            escudoImage = ImageIO.read(getClass().getResourceAsStream("/recursos/imagenes/escudo.png"));
            Image explosionImage = ImageIO.read(getClass().getResourceAsStream("/recursos/imagenes/explosion.png"));

            // Redimensionar imágenes
            fondoImage = fondoImage.getScaledInstance(600, 600, Image.SCALE_SMOOTH);
            naveImage = naveImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            enemigoImage = enemigoImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            obstaculoImage = obstaculoImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            escudoImage = escudoImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);

            // Inicializar el array con la misma imagen
            framesExplosion = new Image[TOTAL_FRAMES];
            for(int i = 0; i < TOTAL_FRAMES; i++) {
                framesExplosion[i] = explosionImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            }

        } catch (IOException e) {
            System.out.println("Error al cargar las imágenes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void iniciarTimerAnimacion() {
        timerAnimacion = new Timer(50, e -> {
            boolean hayAnimaciones = false;

            // Actualizar frames de explosiones existentes
            Iterator<Map.Entry<Point, Integer>> it = animacionesExplosion.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Point, Integer> entry = it.next();
                int frameActual = entry.getValue();
                if (frameActual >= TOTAL_FRAMES - 1) {
                    it.remove();
                    modelo.removerExplosion(entry.getKey());
                } else {
                    animacionesExplosion.put(entry.getKey(), frameActual + 1);
                    hayAnimaciones = true;
                }
            }

            // Agregar nuevas explosiones
            for (Point explosion : modelo.getExplosiones()) {
                if (!animacionesExplosion.containsKey(explosion)) {
                    animacionesExplosion.put(explosion, 0);
                    hayAnimaciones = true;
                }
            }

            if (hayAnimaciones) {
                repaint();
            }
        });
        timerAnimacion.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibuja el fondo
        if (fondoImage != null) {
            g.drawImage(fondoImage, 0, 0, this);
        }

        // Dibuja la nave
        Nave nave = modelo.getNave();
        if (naveImage != null) {
            g.drawImage(naveImage, nave.getX(), nave.getY(), this);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(nave.getX(), nave.getY(), 30, 30);
        }
        if (nave.tieneEscudo()) {
            // Dibujar efecto de escudo
            g.setColor(new Color(0, 255, 255, 128));
            g.fillOval(nave.getX() - 5, nave.getY() - 5, 40, 40);

            // Dibujar barra de duración del escudo
            int barraAncho = 50;
            int barraAlto = 5;
            int barraX = nave.getX() - 10;
            int barraY = nave.getY() - 10;

            // Fondo de la barra
            g.setColor(new Color(60, 60, 60));
            g.fillRect(barraX, barraY, barraAncho, barraAlto);

            // Parte llena de la barra
            g.setColor(new Color(0, 191, 255));
            int anchoLleno = (int)(barraAncho * nave.getPorcentajeEscudoRestante());
            g.fillRect(barraX, barraY, anchoLleno, barraAlto);

            // Borde de la barra
            g.setColor(Color.WHITE);
            g.drawRect(barraX, barraY, barraAncho, barraAlto);
        }

        // Dibuja los enemigos
        for (Enemigo enemigo : modelo.getEnemigos()) {
            if (enemigoImage != null) {
                g.drawImage(enemigoImage, enemigo.getX(), enemigo.getY(), this);
            } else {
                g.setColor(Color.RED);
                g.fillRect(enemigo.getX(), enemigo.getY(), 30, 30);
            }
        }

        // Dibuja los disparos
        g.setColor(Color.YELLOW);
        for (Disparo disparo : modelo.getDisparos()) {
            g.fillRect(disparo.getX(), disparo.getY(), 5, 10);
        }

        //Dibuja los obstaculos
        for (Obstaculo obstaculo : modelo.getObstaculos()) {
            if (obstaculoImage != null) {
                g.drawImage(obstaculoImage, obstaculo.getX(), obstaculo.getY(), this);
            } else {
                g.setColor(Color.RED);
                g.fillRect(obstaculo.getX(), obstaculo.getY(), 40, 40);
            }
        }

        // Dibujar explosiones
        for (Map.Entry<Point, Integer> entry : animacionesExplosion.entrySet()) {
            Point pos = entry.getKey();
            int frame = entry.getValue();
            if (frame < TOTAL_FRAMES && framesExplosion[frame] != null) {
                g.drawImage(framesExplosion[frame], pos.x, pos.y, this);
            }
        }

        // Dibuja la información del juego
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Nivel: " + modelo.getNivel(), 10, 25);
        g.drawString("Puntuación: " + modelo.getPuntuacion(), 10, 50);

        Image iconoVida = modelo.getIconoVida();
        if (iconoVida != null) {
            for (int i = 0; i < modelo.getVidas(); i++) {
                g.drawImage(iconoVida, 10 + (i * 25), 60, this);
            }
        } else {
            g.drawString("Vidas: " + modelo.getVidas(), 10, 75);
        }

        // Dibujar disparos enemigos
        g.setColor(Color.RED);
        for (DisparoEnemigo disparo : modelo.getDisparosEnemigos()) {
            g.fillRect(disparo.getX(), disparo.getY(), 5, 10);
        }

        // Mensaje de fin de juego
        if (!modelo.isJugadorVivo() || !modelo.isJuegoActivo()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String mensaje = !modelo.isJugadorVivo() ? "¡GAME OVER!" : "¡JUEGO TERMINADO!";

            FontMetrics fm = g.getFontMetrics();
            int mensajeAncho = fm.stringWidth(mensaje);
            int x = (getWidth() - mensajeAncho) / 2;
            int y = getHeight() / 2;

            g.drawString(mensaje, x, y);

            String puntuacionFinal = "Puntuación Final: " + modelo.getPuntuacion();
            int puntajeAncho = fm.stringWidth(puntuacionFinal);
            g.drawString(puntuacionFinal, (getWidth() - puntajeAncho) / 2, y + 50);
        }
    }

    public void detener() {
        if (timerAnimacion != null) {
            timerAnimacion.stop();
        }
    }
}