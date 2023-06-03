package com.costi.csw9.Model.Ajax;

public class MediaInfo {
    private String era, id, type, previewHTML, title, subtitle, description, link;

    public MediaInfo(String era, String id, String type, String previewHTML, String title, String subtitle, String description, String link) {
        this.era = era;
        this.id = id;
        this.type = type;
        this.previewHTML = previewHTML;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.link = link;
    }

    public String getEra() {
        return era;
    }

    public void setEra(String era) {
        this.era = era;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPreviewHTML() {
        return previewHTML;
    }

    public void setPreviewHTML(String previewHTML) {
        this.previewHTML = previewHTML;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
