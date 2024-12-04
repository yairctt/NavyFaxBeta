package controlador;

import modelo.Juego;
import vista.JuegoPanel;
import javax.swing.*;
import java.awt.event.*;

public class JuegoControlador implements KeyListener, WindowListener {
    private Juego modelo;
    private JuegoPanel vista;
    private Timer timerRepintado;
    private JFrame frame;

    public JuegoControlador(Juego modelo, JuegoPanel vista) {
        this.modelo = modelo;
        this.vista = vista;

        // Timer solo para repintar la pantalla
        timerRepintado = new Timer(16, e -> vista.repaint());
        timerRepintado.start();

        frame = new JFrame("NavyFax");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(vista);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.addKeyListener(this);
        frame.addWindowListener(this);
        frame.setVisible(true);
        frame.setResizable(false); // Opcional: evitar que se pueda redimensionar
    }

    @Override
    public void windowClosing(WindowEvent e) {
        modelo.detenerHilos();
        vista.detener(); // Detener el timer de animación
        timerRepintado.stop();
    }

    // Implementar otros métodos de WindowListener...
    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (modelo.isJugadorVivo() && modelo.isJuegoActivo()) {  // Solo procesar teclas si el juego está activo
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    modelo.getNave().moverIzquierda();
                    break;
                case KeyEvent.VK_RIGHT:
                    modelo.getNave().moverDerecha();
                    break;
                case KeyEvent.VK_SPACE:
                    modelo.disparar();
                    break;
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Juego modelo = new Juego();
            JuegoPanel vista = new JuegoPanel(modelo);
            new JuegoControlador(modelo, vista);
        });
    }
}
