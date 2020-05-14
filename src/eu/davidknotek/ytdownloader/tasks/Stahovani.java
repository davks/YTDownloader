package eu.davidknotek.ytdownloader.tasks;

import eu.davidknotek.ytdownloader.typy.Fronta;
import eu.davidknotek.ytdownloader.typy.VideoKeStazeni;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stahovani extends Task<String> {

    private static final String YOUTUBEDL = "youtube-dl";

    private String outputNazev;
    private final Fronta fronta;

    public Stahovani(Fronta fronta) {
        this.fronta = fronta;
    }

    /**
     * Ve vlastnim vlaknu se stahnou audio a video stopy, ktere se nasledne spoji.
     * Audio a video stopy se odstrani.
     *
     * @return ???
     * @throws Exception vyjimka
     */
    @Override
    protected String call() throws Exception {
        ObservableList<VideoKeStazeni> seznamVideiKeStazeni = fronta.getSeznamVideiKeStazeni();
        vynulovatDone(seznamVideiKeStazeni);
        boolean bylaChyba = false;

        for (int i = 0; i < seznamVideiKeStazeni.size(); i++) {
            VideoKeStazeni videoKeStazeni = seznamVideiKeStazeni.get(i);
            boolean jeStahnutaVideoStopa = false;
            boolean jeStahnutaAudioStopa = false;
            String videoStopaNazev = "";
            String audioStopaNazev = "";

            // Stahneme video stopu
            if ((videoKeStazeni.getVideoCode() != null) && (!videoKeStazeni.getVideoCode().equals(""))) {
                updateMessage("Stahuji video: " + videoKeStazeni.getVideoName());
                videoStopaNazev = prejmenovatDocasnySoubor("video", videoKeStazeni.getVideoName(), videoKeStazeni.getVideoCode(), videoKeStazeni.getExtensionVideo());
                stahnout(YOUTUBEDL, "-f", videoKeStazeni.getVideoCode(), videoKeStazeni.getUrl(), "-o", videoStopaNazev);
                jeStahnutaVideoStopa = true;
            }

            // Stahneme audio stopu pokud existuje
            if ((videoKeStazeni.getAudioCode() != null) && (!videoKeStazeni.getAudioCode().equals(""))) {
                updateMessage("Stahuji audio: " + videoKeStazeni.getVideoName());
                audioStopaNazev = prejmenovatDocasnySoubor("audio", videoKeStazeni.getVideoName(), videoKeStazeni.getAudioCode(), videoKeStazeni.getExtensionAudio());
                stahnout(YOUTUBEDL, "-f", videoKeStazeni.getAudioCode(), videoKeStazeni.getUrl(), "-o", audioStopaNazev);
                jeStahnutaAudioStopa = true;
            }

            // Spojime video a audio stopu pokud jsme je stahli
            if (jeStahnutaVideoStopa && jeStahnutaAudioStopa) {
                if (Files.exists(Path.of(videoStopaNazev)) && Files.exists(Path.of(audioStopaNazev))) {
                    updateMessage("Spojuji video a audio stopu...");
                    spojitStopy(prikazKeSpojeni(videoKeStazeni, videoStopaNazev, audioStopaNazev));
                    Files.deleteIfExists(Path.of(audioStopaNazev));
                    Files.deleteIfExists(Path.of(videoStopaNazev));
                }

                String done = "✔";
                if (!Files.exists(Path.of(outputNazev))) {
                    bylaChyba = true;
                    done = "×";
                }

                int finalI = i;
                String finalDone = done;
                Platform.runLater(() -> {
                    videoKeStazeni.setDone(finalDone);
                    seznamVideiKeStazeni.set(finalI, videoKeStazeni);
                });
            }

            // Je stahnuta pouze video stopa - prejmenujeme video
            if (jeStahnutaVideoStopa && !jeStahnutaAudioStopa) {
                if (Files.exists(Path.of(videoStopaNazev))) {
                    String novyNazevSouboru = prejmenovatOutputNazev(videoKeStazeni, videoKeStazeni.getExtensionVideo());
                    Files.deleteIfExists(Path.of(novyNazevSouboru));
                    Files.move(Path.of(videoStopaNazev), Path.of(novyNazevSouboru));

                    int finalI = i;
                    Platform.runLater(() -> {
                        videoKeStazeni.setDone("✔");
                        seznamVideiKeStazeni.set(finalI, videoKeStazeni);
                    });
                }
            }
        }
        return bylaChyba ? "U některé úlohy se vyskytl problém." : "Vše proběhlo bez chyb.";
    }

    /**
     * Spusti se prikaz ke spojeni video a audio stopy (ffmpeg).
     *
     * @param prikaz prikaz
     * @throws IOException          vyjimka
     * @throws InterruptedException vyjimka
     */
    private void spojitStopy(String... prikaz) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(prikaz);
        Process process = pb.start();

        process.waitFor();
        process.destroy();
    }

    /**
     * Budeme stahovat YT soubor. Vystup youtube-dl se analyzuje a v programu se zobrazi ruzne informace
     * jako ukazatel stahovani, zbyvajici doba atd...
     *
     * @param prikaz prikaz
     * @throws IOException vyjimka
     */
    private void stahnout(String... prikaz) throws IOException, InterruptedException {
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
            process.waitFor();
            process.destroy();
        }
    }

    /**
     * Analyzuje radek vystupu programu
     *
     * @param line analyzovany radek
     */
    private void analyzujRadek(String line) {
        Pattern pProcenta = Pattern.compile(" ([0-9]{1,3}\\.[0-9])% ");
//        Pattern pRychlostStahovani = Pattern.compile("([0-9]{1,4}\\.[0-9]{1,2}(KiB|MiB|GiB)/s)");
//        Pattern pZbyvajiciCas = Pattern.compile("([0-9]{2}:[0-9]{2})");

        Matcher matcher = pProcenta.matcher(line);
        if (matcher.find()) {
            updateProgress(Double.parseDouble(matcher.group(1)), 100);
        }

//        matcher = pRychlostStahovani.matcher(line);
//        if (matcher.find()) {
//            updateMessage(matcher.group(1));
//        }
    }

    /**
     * Stahuji se audio a video stopy, coz jsou jen docasne soubory, ktere se pozdeji spoji ve vysledne video.
     * Tyhled docasne stazene soubory se potom vymazou. Tato metoda jen upravi nazev takove stopy.
     *
     * @param predpona  text pred nazvem
     * @param nazev     nazev videa
     * @param code      format code
     * @param extension koncovka souboru
     * @return vrati novy nazev
     */
    private String prejmenovatDocasnySoubor(String predpona, String nazev, String code, String extension) {
        String upravenyNazev = predpona + "-";
        upravenyNazev += nazev.trim().toLowerCase()
                .replace(" ", "")
                .replace("/", "");
        upravenyNazev += "-" + code;
        upravenyNazev += "." + extension;

        return upravenyNazev;
    }

    /**
     * Vytvori prikaz, ktery spoji video a audio stopu podle kriterii. Pracuje se se soubory: webm, mp4, m4a.
     * Ostatni formaty jsou ignorovany a v programu se vubec nezobrazi.
     * Obecne se typ vystupniho souboru urci dle audio stopy, protoze ffmpeg takove stopy snadneji slouci,
     * aniz by musel delat nejakou casove narocnou konverzi.
     * video.webm a audio.webm -> output.webm
     * video.webm a audio.m4a -> outpu.mp4
     * video.mp4 a audio.m4a -> output.mp4
     * video.mp4 a audio.webm -> output.mp4
     *
     * @param videoKeStazeni  video, ktere se bude stahovat
     * @param videoStopaNazev nazev video stopy
     * @param audioStopaNazev nazev audio stopy
     * @return vygenerovany prikaz ffmpeg
     */
    private String[] prikazKeSpojeni(VideoKeStazeni videoKeStazeni, String videoStopaNazev, String audioStopaNazev) {
        outputNazev = prejmenovatOutputNazev(videoKeStazeni);
        String[] prikaz = new String[]{"ffmpeg", "-y", "-i", videoStopaNazev, "-i", audioStopaNazev, "-c:v", "copy", "-c:a", "copy", outputNazev};

        // Pokud je video.mp4 a audio.webm vytvorime output.mp4
        if (videoKeStazeni.getExtensionVideo().equals("mp4") && videoKeStazeni.getExtensionAudio().equals("webm")) {
            outputNazev = prejmenovatOutputNazev(videoKeStazeni, "mp4");
            prikaz = new String[]{"ffmpeg", "-y", "-i", videoStopaNazev, "-i", audioStopaNazev, "-strict", "-2", "-c:v", "copy", "-c:a", "copy", outputNazev};
        }
        return prikaz;
    }

    /**
     * Upravi nazev vystupniho souboru.
     *
     * @param videoKeStazeni video, ktere se bude stahovat
     * @return upraveny nazev vystupniho souboru
     */
    private String prejmenovatOutputNazev(VideoKeStazeni videoKeStazeni) {
        return videoKeStazeni.getVideoName().trim().replace("/", "-") +
                " " + videoKeStazeni.getResolution() +
                "." + videoKeStazeni.getExtensionAudio().replace("m4a", "mp4");
    }

    private String prejmenovatOutputNazev(VideoKeStazeni videoKeStazeni, String extension) {
        return videoKeStazeni.getVideoName().trim().replace("/", "-") +
                " " + videoKeStazeni.getResolution() +
                "." + extension;
    }

    /**
     * Pri znovuspusteni se vynuluje u položek v listview done.
     */
    private void vynulovatDone(ObservableList<VideoKeStazeni> seznamVideiKeStazeni) {
        for (int i = 0; i < seznamVideiKeStazeni.size(); i++) {
            VideoKeStazeni videoKeStazeni = seznamVideiKeStazeni.get(i);
            int finalI = i;
            Platform.runLater(() -> {
                videoKeStazeni.setDone("");
                seznamVideiKeStazeni.set(finalI, videoKeStazeni);
            });
        }
    }
}
