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
    private String title;
    private LocalDate publishDate;
    private String email;

    public Article(Long id, String bannerImage, String title, LocalDate publishDate, String email) {
        this.id = id;
        this.bannerImage = bannerImage;
        this.title = title;
        this.publishDate = publishDate;
        this.email = email;
    }

    public Article() {
    }

    public Article(String bannerImage, String title, LocalDate publishDate, String email) {
        this.bannerImage = bannerImage;
        this.title = title;
        this.publishDate = publishDate;
        this.email = email;
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
}
