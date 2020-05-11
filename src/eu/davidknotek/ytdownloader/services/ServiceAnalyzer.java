package eu.davidknotek.ytdownloader.services;

import eu.davidknotek.ytdownloader.typy.FormatVidea;
import eu.davidknotek.ytdownloader.tasks.VideoAnalyzer;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;

public class ServiceAnalyzer extends Service<List<FormatVidea>> {

    private String url;

    public ServiceAnalyzer() {
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected Task<List<FormatVidea>> createTask() {
        VideoAnalyzer videoAnalyzer = new VideoAnalyzer(url);
        return videoAnalyzer;
    }
}
