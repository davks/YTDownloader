package eu.davidknotek.ytdownloader.configuration;

import java.io.*;

public class Konfigurace {
    private static final String SOUBOR = "configuration.conf";
    private static final String DIRECTORY = "directory=";

    private static String cesta = "";

    public static void nacistNastaveni() {
        try (BufferedReader br = new BufferedReader(new FileReader(SOUBOR))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                System.out.println(line);
                if (line.startsWith(DIRECTORY)) {
                    cesta = line.substring(DIRECTORY.length());
                }
            }

        } catch (IOException e) {
            System.out.println("Konfigurační soubor neexistuje.");
        }
    }

    public static void ulozitNastaveni() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SOUBOR))) {
            bw.write(DIRECTORY + cesta + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCesta() {
        return cesta;
    }

    public static void setCesta(String cesta) {
        Konfigurace.cesta = cesta;
    }
}
