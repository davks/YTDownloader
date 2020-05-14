package eu.davidknotek.ytdownloader.typy;

public class VideoKeStazeni {

    private String videoName;
    private String videoCode;
    private String audioCode;
    private String extensionVideo;
    private String extensionAudio;
    private String resolution;
    private String url;
    private String fps;
    private String done;

    public VideoKeStazeni() {
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public String getFps() {
        return fps;
    }

    public void setFps(String fps) {
        this.fps = fps;
    }

    public String getExtensionAudio() {
        return extensionAudio;
    }

    public void setExtensionAudio(String extensionAudio) {
        this.extensionAudio = extensionAudio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoCode() {
        return videoCode;
    }

    public void setVideoCode(String videoCode) {
        this.videoCode = videoCode;
    }

    public String getAudioCode() {
        return audioCode;
    }

    public void setAudioCode(String audioCode) {
        this.audioCode = audioCode;
    }

    public String getExtensionVideo() {
        return extensionVideo;
    }

    public void setExtensionVideo(String extensionVideo) {
        this.extensionVideo = extensionVideo;
    }
}
