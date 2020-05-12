package eu.davidknotek.ytdownloader.typy;

public class VideoKeStazeni {

    private String videoName;
    private String videoCode;
    private String audioCode;
    private String extension;
    private String resolution;

    public VideoKeStazeni() {
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

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
