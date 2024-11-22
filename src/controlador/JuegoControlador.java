package controlador;

import modelo.Juego;
import vista.JuegoPanel;
import javax.swing.*;
import java.awt.event.*;

public class JuegoControlador implements KeyListener, WindowListener {
    private Juego modelo;
    private JuegoPanel vista;
    private Timer timerRepintado;

    public JuegoControlador(Juego modelo, JuegoPanel vista) {
        this.modelo = modelo;
        this.vista = vista;

        // Timer solo para repintar la pantalla
        timerRepintado = new Timer(16, e -> vista.repaint());
        timerRepintado.start();

        JFrame frame = new JFrame("NavyFax");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(vista);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.addKeyListener(this);
        frame.addWindowListener(this);
        frame.setVisible(true);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        modelo.detenerHilos();
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

