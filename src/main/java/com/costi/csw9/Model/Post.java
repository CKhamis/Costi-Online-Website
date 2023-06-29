package com.costi.csw9.Model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;
    @Column(nullable = false)
    private LocalDateTime lastEdited;
    @Column(nullable = false)
    private String subtitle;

    @Column(nullable = false)
    private boolean enabled = false;
    private String category;
    @Column(columnDefinition="text")
    private String body;

    private String imagePath;
    @Column(nullable = false)
    private int views;

    @Column(nullable = false)
    private boolean isPinned;

    @Column(nullable = false)
    private boolean isFavorite;

    @Column(nullable = false)
    private boolean isFeatured;

    @Column(nullable = false)
    private boolean isPublic;

    public Post(String title, String subtitle, String category, String body) {
        this.title = title;
        this.subtitle = subtitle;
        this.category = category;
        this.body = body;
        this.views = 0;
        isPinned = false;
        isFavorite = false;
        isFeatured = false;
    }

    public String getDateEdited(){
        return lastEdited.getMonthValue() + "/" + lastEdited.getDayOfMonth() + "/" + lastEdited.getYear();
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Transient
    public String getTimeSinceEdited() {
        //return lastEdited.getMonthValue() + "/" + lastEdited.getDayOfMonth() + "/" + lastEdited.getYear();
        String unit = "";
        LocalDateTime now = LocalDateTime.now();
        long diff;
        if((diff = ChronoUnit.SECONDS.between(lastEdited,now)) < 60){
            unit = "seconds ago";
        } else if ((diff = ChronoUnit.MINUTES.between(lastEdited,now)) < 60) {
            unit = "minutes ago";
        } else if ((diff = ChronoUnit.HOURS.between(lastEdited,now)) < 24) {
            unit = "hours ago";
        } else if ((diff = ChronoUnit.DAYS.between(lastEdited,now)) < 30) {
            unit = "days ago";
        } else if ((diff = ChronoUnit.MONTHS.between(lastEdited,now)) < 12) {
            unit = "month ago";
        } else{
            diff = ChronoUnit.YEARS.between(lastEdited,now);
        }
        return String.format("%d %s",diff,unit);
    }

    @Transient
    public String getTimeSinceEditedShort() {
        //return lastEdited.getMonthValue() + "/" + lastEdited.getDayOfMonth() + "/" + lastEdited.getYear();
        String unit = "";
        LocalDateTime now = LocalDateTime.now();
        long diff;
        if((diff = ChronoUnit.SECONDS.between(lastEdited,now)) < 60){
            unit = "s";
        } else if ((diff = ChronoUnit.MINUTES.between(lastEdited,now)) < 60) {
            unit = "m";
        } else if ((diff = ChronoUnit.HOURS.between(lastEdited,now)) < 24) {
            unit = "h";
        } else if ((diff = ChronoUnit.DAYS.between(lastEdited,now)) < 30) {
            unit = "d";
        } else if ((diff = ChronoUnit.MONTHS.between(lastEdited,now)) < 12) {
            unit = "mo";
        } else{
            diff = ChronoUnit.YEARS.between(lastEdited,now);
        }
        return String.format("%d %s",diff,unit);
    }
}
