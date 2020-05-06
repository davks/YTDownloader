package eu.davidknotek.ytdownloader;

public class YTVideo {

    private String name;
    private int formatCode;
    private String extension;
    private String resolution;
    private VideoList.Typ typ;
    private int fileSize;

    public YTVideo() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getFormatCode() {
        return formatCode;
    }

    public void setFormatCode(int formatCode) {
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

    public VideoList.Typ getTyp() {
        return typ;
    }

    public void setTyp(VideoList.Typ typ) {
        this.typ = typ;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
}
