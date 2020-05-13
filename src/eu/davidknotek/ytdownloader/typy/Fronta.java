package eu.davidknotek.ytdownloader.typy;

import javafx.collections.ObservableList;
public class Fronta {

    private final ObservableList<VideoKeStazeni> seznamVideiKeStazeni;

    public Fronta(ObservableList<VideoKeStazeni> seznamVideiKeStazeni) {
        this.seznamVideiKeStazeni = seznamVideiKeStazeni;
    }

    public void vlozitDoFronty(VideoKeStazeni videoKeStazeni) {
        seznamVideiKeStazeni.add(videoKeStazeni);
    }

    public void odstranitZFronty(VideoKeStazeni videoKeStazeni) {
        seznamVideiKeStazeni.remove(videoKeStazeni);
    }

    public int presunoutNahoru(VideoKeStazeni videoKeStazeni) {
        int index = seznamVideiKeStazeni.indexOf(videoKeStazeni);
        if (index > 0) {
            seznamVideiKeStazeni.remove(videoKeStazeni);
            seznamVideiKeStazeni.add(--index, videoKeStazeni);
//            seznamVideiKeStazeni.set(index - 1, videoKeStazeni);
        }
        return index;
    }

    public int presunoutDolu(VideoKeStazeni videoKeStazeni) {
        int index = seznamVideiKeStazeni.indexOf(videoKeStazeni);
        if (index < seznamVideiKeStazeni.size() - 1) {
            seznamVideiKeStazeni.remove(videoKeStazeni);
            seznamVideiKeStazeni.add(++index, videoKeStazeni);
//            seznamVideiKeStazeni.set(index + 1, videoKeStazeni);
        }
        return index;
    }
}
