package modelo;

import java.io.*;
import java.util.*;

public class GestorPuntuaciones {
    private static final String ARCHIVO = System.getProperty("user.home")
            + File.separator + ".navyfax" + File.separator + "puntuaciones.dat";
    private static final int MAX_PUNTUACIONES = 10;

    public static List<Integer> cargarPuntuaciones() {
        List<Integer> lista = new ArrayList<>();
        File f = new File(ARCHIVO);
        if (!f.exists()) return lista;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                try { lista.add(Integer.parseInt(linea.trim())); } catch (NumberFormatException ignored) {}
            }
        } catch (IOException ignored) {}
        lista.sort(Collections.reverseOrder());
        return lista;
    }

    public static void guardarPuntuacion(int puntuacion) {
        if (puntuacion <= 0) return;
        List<Integer> lista = cargarPuntuaciones();
        lista.add(puntuacion);
        lista.sort(Collections.reverseOrder());
        if (lista.size() > MAX_PUNTUACIONES) lista = lista.subList(0, MAX_PUNTUACIONES);
        try {
            File f = new File(ARCHIVO);
            f.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                for (int p : lista) bw.write(p + "\n");
            }
        } catch (IOException ignored) {}
    }

    public static boolean esNuevRecord(int puntuacion) {
        List<Integer> lista = cargarPuntuaciones();
        return lista.isEmpty() || puntuacion > lista.get(0);
    }
}
