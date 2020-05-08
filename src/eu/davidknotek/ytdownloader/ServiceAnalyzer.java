package eu.davidknotek.ytdownloader;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

public class ServiceAnalyzer extends Service<String> {

    private VideoAnalyzer videoAnalyzer;
    private String url;

    public ServiceAnalyzer(Label lblNazev) {
//        videoAnalyzer = new VideoAnalyzer();

        setOnSucceeded(workerStateEvent -> {
            lblNazev.textProperty().unbind();
            lblNazev.setText(videoAnalyzer.getYtVideo().getName());
        });

        setOnCancelled(workerStateEvent -> {
            lblNazev.textProperty().unbind();
        });
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected Task<String> createTask() {
        videoAnalyzer = new VideoAnalyzer(url);
        return videoAnalyzer;
    }
}
