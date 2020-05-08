package eu.davidknotek.ytdownloader;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoAnalyzer extends Task<String> {

    private String url;
    private final YTVideo ytVideo;

    private final List<String> errors;
    private final List<String> lines;

    /**
     * Konstruktor. Přivede url.
     */
    public VideoAnalyzer(String url) {
        this.url = url;
        ytVideo = new YTVideo();
        lines = new ArrayList<>();
        errors = new ArrayList<>();
    }

    @Override
    protected String call() throws Exception {
        zjistitNazev();
        //seznamVidei();
        return "Hotovo";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getErrors() {
        return errors;
    }

    public YTVideo getYtVideo() {
        return ytVideo;
    }

    /*====================================
     * Private methods
     =====================================*/

    private void zjistitNazev() throws IOException, InterruptedException {
        provedPrikaz("youtube-dl", "-e", url);
        if (lines.size() > 0) {
            ytVideo.setName(lines.get(0));
//            updateMessage(lines.get(0));
            System.out.println(lines.get(0));
        }
    }

    private void seznamVidei() throws IOException, InterruptedException {
        provedPrikaz("youtube-dl", "-F", url);
        for (String line : lines) {
            String[] video = line.split(",", 2);
            if (video.length == 1) continue;

            System.out.println(video[0]);
            //System.out.print(line + " /"+video.length + "\n");
        }
    }

    /**
     * Provede se jakýkoliv příkaz
     * @param prikaz příkaz
     */
    private void provedPrikaz(String... prikaz) throws IOException, InterruptedException {
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
    }
}
