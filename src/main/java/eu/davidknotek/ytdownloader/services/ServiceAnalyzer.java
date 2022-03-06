package eu.davidknotek.ytdownloader.services;

import eu.davidknotek.ytdownloader.typy.FormatVidea;
import eu.davidknotek.ytdownloader.tasks.VideoAnalyzer;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;

public class ServiceAnalyzer extends Service<ObservableList<FormatVidea>> {

    private String url;
    private String ytDownloadTool;

    public ServiceAnalyzer() {
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setYtDownloadTool(String ytDownloadTool) {
        this.ytDownloadTool = ytDownloadTool;
    }

    @Override
    protected Task<ObservableList<FormatVidea>> createTask() {
        VideoAnalyzer videoAnalyzer = new VideoAnalyzer(url, ytDownloadTool);
        return videoAnalyzer;
    }
}
