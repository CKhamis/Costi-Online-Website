package com.costi.csw9.Model.Ajax;

public class ProjectInfo {
    private String imagePath, subtitle, description, era, title, id, type;
    private boolean discontinued;
    private String[] repositoryLinks;

    public ProjectInfo(String era, String type, String id, String imagePath, String title, boolean discontinued, String subtitle, String description, String[] repositoryLinks) {
        this.imagePath = imagePath;
        this.subtitle = subtitle;
        this.description = description;
        this.era = era;
        this.title = title;
        this.id = id;
        this.repositoryLinks = repositoryLinks;
        this.type = type;
        this.discontinued = discontinued;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(boolean discontinued) {
        this.discontinued = discontinued;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    public String[] getRepositoryLinks() {
        return repositoryLinks;
    }

    public void setRepositoryLinks(String[] repositoryLinks) {
        this.repositoryLinks = repositoryLinks;
    }

    public String getEra() {
        return era;
    }

    public void setEra(String era) {
        this.era = era;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
