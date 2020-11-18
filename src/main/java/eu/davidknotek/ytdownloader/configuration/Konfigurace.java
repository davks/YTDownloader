package eu.davidknotek.ytdownloader.configuration;

import java.io.*;
import java.util.HashMap;

public class Konfigurace {

    private static final String SOUBOR = "configuration.conf";

    private static final String DIRECTORY = "directory=";
    private static final String WINDOW_WIDTH = "window_width=";
    private static final String WINDOW_HEIGHT = "window_height=";

    private static String directory = "";
    private static String windowWidth = "";
    private static String windowHeight = "";

    public static void nacistNastaveni() {
        try (BufferedReader br = new BufferedReader(new FileReader(SOUBOR))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(DIRECTORY)) directory = line.substring(DIRECTORY.length());
                if (line.startsWith(WINDOW_WIDTH)) windowWidth = line.substring(WINDOW_WIDTH.length());
                if (line.startsWith(WINDOW_HEIGHT)) windowHeight = line.substring(WINDOW_HEIGHT.length());
            }

        } catch (IOException e) {
            System.out.println("Konfigurační soubor neexistuje.");
        }
    }

    public static void ulozitNastaveni() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SOUBOR))) {
            String radek = "";

            radek += (DIRECTORY + directory + "\n");
            radek += (WINDOW_WIDTH + windowWidth + "\n");
            radek += (WINDOW_HEIGHT + windowHeight + "\n");

            bw.write(radek);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDirectory() {
        return directory;
    }

    public static void setDirectory(String directory) {
        Konfigurace.directory = directory;
    }

    public static String getWindowWidth() {
        return windowWidth;
    }

    public static void setWindowWidth(String windowWidth) {
        Konfigurace.windowWidth = windowWidth;
    }

    public static String getWindowHeight() {
        return windowHeight;
    }

    public static void setWindowHeight(String windowHeight) {
        Konfigurace.windowHeight = windowHeight;
    }
}
