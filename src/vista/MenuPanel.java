package vista;

import modelo.Dificultad;
import modelo.GestorPuntuaciones;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class MenuPanel extends JPanel {

    public interface MenuListener {
        void onJugar(Dificultad dificultad);
        void onSalir();
    }

    private final MenuListener listener;
    private final int[][] estrellas;
    private final Timer timerEstrellas;
    private Dificultad dificultadSeleccionada = Dificultad.NORMAL;

    // Botones de dificultad (para hover/selección)
    private final Rectangle[] rectsDificultad = new Rectangle[3];
    private final Dificultad[] dificultades = Dificultad.values();

    public MenuPanel(MenuListener listener) {
        this.listener = listener;
        setPreferredSize(new Dimension(600, 600));
        setBackground(new Color(8, 8, 25));
        setLayout(null);

        estrellas = new int[120][3];
        Random rnd = new Random();
        for (int i = 0; i < estrellas.length; i++) {
            estrellas[i][0] = rnd.nextInt(600);
            estrellas[i][1] = rnd.nextInt(600);
            estrellas[i][2] = 1 + rnd.nextInt(2);
        }

        timerEstrellas = new Timer(40, e -> {
            for (int[] s : estrellas) s[1] = (s[1] + s[2]) % 600;
            repaint();
        });
        timerEstrellas.start();

        crearBotones();
        crearBotonesDificultad();
    }

    // ── Botones principales ───────────────────────────────────────────────────

    private void crearBotones() {
        JButton btnJugar = crearBoton("JUGAR", new Color(0, 100, 220));
        btnJugar.setBounds(210, 360, 180, 50);
        btnJugar.addActionListener(e -> listener.onJugar(dificultadSeleccionada));
        add(btnJugar);

        JButton btnSalir = crearBoton("SALIR", new Color(130, 25, 25));
        btnSalir.setBounds(210, 430, 180, 50);
        btnSalir.addActionListener(e -> listener.onSalir());
        add(btnSalir);
    }

    private JButton crearBoton(String texto, Color colorBase) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? colorBase.brighter() : colorBase);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Selector de dificultad (custom painted, reacciona a clics) ───────────

    private void crearBotonesDificultad() {
        // Tres botones invisibles por encima; el pintado es en paintComponent
        int startX = 118, y = 295, w = 115, h = 38, gap = 10;
        for (int i = 0; i < 3; i++) {
            rectsDificultad[i] = new Rectangle(startX + i * (w + gap), y, w, h);
        }

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                for (int i = 0; i < 3; i++) {
                    if (rectsDificultad[i].contains(e.getPoint())) {
                        dificultadSeleccionada = dificultades[i];
                        repaint();
                        break;
                    }
                }
            }
        });

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) { repaint(); }
        });
    }

    // ── Renderizado ───────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(8, 8, 25));
        g2.fillRect(0, 0, 600, 600);

        for (int[] s : estrellas) {
            int b = 120 + s[2] * 60;
            g2.setColor(new Color(b, b, b));
            g2.fillOval(s[0], s[1], s[2] + 1, s[2] + 1);
        }

        // Título
        g2.setFont(new Font("Arial", Font.BOLD, 72));
        FontMetrics fm = g2.getFontMetrics();
        String titulo = "NAVYFAX";
        int tx = (600 - fm.stringWidth(titulo)) / 2;
        g2.setColor(new Color(0, 60, 160));
        g2.drawString(titulo, tx + 3, 163);
        g2.setColor(new Color(0, 180, 255));
        g2.drawString(titulo, tx, 160);

        // Subtítulo
        g2.setFont(new Font("Arial", Font.ITALIC, 16));
        fm = g2.getFontMetrics();
        g2.setColor(new Color(160, 200, 255));
        String sub = "Arcade Space Shooter";
        g2.drawString(sub, (600 - fm.stringWidth(sub)) / 2, 192);

        // Línea decorativa
        g2.setColor(new Color(0, 80, 180));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(120, 210, 480, 210);
        g2.setStroke(new BasicStroke(1f));

        // Controles
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        fm = g2.getFontMetrics();
        String ctrl = "← → ESPACIO  |  ESC para pausar";
        g2.setColor(new Color(120, 150, 200));
        g2.drawString(ctrl, (600 - fm.stringWidth(ctrl)) / 2, 238);

        // Etiqueta de dificultad
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        fm = g2.getFontMetrics();
        String labelDif = "DIFICULTAD";
        g2.setColor(new Color(180, 180, 220));
        g2.drawString(labelDif, (600 - fm.stringWidth(labelDif)) / 2, 280);

        // Botones de dificultad
        dibujarBotonesDificultad(g2);

        // Botón JUGAR se pinta con Swing sobre esto

        // High Scores
        dibujarScores(g2);
    }

    private void dibujarBotonesDificultad(Graphics2D g2) {
        Color[] coloresDif = {
            new Color(0,  160,  60),   // FÁCIL  - verde
            new Color(200, 160,  0),   // NORMAL - amarillo
            new Color(200,  30, 30)    // DIFÍCIL - rojo
        };
        Point mouse = getMousePosition();

        for (int i = 0; i < 3; i++) {
            Rectangle r = rectsDificultad[i];
            boolean seleccionado = dificultades[i] == dificultadSeleccionada;
            boolean hover = mouse != null && r.contains(mouse);

            Color base = coloresDif[i];
            if (seleccionado) {
                // Fondo lleno + borde brillante
                g2.setColor(base);
                g2.fillRoundRect(r.x, r.y, r.width, r.height, 8, 8);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(r.x, r.y, r.width, r.height, 8, 8);
                g2.setStroke(new BasicStroke(1f));
            } else {
                // Fondo semitransparente
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), hover ? 80 : 40));
                g2.fillRoundRect(r.x, r.y, r.width, r.height, 8, 8);
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), hover ? 200 : 130));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(r.x, r.y, r.width, r.height, 8, 8);
                g2.setStroke(new BasicStroke(1f));
            }

            // Texto
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            String nombre = dificultades[i].nombre;
            g2.setColor(seleccionado ? Color.WHITE : new Color(200, 210, 230));
            g2.drawString(nombre, r.x + (r.width - fm.stringWidth(nombre)) / 2,
                    r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
        }
    }

    private void dibujarScores(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        String titulo = "— TOP PUNTUACIONES —";
        g2.setColor(new Color(200, 180, 50));
        g2.drawString(titulo, (600 - fm.stringWidth(titulo)) / 2, 510);

        List<Integer> scores = GestorPuntuaciones.cargarPuntuaciones();
        if (scores.isEmpty()) {
            g2.setFont(new Font("Arial", Font.ITALIC, 13));
            g2.setColor(new Color(120, 120, 150));
            String vacio = "Todavía no hay puntuaciones";
            fm = g2.getFontMetrics();
            g2.drawString(vacio, (600 - fm.stringWidth(vacio)) / 2, 534);
        } else {
            Color[] colores = {
                new Color(255, 215, 0), new Color(192, 192, 192),
                new Color(205, 127, 50), new Color(180, 200, 220), new Color(160, 180, 200)
            };
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            for (int i = 0; i < Math.min(5, scores.size()); i++) {
                fm = g2.getFontMetrics();
                String linea = (i + 1) + ".  " + scores.get(i) + " pts";
                g2.setColor(i < colores.length ? colores[i] : new Color(160, 160, 180));
                g2.drawString(linea, (600 - fm.stringWidth(linea)) / 2, 532 + i * 20);
            }
        }
    }

    public void detener() { timerEstrellas.stop(); }
}
