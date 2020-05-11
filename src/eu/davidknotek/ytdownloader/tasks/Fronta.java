package eu.davidknotek.ytdownloader.tasks;

import javafx.concurrent.Task;

public class Fronta extends Task<String> {

    @Override
    protected String call() throws Exception {
        return null;
    }

    public void vlozitDoFronty() {
        // TODO vlozi do fronty nove video (na pozici)
    }

    public void odstranitZFronty() {
        // TODO odstrani video z fronty
    }

    public void presunoutNahoru() {
        // TODO presune video o pozici nahoru
    }

    public void presunoutDolu() {
        // TODO presune video o pozici dolu
    }

    public void spustitStahovani() {
        // TODO spusti stahovani videi ve fronte
    }
}
