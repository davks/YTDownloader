package eu.davidknotek.ytdownloader.typy;

import eu.davidknotek.ytdownloader.enums.TypVidea;

public class FormatVidea {

    private String formatCode;
    private String extension;
    private String resolution;
    private String fps;
    private String fileSize;
    private String audioQuality;
    private TypVidea typVidea;


    public FormatVidea() {
    }

    public String getAudioQuality() {
        return audioQuality;
    }

    public void setAudioQuality(String audioQuality) {
        this.audioQuality = audioQuality;
    }

    public String getFps() {
        return fps;
    }

    public void setFps(String fps) {
        this.fps = fps;
    }

    public String getFormatCode() {
        return formatCode;
    }

    public void setFormatCode(String formatCode) {
        this.formatCode = formatCode;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public TypVidea getTypVidea() {
        return typVidea;
    }

    public void setTypVidea(TypVidea typVidea) {
        this.typVidea = typVidea;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
