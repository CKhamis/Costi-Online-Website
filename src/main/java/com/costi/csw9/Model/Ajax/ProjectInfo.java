package com.costi.csw9.Model.Ajax;

public class ProjectInfo {
    private String imagePath, subtitle, description, era;
    private String[] repositoryLinks;

    public ProjectInfo(String imagePath, String subtitle, String description, String era, String[] repositoryLinks) {
        this.imagePath = imagePath;
        this.subtitle = subtitle;
        this.description = description;
        this.era = era;
        this.repositoryLinks = repositoryLinks;
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
}
