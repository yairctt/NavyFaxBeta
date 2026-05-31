package modelo;

import javax.sound.sampled.*;
import java.io.InputStream;

public class SoundManager {
    private Clip musicaFondo;
    private FloatControl controlVolumen;

    // Clips precargados — se reutilizan en cada reproducción
    private Clip clipDisparo;
    private Clip clipExplosion;
    private Clip clipDaño;
    private Clip clipGameOver;
    private Clip clipNivelCompleto;

    public SoundManager() {
        clipDisparo     = cargarClip("/recursos/sonidos/disparo.wav");
        clipExplosion   = cargarClip("/recursos/sonidos/explosion.wav");
        clipDaño        = cargarClip("/recursos/sonidos/daño.wav");
        clipGameOver    = cargarClip("/recursos/sonidos/game_over.wav");
        // Reutiliza el sonido de explosión para nivel completado (tono distinto no disponible)
        clipNivelCompleto = cargarClip("/recursos/sonidos/explosion.wav");
    }

    // ── Música de fondo ───────────────────────────────────────────────────────

    public void iniciarMusicaFondo() {
        try {
            InputStream stream = getClass().getResourceAsStream("/recursos/sonidos/musica.wav");
            if (stream == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(stream);
            DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
            if (!AudioSystem.isLineSupported(info)) return;
            musicaFondo = (Clip) AudioSystem.getLine(info);
            musicaFondo.open(ais);
            if (musicaFondo.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                controlVolumen = (FloatControl) musicaFondo.getControl(FloatControl.Type.MASTER_GAIN);
                controlVolumen.setValue(controlVolumen.getMaximum());
            }
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception ignored) {}
    }

    public void detenerMusica() {
        if (musicaFondo != null && musicaFondo.isOpen()) {
            musicaFondo.stop();
            musicaFondo.close();
        }
    }

    public void pausarMusica() {
        if (musicaFondo != null && musicaFondo.isRunning()) musicaFondo.stop();
    }

    public void reanudarMusica() {
        if (musicaFondo != null && !musicaFondo.isRunning() && musicaFondo.isOpen())
            musicaFondo.start();
    }

    public void setVolumen(float volumen) {
        if (controlVolumen != null) {
            float min = controlVolumen.getMinimum(), max = controlVolumen.getMaximum();
            controlVolumen.setValue(min + (max - min) * volumen);
        }
    }

    // ── Efectos de sonido ─────────────────────────────────────────────────────

    public void reproducirDisparo()      { reproducirClip(clipDisparo); }
    public void reproducirExplosion()    { reproducirClip(clipExplosion); }
    public void reproducirDaño()         { reproducirClip(clipDaño); }
    public void reproducirGameOver()     { reproducirClip(clipGameOver); }
    public void reproducirNivelCompleto(){ reproducirClip(clipNivelCompleto); }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Carga un Clip desde recursos; retorna null si falla. */
    private Clip cargarClip(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) return null;
            AudioInputStream ais = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception ignored) {
            return null;
        }
    }

    /** Detiene, rebobina y reproduce un Clip reutilizable. */
    private void reproducirClip(Clip clip) {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void detenerTodo() {
        detenerMusica();
        cerrarClip(clipDisparo);
        cerrarClip(clipExplosion);
        cerrarClip(clipDaño);
        cerrarClip(clipGameOver);
        cerrarClip(clipNivelCompleto);
    }

    private void cerrarClip(Clip clip) {
        if (clip != null && clip.isOpen()) clip.close();
    }
}
