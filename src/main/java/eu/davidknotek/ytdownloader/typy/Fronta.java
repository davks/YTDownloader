package eu.davidknotek.ytdownloader.typy;

import javafx.collections.ObservableList;
public class Fronta {

    private final ObservableList<VideoKeStazeni> seznamVideiKeStazeni;

    public Fronta(ObservableList<VideoKeStazeni> seznamVideiKeStazeni) {
        this.seznamVideiKeStazeni = seznamVideiKeStazeni;
    }

    /**
     * Vlozit do fronty nove video.
     * @param videoKeStazeni vkladane video
     */
    public void vlozitDoFronty(VideoKeStazeni videoKeStazeni) {
        seznamVideiKeStazeni.add(videoKeStazeni);
    }

    /**
     * Odstranit z fronty vybrane video.
     * @param videoKeStazeni odstranovane video
     */
    public void odstranitZFronty(VideoKeStazeni videoKeStazeni) {
        seznamVideiKeStazeni.remove(videoKeStazeni);
    }

    public void odstranitVseZFronty() {
        seznamVideiKeStazeni.clear();
    }

    /**
     * Ve fronte vybrane video presunout o pozici nahoru.
     * @param videoKeStazeni vybrane video k presunuti
     * @return novy index vybraneho videa
     */
    public int presunoutNahoru(VideoKeStazeni videoKeStazeni) {
        int index = seznamVideiKeStazeni.indexOf(videoKeStazeni);
        if (index > 0) {
            presunout(--index, videoKeStazeni);
        }
        return index;
    }

    /**
     * Ve fronte vybrane video presune o pozici dolu.
     * @param videoKeStazeni vybrane video k presunuti
     * @return novy index vybraneho videa
     */
    public int presunoutDolu(VideoKeStazeni videoKeStazeni) {
        int index = seznamVideiKeStazeni.indexOf(videoKeStazeni);
        if (index < seznamVideiKeStazeni.size() - 1) {
            presunout(++index, videoKeStazeni);
        }
        return index;
    }

    /**
     * Ziskame frontu, tedy seznam videi ke stazeni.
     * @return seznam videi ke stazeni
     */
    public ObservableList<VideoKeStazeni> getSeznamVideiKeStazeni() {
        return seznamVideiKeStazeni;
    }

    /**
     * Provede samotny presun videa ve fronte
     * @param index novy index
     * @param videoKeStazeni video k presunuti
     */
    private void presunout(int index, VideoKeStazeni videoKeStazeni) {
        seznamVideiKeStazeni.remove(videoKeStazeni);
        seznamVideiKeStazeni.add(index, videoKeStazeni);
    }
}
