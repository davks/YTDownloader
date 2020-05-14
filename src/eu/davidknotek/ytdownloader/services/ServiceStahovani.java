package eu.davidknotek.ytdownloader.services;

import eu.davidknotek.ytdownloader.tasks.Stahovani;
import eu.davidknotek.ytdownloader.typy.Fronta;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceStahovani extends Service<String> {

    private Fronta fronta;

    public void setFronta(Fronta fronta) {
        this.fronta = fronta;
    }

    @Override
    protected Task<String> createTask() {
        return new Stahovani(fronta);
    }
}
