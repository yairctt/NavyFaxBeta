package modelo;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class SoundManager {
    private Clip musicaFondo;
    private FloatControl controlVolumen;
    private Clip sonidoExplosion;
    private Clip sonidoDisparo;
    private Clip sonidoDaño;
    private Clip sonidoGameOver;

    public void iniciarMusicaFondo() {
        try {
            InputStream audioFile = getClass().getResourceAsStream("/recursos/sonidos/musica.wav");
            if (audioFile == null) {
                System.out.println("No se pudo encontrar el archivo de música");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            System.out.println("Stream de audio creado");

            AudioFormat format = audioStream.getFormat();
            System.out.println("Formato de audio: " + format.toString());

            DataLine.Info info = new DataLine.Info(Clip.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Línea de audio no soportada");
                return;
            }

            musicaFondo = (Clip) AudioSystem.getLine(info);
            musicaFondo.open(audioStream);

            // Configurar volumen al máximo
            if (musicaFondo.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                controlVolumen = (FloatControl) musicaFondo.getControl(FloatControl.Type.MASTER_GAIN);
                // Poner volumen al máximo para pruebas
                controlVolumen.setValue(controlVolumen.getMaximum());
                System.out.println("Volumen configurado al máximo: " + controlVolumen.getValue() + "dB");
            }

            musicaFondo.start(); // Primero iniciamos una vez
            System.out.println("Reproducción iniciada");

            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY); // Luego configuramos el loop
            System.out.println("Loop configurado");

            // Verificar estado
            System.out.println("Estado del clip: " +
                    (musicaFondo.isRunning() ? "Reproduciendo" : "Detenido") +
                    " - Frame position: " + musicaFondo.getFramePosition());

        } catch (Exception e) {
            System.out.println("Error detallado al cargar la música:");
            e.printStackTrace();
        }
    }

    public void detenerMusica() {
        if (musicaFondo != null) {
            musicaFondo.stop();
            musicaFondo.close();
            System.out.println("Música detenida");
        }
    }

    public void setVolumen(float volumen) {
        if (controlVolumen != null) {
            // Convertir de 0-1 a decibeles
            float min = controlVolumen.getMinimum();
            float max = controlVolumen.getMaximum();
            float dB = min + (max - min) * volumen;
            controlVolumen.setValue(dB);
            System.out.println("Volumen ajustado a: " + dB + "dB");
        }
    }

    public void pausarMusica() {
        if (musicaFondo != null && musicaFondo.isRunning()) {
            musicaFondo.stop();
            System.out.println("Música pausada");
        }
    }

    public void reanudarMusica() {
        if (musicaFondo != null && !musicaFondo.isRunning()) {
            musicaFondo.start();
            System.out.println("Música reanudada");
        }
    }

    public void reproducirExplosion() {
        try {
            // Cargar el sonido de explosión
            InputStream audioFile = getClass().getResourceAsStream("/recursos/sonidos/explosion.wav");
            if (audioFile == null) {
                System.out.println("No se pudo encontrar el archivo de explosión");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            sonidoExplosion = AudioSystem.getClip();
            sonidoExplosion.open(audioStream);

            // Configurar volumen para la explosión
            if (sonidoExplosion.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl volumeControl = (FloatControl) sonidoExplosion.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volumeControl.getMaximum() - 3.0f); // Un poco más bajo que el máximo
            }

            sonidoExplosion.start();

        } catch (Exception e) {
            System.out.println("Error al reproducir sonido de explosión: " + e.getMessage());
        }
    }

    public void reproducirDaño() {
        try {
            // Cargar el sonido de daño
            InputStream audioFile = getClass().getResourceAsStream("/recursos/sonidos/daño.wav");
            if (audioFile == null) {
                System.out.println("No se pudo encontrar el archivo de daño");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            sonidoDaño = AudioSystem.getClip();
            sonidoDaño.open(audioStream);

            // Configurar volumen para la bala
            if (sonidoDaño.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl volumeControl = (FloatControl) sonidoDaño.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volumeControl.getMaximum() - 3.0f); // Un poco más bajo que el máximo
            }

            sonidoDaño.start();

        } catch (Exception e) {
            System.out.println("Error al reproducir sonido de explosión: " + e.getMessage());
        }
    }

    public void reproducirDisparo() {
        try {
            // Cargar el sonido de disparo
            InputStream audioFile = getClass().getResourceAsStream("/recursos/sonidos/disparo.wav");
            if (audioFile == null) {
                System.out.println("No se pudo encontrar el archivo de disparo");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            sonidoDisparo = AudioSystem.getClip();
            sonidoDisparo.open(audioStream);

            // Configurar volumen para la bala
            if (sonidoDisparo.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl volumeControl = (FloatControl) sonidoDisparo.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volumeControl.getMaximum() - 3.0f); // Un poco más bajo que el máximo
            }

            sonidoDisparo.start();

        } catch (Exception e) {
            System.out.println("Error al reproducir sonido de disparo: " + e.getMessage());
        }
    }

    public void reproducirGameOver() {
        try {
            // Cargar el sonido de game over
            InputStream audioFile = getClass().getResourceAsStream("/recursos/sonidos/game_over.wav");
            if (audioFile == null) {
                System.out.println("No se pudo encontrar el archivo de game_over");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            sonidoGameOver = AudioSystem.getClip();
            sonidoGameOver.open(audioStream);

            // Configurar volumen para la bala
            if (sonidoGameOver.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl volumeControl = (FloatControl) sonidoGameOver.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volumeControl.getMaximum() - 3.0f); // Un poco más bajo que el máximo
            }

            sonidoGameOver.start();

        } catch (Exception e) {
            System.out.println("Error al reproducir sonido de explosión: " + e.getMessage());
        }
    }



    public void detenerTodo() {
        detenerMusica();
        if (sonidoExplosion != null) {
            sonidoExplosion.close();
        }
        if (sonidoDisparo != null) {
            sonidoDisparo.close();
        }
        if (sonidoDaño != null) {
            sonidoDaño.close();
        }
        if (sonidoGameOver != null) {
            sonidoGameOver.close();
        }
    }

}