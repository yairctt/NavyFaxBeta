package vista;

import modelo.*;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class JuegoPanel extends JPanel implements MouseListener {

    public interface GameOverListener {
        void onReintentar();
        void onMenu();
    }

    // ── Modelo ────────────────────────────────────────────────────────────────
    private final Juego modelo;
    private GameOverListener gameOverListener;

    // ── Imágenes ──────────────────────────────────────────────────────────────
    private Image fondoImage;
    private Image naveImage;
    private Image enemigoImage;
    private Image obstaculoImage;
    private static final int TOTAL_FRAMES = 8;

    // ── Parallax: dos capas de estrellas + scroll del fondo ──────────────────
    private final int[][] estrellas1 = new int[70][2]; // capa lenta
    private final int[][] estrellas2 = new int[35][2]; // capa rápida
    private double scrollFondo = 0;

    // ── Animaciones de explosión ──────────────────────────────────────────────
    private final Map<Point, Integer> animacionesExplosion = new HashMap<>();
    private Timer timerAnimacion;

    // ── Números flotantes de puntuación ───────────────────────────────────────
    private static class NumeroFlotante {
        int x, y, alpha;
        final String texto;
        NumeroFlotante(int x, int y, String texto) { this.x = x; this.y = y; this.texto = texto; this.alpha = 230; }
    }
    private final ArrayList<NumeroFlotante> numerosFlotantes = new ArrayList<>();

    // ── Efectos de daño ───────────────────────────────────────────────────────
    private int flashAlpha = 0;           // destello rojo al recibir daño
    private int shakeIntensidad = 0;      // intensidad del screen shake
    private int shakeX = 0, shakeY = 0;
    private long ultimoDañoTrackeado = 0; // para detectar daño nuevo

    // ── Botones Game Over ─────────────────────────────────────────────────────
    private Rectangle botonReintentar;
    private Rectangle botonMenu;

    // ── RNG para efectos ──────────────────────────────────────────────────────
    private final Random rnd = new Random();

    public JuegoPanel(Juego modelo) {
        this.modelo = modelo;
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.BLACK);
        addMouseListener(this);
        inicializarEstrellas();
        cargarImagenes();
        iniciarTimerAnimacion();
    }

    public void setGameOverListener(GameOverListener l) { this.gameOverListener = l; }

    public void limpiarEstadoVisual() {
        animacionesExplosion.clear();
        numerosFlotantes.clear();
        flashAlpha = 0;
        shakeIntensidad = 0;
    }

    // ── Inicialización ────────────────────────────────────────────────────────

    private void inicializarEstrellas() {
        for (int[] s : estrellas1) { s[0] = rnd.nextInt(600); s[1] = rnd.nextInt(600); }
        for (int[] s : estrellas2) { s[0] = rnd.nextInt(600); s[1] = rnd.nextInt(600); }
    }

    private void cargarImagenes() {
        try {
            fondoImage    = leer("/recursos/imagenes/fondo.jpg");
            naveImage     = leer("/recursos/imagenes/nave.png").getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            enemigoImage  = leer("/recursos/imagenes/enemigo.png");
            obstaculoImage= leer("/recursos/imagenes/obstaculo.png").getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        } catch (IOException | IllegalArgumentException ignored) {}
    }

    private Image leer(String path) throws IOException {
        var stream = getClass().getResourceAsStream(path);
        if (stream == null) throw new IOException("Recurso no encontrado: " + path);
        return ImageIO.read(stream);
    }

    // ── Timer de animación (50ms) ─────────────────────────────────────────────

    private void iniciarTimerAnimacion() {
        timerAnimacion = new Timer(50, e -> {
            actualizarParallax();
            actualizarExplosiones();
            procesarEventosKill();
            actualizarNumerosFlotantes();
            actualizarEfectosDaño();
            repaint();
        });
        timerAnimacion.start();
    }

    public void detener() {
        if (timerAnimacion != null) timerAnimacion.stop();
    }

    private void actualizarParallax() {
        scrollFondo = (scrollFondo + 0.4) % 600;
        for (int[] s : estrellas1) s[1] = (s[1] + 1) % 600;
        for (int[] s : estrellas2) s[1] = (s[1] + 2) % 600;
    }

    private void actualizarExplosiones() {
        Iterator<Map.Entry<Point, Integer>> it = animacionesExplosion.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Point, Integer> entry = it.next();
            if (entry.getValue() >= TOTAL_FRAMES - 1) {
                it.remove();
                modelo.removerExplosion(entry.getKey());
            } else {
                animacionesExplosion.put(entry.getKey(), entry.getValue() + 1);
            }
        }
        for (Point p : modelo.getExplosiones()) {
            if (!animacionesExplosion.containsKey(p)) animacionesExplosion.put(p, 0);
        }
    }

    private void procesarEventosKill() {
        for (int[] ev : modelo.getEventosKill()) {
            numerosFlotantes.add(new NumeroFlotante(ev[0] + 10, ev[1] - 5, "+" + ev[2]));
            modelo.getEventosKill().remove(ev);
        }
    }

    private void actualizarNumerosFlotantes() {
        Iterator<NumeroFlotante> it = numerosFlotantes.iterator();
        while (it.hasNext()) {
            NumeroFlotante nf = it.next();
            nf.y -= 2;
            nf.alpha -= 10;
            if (nf.alpha <= 0) it.remove();
        }
    }

    private void actualizarEfectosDaño() {
        long dañoActual = modelo.getUltimoDaño();
        if (dañoActual != ultimoDañoTrackeado) {
            ultimoDañoTrackeado = dañoActual;
            flashAlpha = 150;
            shakeIntensidad = 12;
        }
        if (flashAlpha > 0) flashAlpha = Math.max(0, flashAlpha - 18);
        if (shakeIntensidad > 0) {
            shakeX = (int)((rnd.nextFloat() - 0.5f) * shakeIntensidad);
            shakeY = (int)((rnd.nextFloat() - 0.5f) * shakeIntensidad);
            shakeIntensidad = Math.max(0, shakeIntensidad - 2);
        } else { shakeX = 0; shakeY = 0; }
    }

    // ── Renderizado principal ─────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        dibujarFondoParallax(g2);

        // Aplicar screen shake a todo el contenido del juego
        g2.translate(shakeX, shakeY);
        dibujarNave(g2);
        dibujarEnemigos(g2);
        dibujarDisparos(g2);
        dibujarObstaculos(g2);
        dibujarPowerUps(g2);
        dibujarExplosiones(g2);
        dibujarNumerosFlotantes(g2);
        dibujarDisparosEnemigos(g2);
        g2.translate(-shakeX, -shakeY);

        // HUD y overlays fuera del shake
        dibujarHUD(g2);
        if (flashAlpha > 0) dibujarFlash(g2);
        if (modelo.isNivelCompletado()) dibujarNivelCompletado(g2);
        else if (modelo.isPausado())    dibujarPausa(g2);
        if (!modelo.isJugadorVivo())    dibujarGameOver(g2);
    }

    // ── Fondo con parallax ────────────────────────────────────────────────────

    private void dibujarFondoParallax(Graphics2D g) {
        // Dos copias del fondo para scroll continuo
        int sy = (int) scrollFondo;
        if (fondoImage != null) {
            g.drawImage(fondoImage, 0, sy - 600, 600, 600, this);
            g.drawImage(fondoImage, 0, sy,       600, 600, this);
        } else {
            g.setColor(new Color(8, 8, 25));
            g.fillRect(0, 0, 600, 600);
        }

        // Capa lenta de estrellas (pequeñas)
        g.setColor(new Color(200, 210, 255, 130));
        for (int[] s : estrellas1) g.fillOval(s[0], s[1], 1, 1);

        // Capa rápida (un poco más grandes y brillantes)
        g.setColor(new Color(255, 255, 255, 180));
        for (int[] s : estrellas2) g.fillOval(s[0], s[1], 2, 2);
    }

    // ── Nave ──────────────────────────────────────────────────────────────────

    private void dibujarNave(Graphics2D g) {
        Nave nave = modelo.getNave();
        if (naveImage != null) g.drawImage(naveImage, nave.getX(), nave.getY(), this);
        else { g.setColor(Color.GREEN); g.fillRect(nave.getX(), nave.getY(), 30, 30); }

        if (nave.tieneEscudo()) {
            g.setColor(new Color(0, 200, 255, 110));
            g.fillOval(nave.getX() - 8, nave.getY() - 8, 46, 46);
            g.setColor(new Color(0, 200, 255, 220));
            g.setStroke(new BasicStroke(2f));
            g.drawOval(nave.getX() - 8, nave.getY() - 8, 46, 46);
            g.setStroke(new BasicStroke(1f));
            // Barra de duración
            int bx = nave.getX() - 10, by = nave.getY() - 18;
            g.setColor(new Color(40, 40, 40));
            g.fillRect(bx, by, 50, 5);
            g.setColor(new Color(0, 191, 255));
            g.fillRect(bx, by, (int)(50 * nave.getPorcentajeEscudoRestante()), 5);
            g.setColor(Color.WHITE); g.drawRect(bx, by, 50, 5);
        }

        // Barra de recarga junto a la nave
        dibujarBarraRecarga(g, nave);
    }

    private void dibujarBarraRecarga(Graphics2D g, Nave nave) {
        double recarga = modelo.getProgresoRecarga();
        int bx = nave.getX() - 5, by = nave.getY() + 34;
        g.setColor(new Color(30, 30, 30));
        g.fillRect(bx, by, 40, 4);
        g.setColor(recarga >= 1.0 ? new Color(0, 220, 255) : new Color(0, 120, 180));
        g.fillRect(bx, by, (int)(40 * recarga), 4);
    }

    // ── Enemigos ──────────────────────────────────────────────────────────────

    private void dibujarEnemigos(Graphics2D g) {
        Enemigo bossRef = null;
        for (Enemigo enemigo : modelo.getEnemigos()) {
            int w, h;
            Color tinte;
            switch (enemigo.getTipo()) {
                case RAPIDO: w = 20; h = 20; tinte = new Color(255, 220, 0,  130); break;
                case TANQUE: w = 45; h = 45; tinte = new Color(0,   200, 50, 130); break;
                case BOSS:   w = 60; h = 60; tinte = new Color(220, 30,  30, 160); break;
                default:     w = 30; h = 30; tinte = null;
            }
            if (enemigoImage != null) g.drawImage(enemigoImage, enemigo.getX(), enemigo.getY(), w, h, this);
            else { g.setColor(tinte != null ? tinte : Color.RED); g.fillRect(enemigo.getX(), enemigo.getY(), w, h); }
            if (tinte != null && enemigoImage != null) { g.setColor(tinte); g.fillRect(enemigo.getX(), enemigo.getY(), w, h); }
            if (enemigo.getSaludMaxima() > 1)
                dibujarBarraVidaEnemigo(g, enemigo.getX(), enemigo.getY() - 8, w, enemigo.getSalud(), enemigo.getSaludMaxima());
            if (enemigo.getTipo() == Enemigo.TipoEnemigo.BOSS) bossRef = enemigo;
        }
        if (bossRef != null) dibujarBarraBoss(g, bossRef.getSalud(), bossRef.getSaludMaxima());
    }

    private void dibujarBarraVidaEnemigo(Graphics2D g, int x, int y, int ancho, int salud, int max) {
        g.setColor(new Color(40, 40, 40));
        g.fillRect(x, y, ancho, 5);
        float pct = (float) salud / max;
        g.setColor(pct > 0.5f ? new Color(60, 200, 60) : pct > 0.25f ? new Color(220, 160, 0) : Color.RED);
        g.fillRect(x, y, (int)(ancho * pct), 5);
        g.setColor(new Color(80, 80, 80)); g.drawRect(x, y, ancho, 5);
    }

    private void dibujarBarraBoss(Graphics2D g, int salud, int max) {
        int bw = 320, bh = 14, bx = (600 - bw) / 2, by = 570;
        g.setColor(new Color(25, 25, 25, 210));
        g.fillRoundRect(bx - 44, by - 4, bw + 58, bh + 8, 8, 8);
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(new Color(255, 80, 80));
        g.drawString("BOSS", bx - 42, by + 11);
        float pct = (float) salud / max;
        g.setColor(pct > 0.5f ? new Color(200, 30, 30) : pct > 0.25f ? new Color(200, 100, 0) : new Color(255, 0, 0));
        g.fillRect(bx, by, (int)(bw * pct), bh);
        g.setColor(new Color(100, 100, 100)); g.drawRect(bx, by, bw, bh);
    }

    // ── Disparos ──────────────────────────────────────────────────────────────

    private void dibujarDisparos(Graphics2D g) {
        g.setColor(Color.YELLOW);
        for (Disparo d : modelo.getDisparos()) g.fillRect(d.getX(), d.getY(), 5, 10);
    }

    private void dibujarDisparosEnemigos(Graphics2D g) {
        g.setColor(new Color(255, 80, 80));
        for (DisparoEnemigo d : modelo.getDisparosEnemigos()) g.fillRect(d.getX(), d.getY(), 5, 10);
    }

    // ── Obstáculos ────────────────────────────────────────────────────────────

    private void dibujarObstaculos(Graphics2D g) {
        for (Obstaculo o : modelo.getObstaculos()) {
            if (obstaculoImage != null) g.drawImage(obstaculoImage, o.getX(), o.getY(), this);
            else { g.setColor(new Color(100, 60, 20)); g.fillRect(o.getX(), o.getY(), 40, 40); }
        }
    }

    // ── Power-ups ─────────────────────────────────────────────────────────────

    private void dibujarPowerUps(Graphics2D g) {
        for (PowerUp p : modelo.getPowerUps()) {
            Color bg; String letra;
            switch (p.getTipo()) {
                case DISPARO_DOBLE:     bg = new Color(0,  200, 220); letra = "x2"; break;
                case ESCUDO_EXTRA:      bg = new Color(60, 80,  220); letra = "SC"; break;
                case VELOCIDAD_DISPARO: bg = new Color(230,150, 0  ); letra = "VL"; break;
                default:                bg = Color.WHITE;             letra = "?";
            }
            int x = p.getX(), y = p.getY(), s = 22;
            g.setColor(bg); g.fillRoundRect(x, y, s, s, 6, 6);
            g.setColor(Color.WHITE); g.setStroke(new BasicStroke(1.5f)); g.drawRoundRect(x, y, s, s, 6, 6); g.setStroke(new BasicStroke(1f));
            g.setFont(new Font("Arial", Font.BOLD, 9));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(Color.BLACK);
            g.drawString(letra, x + (s - fm.stringWidth(letra)) / 2, y + (s + fm.getAscent() - fm.getDescent()) / 2 - 1);
        }
    }

    // ── Explosiones programáticas ─────────────────────────────────────────────

    private void dibujarExplosiones(Graphics2D g) {
        for (Map.Entry<Point, Integer> entry : animacionesExplosion.entrySet()) {
            dibujarExplosion(g, entry.getKey().x, entry.getKey().y, entry.getValue());
        }
    }

    private void dibujarExplosion(Graphics2D g, int cx, int cy, int frame) {
        float progreso = (float) frame / TOTAL_FRAMES;
        float alpha = 1.0f - progreso;
        // Radio crece y luego se contrae
        int radio = (int)(25 * Math.sin(progreso * Math.PI));

        if (frame < 3) {
            // Flash inicial blanco/amarillo
            int flashSize = (int)(50 * (1 - progreso * 0.5));
            g.setColor(new Color(255, 255, 220, (int)(220 * alpha)));
            g.fillOval(cx - flashSize / 2, cy - flashSize / 2, flashSize, flashSize);
        }
        // Bola de fuego naranja
        g.setColor(new Color(255, 120, 0, (int)(200 * alpha)));
        g.fillOval(cx - radio, cy - radio, radio * 2, radio * 2);
        // Núcleo amarillo interior
        int nucleo = radio / 2;
        g.setColor(new Color(255, 230, 50, (int)(180 * alpha)));
        g.fillOval(cx - nucleo, cy - nucleo, nucleo * 2, nucleo * 2);
        // Anillo de humo al final
        if (frame >= 5) {
            int humo = (int)(35 * progreso);
            g.setColor(new Color(180, 100, 50, (int)(100 * alpha)));
            g.setStroke(new BasicStroke(3f));
            g.drawOval(cx - humo, cy - humo, humo * 2, humo * 2);
            g.setStroke(new BasicStroke(1f));
        }
    }

    // ── Números flotantes ─────────────────────────────────────────────────────

    private void dibujarNumerosFlotantes(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 14));
        for (NumeroFlotante nf : numerosFlotantes) {
            g.setColor(new Color(255, 230, 50, nf.alpha));
            g.drawString(nf.texto, nf.x, nf.y);
        }
    }

    // ── HUD profesional ───────────────────────────────────────────────────────

    private void dibujarHUD(Graphics2D g) {
        // Nivel y puntuación
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Nivel: " + modelo.getNivel(), 10, 25);
        g.drawString("Puntuación: " + modelo.getPuntuacion(), 10, 48);

        // Badge de dificultad (esquina superior derecha)
        dibujarBadgeDificultad(g);

        // Barra de vida gráfica
        dibujarBarraVidaJugador(g);

        // Indicadores de power-up activos
        int iy = 92;
        Nave.TipoDisparo td = modelo.getNave().getTipoDisparo();
        if (td != Nave.TipoDisparo.SIMPLE) {
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.setColor(new Color(0, 220, 240));
            g.drawString(td == Nave.TipoDisparo.DOBLE ? "▲ DISPARO x2" : "▲ DISPARO x3", 10, iy);
            iy += 16;
        }
        if (modelo.isVelocidadDisparoActiva()) {
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.setColor(new Color(240, 170, 0));
            g.drawString("▲ VELOCIDAD+", 10, iy);
        }

        // Combo multiplier (centro-arriba cuando > 1)
        int combo = modelo.getCombo();
        if (combo > 1) {
            g.setFont(new Font("Arial", Font.BOLD, 28));
            String comboTxt = "x" + combo;
            FontMetrics fm = g.getFontMetrics();
            int cx = (600 - fm.stringWidth(comboTxt)) / 2;
            // Sombra
            g.setColor(new Color(180, 100, 0, 160));
            g.drawString(comboTxt, cx + 2, 32);
            // Texto principal
            g.setColor(new Color(255, 200, 0));
            g.drawString(comboTxt, cx, 30);
            // Etiqueta
            g.setFont(new Font("Arial", Font.PLAIN, 11));
            fm = g.getFontMetrics();
            String label = "COMBO";
            g.setColor(new Color(255, 200, 0, 180));
            g.drawString(label, (600 - fm.stringWidth(label)) / 2, 45);
        }
    }

    private void dibujarBadgeDificultad(Graphics2D g) {
        String nombre = modelo.getDificultad().nombre.toUpperCase();
        Color color;
        switch (modelo.getDificultad()) {
            case FACIL:   color = new Color(0, 180, 60);  break;
            case DIFICIL: color = new Color(220, 40, 40); break;
            default:      color = new Color(200, 160, 0);
        }
        g.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        int pw = fm.stringWidth(nombre) + 10, ph = 16;
        int px = 600 - pw - 8, py = 8;
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
        g.fillRoundRect(px, py, pw, ph, 6, 6);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(px, py, pw, ph, 6, 6);
        g.setStroke(new BasicStroke(1f));
        g.setColor(color);
        g.drawString(nombre, px + 5, py + ph - 3);
    }

    private void dibujarBarraVidaJugador(Graphics2D g) {
        int maxV = modelo.getVidasMaximas();
        int v    = modelo.getVidas();
        float pct = (float) v / maxV;

        int bx = 10, by = 57, bw = 150, bh = 12;
        // Fondo oscuro
        g.setColor(new Color(30, 30, 30));
        g.fillRoundRect(bx - 1, by - 1, bw + 2, bh + 2, 6, 6);
        // Relleno con color dinámico
        Color barColor = pct > 0.5f ? new Color(50, 200, 60)
                       : pct > 0.25f ? new Color(220, 160, 0)
                       : new Color(220, 40, 40);
        g.setColor(barColor);
        g.fillRoundRect(bx, by, (int)(bw * pct), bh, 5, 5);
        // Borde
        g.setColor(new Color(100, 100, 100));
        g.drawRoundRect(bx, by, bw, bh, 6, 6);
        // Etiqueta con contador
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(Color.WHITE);
        g.drawString("VIDA  " + v + "/" + maxV, bx + 4, by + bh - 1);
    }

    // ── Nivel completado ──────────────────────────────────────────────────────

    private void dibujarNivelCompletado(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 155));
        g.fillRect(0, 0, 600, 600);

        // Destellos dorados en esquinas
        g.setColor(new Color(255, 215, 0, 60));
        for (int i = 0; i < 8; i++) {
            int rx = (int)(Math.random() * 600), ry = (int)(Math.random() * 600);
            g.fillOval(rx, ry, 3 + (int)(Math.random() * 5), 3 + (int)(Math.random() * 5));
        }

        // Título "¡NIVEL X COMPLETADO!"
        g.setFont(new Font("Arial", Font.BOLD, 38));
        FontMetrics fm = g.getFontMetrics();
        String linea1 = "¡NIVEL " + modelo.getNivel() + " COMPLETADO!";
        // Sombra
        g.setColor(new Color(0, 120, 0, 120));
        g.drawString(linea1, (600 - fm.stringWidth(linea1)) / 2 + 3, 243);
        // Texto principal
        g.setColor(new Color(80, 255, 120));
        g.drawString(linea1, (600 - fm.stringWidth(linea1)) / 2, 240);

        // Puntuación acumulada
        g.setFont(new Font("Arial", Font.BOLD, 22));
        fm = g.getFontMetrics();
        String pts = "Puntuación: " + modelo.getPuntuacion();
        g.setColor(Color.WHITE);
        g.drawString(pts, (600 - fm.stringWidth(pts)) / 2, 290);

        // Aviso próximo nivel
        g.setFont(new Font("Arial", Font.ITALIC, 16));
        fm = g.getFontMetrics();
        String siguiente = "¡Preparáte para el Nivel " + (modelo.getNivel() + 1) + "!";
        g.setColor(new Color(180, 220, 255));
        g.drawString(siguiente, (600 - fm.stringWidth(siguiente)) / 2, 330);
    }

    // ── Flash de daño ─────────────────────────────────────────────────────────

    private void dibujarFlash(Graphics2D g) {
        g.setColor(new Color(255, 0, 0, flashAlpha));
        g.fillRect(0, 0, 600, 600);
    }

    // ── Pausa ─────────────────────────────────────────────────────────────────

    private void dibujarPausa(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, 600, 600);

        g.setFont(new Font("Arial", Font.BOLD, 52));
        FontMetrics fm = g.getFontMetrics();
        String txt = "PAUSADO";
        g.setColor(new Color(0, 100, 200, 90));
        g.drawString(txt, (600 - fm.stringWidth(txt)) / 2 + 3, 283);
        g.setColor(Color.WHITE);
        g.drawString(txt, (600 - fm.stringWidth(txt)) / 2, 280);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        fm = g.getFontMetrics();
        String hint = "Presiona ESC para continuar";
        g.setColor(new Color(180, 200, 255));
        g.drawString(hint, (600 - fm.stringWidth(hint)) / 2, 335);
    }

    // ── Game Over ─────────────────────────────────────────────────────────────

    private void dibujarGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, 600, 600);

        g.setFont(new Font("Arial", Font.BOLD, 52));
        FontMetrics fm = g.getFontMetrics();
        String go = "GAME OVER";
        g.setColor(new Color(160, 0, 0));
        g.drawString(go, (600 - fm.stringWidth(go)) / 2 + 3, 203);
        g.setColor(new Color(255, 60, 60));
        g.drawString(go, (600 - fm.stringWidth(go)) / 2, 200);

        g.setFont(new Font("Arial", Font.BOLD, 24));
        fm = g.getFontMetrics();
        String score = "Puntuación: " + modelo.getPuntuacion();
        g.setColor(Color.WHITE);
        g.drawString(score, (600 - fm.stringWidth(score)) / 2, 255);

        if (GestorPuntuaciones.esNuevRecord(modelo.getPuntuacion())) {
            g.setFont(new Font("Arial", Font.BOLD, 16));
            fm = g.getFontMetrics();
            String rec = "★  ¡NUEVO RECORD!  ★";
            g.setColor(new Color(255, 215, 0));
            g.drawString(rec, (600 - fm.stringWidth(rec)) / 2, 285);
        }

        botonReintentar = new Rectangle(130, 330, 145, 45);
        botonMenu       = new Rectangle(325, 330, 145, 45);
        dibujarBotonGameOver(g, botonReintentar, "Reintentar", new Color(0, 90, 200));
        dibujarBotonGameOver(g, botonMenu,       "Menú",       new Color(80, 80, 80));

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        fm = g.getFontMetrics();
        String hint = "Haz clic en un botón para continuar";
        g.setColor(new Color(140, 150, 170));
        g.drawString(hint, (600 - fm.stringWidth(hint)) / 2, 410);
    }

    private void dibujarBotonGameOver(Graphics2D g, Rectangle r, String texto, Color color) {
        Point mouse = getMousePosition();
        boolean hover = mouse != null && r.contains(mouse);
        g.setColor(hover ? color.brighter() : color);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g.setColor(new Color(200, 200, 200));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        g.drawString(texto,
                r.x + (r.width - fm.stringWidth(texto)) / 2,
                r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
    }

    // ── MouseListener ─────────────────────────────────────────────────────────

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!modelo.isJugadorVivo() && gameOverListener != null) {
            if (botonReintentar != null && botonReintentar.contains(e.getPoint()))
                gameOverListener.onReintentar();
            else if (botonMenu != null && botonMenu.contains(e.getPoint()))
                gameOverListener.onMenu();
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
