package eu.davidknotek.ytdownloader.tasks;

import eu.davidknotek.ytdownloader.typy.FormatVidea;
import eu.davidknotek.ytdownloader.enums.TypVidea;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoAnalyzer extends Task<List<FormatVidea>> {

    private String url;
    private final List<String> errors;
    private final List<FormatVidea> allFormatList; //seznam formatu jednoho videa

    /**
     * Konstruktor. Přivede url.
     */
    public VideoAnalyzer(String url) {
        this.url = url;
        errors = new ArrayList<>();
        allFormatList = FXCollections.observableArrayList();
    }

    @Override
    protected List<FormatVidea> call() throws Exception {
        updateMessage("Analyzuji URL, čekejte prosím...");
        String nazevVidea = zjistitNazevVidea();
        if (nazevVidea != null) {
            updateTitle(nazevVidea);
            analyzovatURL();
            updateMessage("Analýza URL proběhla v pořádku.");
        } else {
            updateMessage("Analýza URL neproběhla. Překontrolujte adresu videa.");
        }
        return allFormatList;
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<FormatVidea> getAllFormatList() {
        return allFormatList;
    }

    /*====================================
     * Private methods
     =====================================*/

    private String zjistitNazevVidea() throws IOException, InterruptedException {
        List<String> lines = provedPrikaz("youtube-dl", "-e", url);
        if (lines.size() > 0) {
            return lines.get(0);
        }
        return null;
    }

    /**
     * Analyzuje se URL videa. Ziskají se formaty, ktere pujdou stahnout.
     * @throws IOException vyjimka IOE
     * @throws InterruptedException vyjimka IE
     */
    private void analyzovatURL() throws IOException, InterruptedException {
        List<String> lines = provedPrikaz("youtube-dl", "-F", url);
        Pattern pFormatCode = Pattern.compile("^([0-9]{2,3}) ");
        Pattern pExtension = Pattern.compile(" (mp4|webm|m4a) ");
        Pattern pResolution = Pattern.compile(" ([0-9]{3,4}x[0-9]{3,4}) ");
        Pattern pFPS = Pattern.compile(" ([0-9]{2,3}fps),");
        Pattern pFileSize = Pattern.compile(" ([0-9]{1,3}\\.[0-9]{1,2}(MiB|GiB))");
        Pattern pAudioQuality = Pattern.compile(" (\\([0-9]{5}Hz\\))");

        for (String line : lines) {
            if (isCancelled()) {
                break;
            }
            FormatVidea formatVidea = new FormatVidea();
            // FormatCode
            Matcher matcher = pFormatCode.matcher(line);
            if (matcher.find())
                formatVidea.setFormatCode(matcher.group(1));
            else
                continue;
            // Extension
            matcher = pExtension.matcher(line);
            formatVidea.setExtension(matcher.find() ? matcher.group(1) : "");
            // Resolution
            matcher = pResolution.matcher(line);
            formatVidea.setResolution(matcher.find() ? matcher.group(1) : "");
            // FPS
            matcher = pFPS.matcher(line);
            formatVidea.setFps(matcher.find() ? matcher.group(1) : "");
            // FileSize
            matcher = pFileSize.matcher(line);
            formatVidea.setFileSize(matcher.find() ? matcher.group(1) : "");
            // AudioQuality
            matcher = pAudioQuality.matcher(line);
            formatVidea.setAudioQuality(matcher.find() ? matcher.group(1) : "");
            // FileTyp
            if (line.contains("audio only")) formatVidea.setTypVidea(TypVidea.AUDIO_ONLY);
            else if (line.contains("video only")) formatVidea.setTypVidea(TypVidea.VIDEO_ONLY);
            else formatVidea.setTypVidea(TypVidea.VIDEO);

            allFormatList.add(formatVidea);
        }

//        for (FormatVidea format : seznamFormatu) {
//            System.out.println(format.getFormatCode() + " / " +
//                    format.getExtension() + " / " +
//                    format.getFileSize() + " / " +
//                    format.getTyp());
//        }
    }

    /**
     * Provede se jakýkoliv příkaz
     * @param prikaz příkaz
     */
    private List<String> provedPrikaz(String... prikaz) throws IOException, InterruptedException {
        List<String> lines = new ArrayList<>();
        ProcessBuilder pb = new ProcessBuilder(prikaz);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                if (isCancelled()) {
                    break;
                }
                lines.add(line);
            }

            while ((line = error.readLine()) != null) {
                if (isCancelled()) {
                    break;
                }
                errors.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        process.waitFor();
        process.destroy();

        return lines;
    }
}
