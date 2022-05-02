package com.costi.csw9.Model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bannerImage;
    private String title, description, content;
    private LocalDate publishDate;
    private String email;
    private boolean enabled;

    public Article(String bannerImage, String title, String description, String content, LocalDate publishDate, String email) {
        this.bannerImage = bannerImage;
        this.title = title;
        this.description = description;
        this.content = content;
        this.publishDate = publishDate;
        this.email = email;
        this.enabled = true;
    }

    public Article() {
        this.enabled = true;
    }

    @Transient
    public String getPhotosImagePath() {
        if (bannerImage == null || id == null) return null;
        String path = "/images/articleBanners/" + bannerImage; //+ id + "/"
        return path;
    }

    @Transient
    public String getArticleLink() {
        String path = "/Articles" + id + "/";
        return path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + title + '\'' +
                ", birthday=" + publishDate +
                ", email='" + email + '\'' +
                '}';
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setBannerImage(String fileName) {
        bannerImage = fileName;
    }
}
