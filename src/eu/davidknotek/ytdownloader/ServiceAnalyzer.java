package eu.davidknotek.ytdownloader;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

public class ServiceAnalyzer extends Service<String> {

    private Label lblNazev;
    private VideoAnalyzer videoAnalyzer;
    private String url;

    public ServiceAnalyzer(Label lblNazev) {
        this.lblNazev = lblNazev;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        lblNazev.textProperty().unbind();
        lblNazev.setText(videoAnalyzer.getYtVideo().getName());
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        lblNazev.textProperty().unbind();
    }

    @Override
    protected Task<String> createTask() {
        videoAnalyzer = new VideoAnalyzer(url);
        return videoAnalyzer;
    }
}
