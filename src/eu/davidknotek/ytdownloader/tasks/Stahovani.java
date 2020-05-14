package eu.davidknotek.ytdownloader.tasks;

import eu.davidknotek.ytdownloader.typy.Fronta;
import eu.davidknotek.ytdownloader.typy.VideoKeStazeni;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stahovani extends Task<String> {

    private static final String YOUTUBEDL = "youtube-dl";
    private final Fronta fronta;

    public Stahovani(Fronta fronta) {
        this.fronta = fronta;
    }

    @Override
    protected String call() throws Exception {
        ObservableList<VideoKeStazeni> seznamVideiKeStazeni = fronta.getSeznamVideiKeStazeni();
        for (VideoKeStazeni videoKeStazeni : seznamVideiKeStazeni) {
            // Stahneme video soubor
            if ((videoKeStazeni.getVideoCode()) != null && (!videoKeStazeni.getVideoCode().equals(""))) {
                updateMessage("Stahuji video: " + videoKeStazeni.getVideoName());
                String outputVideo = zpracovatNazevVidea("video", videoKeStazeni.getVideoName(), videoKeStazeni.getVideoCode(), videoKeStazeni.getExtensionVideo());
                stahnout(YOUTUBEDL, "-f", videoKeStazeni.getVideoCode(), videoKeStazeni.getUrl(), "-o", outputVideo);
            }

            // Stahneme audio soubor pokud existuje
            if ((videoKeStazeni.getAudioCode() != null) && (!videoKeStazeni.getAudioCode().equals(""))) {
                updateMessage("Stahuji audio: " + videoKeStazeni.getVideoName());
                String outputAudio = zpracovatNazevVidea("audio", videoKeStazeni.getVideoName(), videoKeStazeni.getAudioCode(), videoKeStazeni.getExtensionVideo());
                stahnout(YOUTUBEDL, "-f", videoKeStazeni.getAudioCode(), videoKeStazeni.getUrl(), "-o", outputAudio);
            }
        }
        return null;
    }

    /**
     * Budeme stahovat YT soubor.
     * @param prikaz prikaz
     * @throws IOException vyjimka
     */
    private void stahnout(String... prikaz) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(prikaz);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isCancelled()) {
                    process.destroy();
                    break;
                }
                analyzujRadek(line);
            }

            updateProgress(0.0, 100.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Analyzuje radek vystupu programu
     * @param line analyzovany radek
     */
    private void analyzujRadek(String line) {
        Pattern pProcenta = Pattern.compile(" ([0-9]{1,3}\\.[0-9]{1})% ");
        Pattern pRychlostStahovani = Pattern.compile("([0-9]{1,4}\\.[0-9]{1,2}(KiB|MiB|GiB)/s)");
        Pattern pZbyvajiciCas = Pattern.compile("([0-9]{2}:[0-9]{2})");

        Matcher matcher = pProcenta.matcher(line);
        if (matcher.find()) {
            updateProgress(Double.parseDouble(matcher.group(1)), 100);
        }

        matcher = pRychlostStahovani.matcher(line);
        if (matcher.find()) {
//            updateMessage(matcher.group(1));
        }
    }

    private String zpracovatNazevVidea(String predpona, String nazev, String code, String extension) {
        String upravenyNazev = predpona + "-";
        upravenyNazev += nazev.trim().toLowerCase()
                .replace(" ", "")
                .replace("/", "");
        upravenyNazev += "-" + code;
        upravenyNazev += "." + extension;

        return upravenyNazev;
    }
}
