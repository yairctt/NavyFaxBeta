package vista;

import modelo.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;

public class JuegoPanel extends JPanel {
    private Juego modelo;
    private Image fondoImage;
    private Image naveImage;
    private Image enemigoImage;
    private Image obstaculoImage;

    public JuegoPanel(Juego modelo) {
        this.modelo = modelo;
        setPreferredSize(new Dimension(600, 600));
        cargarImagenes();
    }

    private void cargarImagenes() {
        try {
            // Cargar las imágenes desde los recursos
            fondoImage = ImageIO.read(getClass().getResourceAsStream("/recursos/imagenes/fondo.jpg"));
            naveImage = ImageIO.read(getClass().getResourceAsStream("/recursos/imagenes/nave.png"));
            enemigoImage = ImageIO.read(getClass().getResourceAsStream("/recursos/imagenes/enemigo.png"));
            obstaculoImage = ImageIO.read(getClass().getResourceAsStream("/recursos/imagenes/obstaculo.png"));

            // Redimensionar imágenes
            fondoImage = fondoImage.getScaledInstance(600, 600, Image.SCALE_SMOOTH);
            naveImage = naveImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            enemigoImage = enemigoImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            obstaculoImage = obstaculoImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);

        } catch (IOException e) {
            System.out.println("Error al cargar las imágenes: " + e.getMessage());
            e.printStackTrace();
        }
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

        // Dibuja la información del juego
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Nivel: " + modelo.getNivel(), 10, 25);
        g.drawString("Puntuación: " + modelo.getPuntuacion(), 10, 50);

        // Dibujar disparos enemigos
        g.setColor(Color.RED);
        for (DisparoEnemigo disparo : modelo.getDisparosEnemigos()) {
            g.fillRect(disparo.getX(), disparo.getY(), 5, 10);
        }

        // Mensaje de fin de juego - unificado para ambos casos
        if (!modelo.isJugadorVivo() || !modelo.isJuegoActivo()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String mensaje = !modelo.isJugadorVivo() ? "¡GAME OVER!" : "¡JUEGO TERMINADO!";

            // Calcular posición para centrar el texto
            FontMetrics fm = g.getFontMetrics();
            int mensajeAncho = fm.stringWidth(mensaje);
            int x = (getWidth() - mensajeAncho) / 2;
            int y = getHeight() / 2;

            g.drawString(mensaje, x, y);
        }
    }
}
