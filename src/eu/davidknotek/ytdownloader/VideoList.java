package eu.davidknotek.ytdownloader;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoList extends Task<String> {

    enum Typ {
        VIDEO, VIDEO_ONLY, AUDIO_ONLY
    }

    enum SpustitPrikaz {
        ANALYZOVAT, STAHNOUT
    }

    private String url;
    private YTVideo ytVideo;

    private SpustitPrikaz spustitPrikaz;

    private List<String> errors;
    private List<String> lines;

    @Override
    protected String call() throws Exception {
        updateMessage("Analyzuji...");
        if (spustitPrikaz == SpustitPrikaz.ANALYZOVAT) {
            analyzovat();
        }
        if (spustitPrikaz == SpustitPrikaz.STAHNOUT) {
            stahnout();
        }
        updateMessage("Hotovo...");
        return null;
    }

    public VideoList(String url, SpustitPrikaz spustitPrikaz) {
        this.url = url;
        this.spustitPrikaz = spustitPrikaz;
        ytVideo = new YTVideo();
    }

    private void stahnout() {
        System.out.println("stahuji...");
    }

    private void analyzovat() {
        zjistitNazev();
        seznamVidei();

        //System.out.println(ytVideo.getName());
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
    private void seznamVidei() {
        List<String> prikaz = new ArrayList<>(Arrays.asList("youtube-dl", "-F", url));
        lines = provedPrikaz(prikaz);
        for (String line : lines) {
            String[] video = line.split(",", 2);
            if (video.length == 1) continue;

            System.out.println(video[0]);
            //System.out.print(line + " /"+video.length + "\n");
        }
    }

    private void zjistitNazev() {
        List<String> prikaz = new ArrayList<>(Arrays.asList("youtube-dl", "-e", url));
        lines = provedPrikaz(prikaz);
        if (lines.size() > 0) {
            ytVideo.setName(lines.get(0));
        }

    }

    /**
     * Provede se jakýkoliv příkaz
     * @param prikaz příkaz
     * @return výstup
     */
    private List<String> provedPrikaz(List<String> prikaz) {
        ProcessBuilder pb = new ProcessBuilder(prikaz);
        BufferedReader reader = null;
        BufferedReader error = null;

        Process process = null;
        try {
            process = pb.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line = null;

            lines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                lines.add(line);
            }

            errors = new ArrayList<>();
            while ((line = error.readLine()) != null) {
                errors.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (error != null) {
                    error.close();
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        try {
            if (process != null) {
                process.waitFor();
                process.destroy();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
