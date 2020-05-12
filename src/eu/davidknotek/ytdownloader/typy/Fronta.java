package eu.davidknotek.ytdownloader.typy;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class Fronta {

    private final ObservableList<VideoKeStazeni> seznamVideiKeStazeni;

    public Fronta(ObservableList<VideoKeStazeni> seznamVideiKeStazeni) {
        this.seznamVideiKeStazeni = seznamVideiKeStazeni;
    }

    public Fronta(ObservableList<VideoKeStazeni> seznamVideiKeStazeni, VideoKeStazeni... videoKeStazeni) {
        this.seznamVideiKeStazeni = seznamVideiKeStazeni;
        this.seznamVideiKeStazeni.addAll(Arrays.asList(videoKeStazeni));
//        for (int i = 0; i < videoKeStazeni.length; i++) {
//            seznamVideiKeStazeni.add(videoKeStazeni[i]);
//        }
    }

    public void vlozitDoFronty(VideoKeStazeni videoKeStazeni) {
        seznamVideiKeStazeni.add(videoKeStazeni);
    }

    public void odstranitZFronty(VideoKeStazeni videoKeStazeni) {
        seznamVideiKeStazeni.remove(videoKeStazeni);
    }

    public void presunoutNahoru(VideoKeStazeni videoKeStazeni) {
        int index = seznamVideiKeStazeni.indexOf(videoKeStazeni);
        if (index > 0) {
            seznamVideiKeStazeni.set(index - 1, videoKeStazeni);
        }
    }

    public void presunoutDolu(VideoKeStazeni videoKeStazeni) {
        int index = seznamVideiKeStazeni.indexOf(videoKeStazeni);
        if (index < seznamVideiKeStazeni.size()) {
            seznamVideiKeStazeni.set(index + 1, videoKeStazeni);
        }
    }
}
