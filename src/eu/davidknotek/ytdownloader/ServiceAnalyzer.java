package eu.davidknotek.ytdownloader;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ServiceAnalyzer extends Service<String> {

    private final Label lblNazevVidea;
    private final Label lblZprava;
    private final ProgressBar pbUkazatel;
    private VideoAnalyzer videoAnalyzer;
    private String url;

    public ServiceAnalyzer(Label lblNazevVidea, Label lblZprava, ProgressBar pbUkazatel) {
        this.lblNazevVidea = lblNazevVidea;
        this.lblZprava = lblZprava;
        this.pbUkazatel = pbUkazatel;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        lblNazevVidea.textProperty().unbind();
        lblZprava.textProperty().unbind();
        pbUkazatel.progressProperty().unbind();
        lblZprava.setText(getValue());
        pbUkazatel.setProgress(0.0);
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        lblNazevVidea.textProperty().unbind();
        lblZprava.textProperty().unbind();
        pbUkazatel.progressProperty().unbind();
        lblZprava.setText("Analýza byla přerušena.");
        pbUkazatel.setProgress(0.0);
    }

    @Override
    protected void failed() {
        super.failed();
        lblNazevVidea.textProperty().unbind();
        lblZprava.textProperty().unbind();
        pbUkazatel.progressProperty().unbind();
        lblZprava.setText("Vyskytl se problém při analýze URL.");
        pbUkazatel.setProgress(0.0);
    }

    @Override
    protected Task<String> createTask() {
        videoAnalyzer = new VideoAnalyzer(url);
        return videoAnalyzer;
    }
}
