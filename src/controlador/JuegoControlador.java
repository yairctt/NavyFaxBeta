package controlador;

import modelo.Dificultad;
import modelo.Juego;
import vista.JuegoPanel;
import vista.MenuPanel;
import javax.swing.*;
import java.awt.event.*;

public class JuegoControlador
        implements KeyListener, WindowListener, MenuPanel.MenuListener, JuegoPanel.GameOverListener {

    private enum Estado { MENU, JUGANDO, PAUSADO }

    private Juego modelo;
    private JuegoPanel vistaJuego;
    private MenuPanel vistaMenu;
    private Timer timerRepintado;
    private final JFrame frame;
    private Estado estado = Estado.MENU;
    private Dificultad dificultadActual = Dificultad.NORMAL;

    public JuegoControlador() {
        frame = new JFrame("NavyFax");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.addKeyListener(this);
        frame.addWindowListener(this);
        mostrarMenu();
    }

    // ── Navegación ────────────────────────────────────────────────────────────

    private void mostrarMenu() {
        estado = Estado.MENU;
        if (timerRepintado != null) { timerRepintado.stop(); timerRepintado = null; }
        if (modelo != null)         { modelo.detenerHilos(); modelo = null; }
        if (vistaJuego != null)     { vistaJuego.detener(); vistaJuego = null; }
        if (vistaMenu != null)      { vistaMenu.detener(); }

        vistaMenu = new MenuPanel(this);
        frame.setContentPane(vistaMenu);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }

    private void iniciarJuego(Dificultad dificultad) {
        this.dificultadActual = dificultad;
        estado = Estado.JUGANDO;
        if (vistaMenu != null) { vistaMenu.detener(); vistaMenu = null; }

        modelo     = new Juego(dificultad);
        vistaJuego = new JuegoPanel(modelo);
        vistaJuego.setGameOverListener(this);

        frame.setContentPane(vistaJuego);
        frame.pack();
        frame.requestFocusInWindow();

        timerRepintado = new Timer(16, e -> vistaJuego.repaint());
        timerRepintado.start();

        modelo.iniciar();
    }

    // ── MenuPanel.MenuListener ────────────────────────────────────────────────

    @Override
    public void onJugar(Dificultad dificultad) { iniciarJuego(dificultad); }

    @Override
    public void onSalir() { System.exit(0); }

    // ── JuegoPanel.GameOverListener ───────────────────────────────────────────

    @Override
    public void onReintentar() {
        if (modelo == null) return;
        estado = Estado.JUGANDO;
        vistaJuego.limpiarEstadoVisual();
        modelo.reiniciar();
    }

    @Override
    public void onMenu() { mostrarMenu(); }

    // ── Teclado ───────────────────────────────────────────────────────────────

    @Override
    public void keyPressed(KeyEvent e) {
        if (estado == Estado.MENU) return;
        int key = e.getKeyCode();

        // ESC pausa/reanuda (no durante nivel completado)
        if (key == KeyEvent.VK_ESCAPE && modelo != null
                && modelo.isJuegoActivo() && !modelo.isNivelCompletado()) {
            if (modelo.isPausado()) {
                modelo.reanudar();
                estado = Estado.JUGANDO;
            } else {
                modelo.pausar();
                estado = Estado.PAUSADO;
            }
            return;
        }

        if (estado == Estado.JUGANDO && modelo != null
                && modelo.isJugadorVivo() && modelo.isJuegoActivo()
                && !modelo.isNivelCompletado()) {
            switch (key) {
                case KeyEvent.VK_LEFT:  modelo.getNave().moverIzquierda(); break;
                case KeyEvent.VK_RIGHT: modelo.getNave().moverDerecha();   break;
                case KeyEvent.VK_SPACE: modelo.disparar();                 break;
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    // ── WindowListener ────────────────────────────────────────────────────────

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (estado == Estado.JUGANDO && modelo != null
                && modelo.isJuegoActivo() && !modelo.isNivelCompletado()) {
            modelo.pausar();
            estado = Estado.PAUSADO;
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (modelo != null)         modelo.detenerHilos();
        if (vistaJuego != null)     vistaJuego.detener();
        if (vistaMenu != null)      vistaMenu.detener();
        if (timerRepintado != null) timerRepintado.stop();
    }

    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}

    // ── Entry point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JuegoControlador::new);
    }
}
