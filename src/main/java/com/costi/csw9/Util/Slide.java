package com.costi.csw9.Util;

public class Slide {
    private String mobilePath, desktopPath, captionHeader, captionDetails;

    public Slide(String mobilePath, String desktopPath, String captionHeader, String captionDetails) {
        this.mobilePath = mobilePath;
        this.desktopPath = desktopPath;
        this.captionHeader = captionHeader;
        this.captionDetails = captionDetails;
    }

    public Slide(String desktopPath) {
        this.mobilePath = "";
        this.desktopPath = desktopPath;
        this.captionHeader = "";
        this.captionDetails = "";
    }

    public String getMobilePath() {
        return mobilePath;
    }

    public void setMobilePath(String mobilePath) {
        this.mobilePath = mobilePath;
    }

    public String getDesktopPath() {
        return desktopPath;
    }

    public void setDesktopPath(String desktopPath) {
        this.desktopPath = desktopPath;
    }

    public String getCaptionHeader() {
        return captionHeader;
    }

    public void setCaptionHeader(String captionHeader) {
        this.captionHeader = captionHeader;
    }

    public String getCaptionDetails() {
        return captionDetails;
    }

    public void setCaptionDetails(String captionDetails) {
        this.captionDetails = captionDetails;
    }
}
