package com.costi.csw9.Model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@NoArgsConstructor
public class Light {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false, unique = true)
    private String label;
    @Column(nullable = false)
    private LocalDateTime dateAdded;
    @Column(nullable = false)
    private LocalDateTime lastModified;
    private LocalDateTime lastConnected;
    @Column(nullable = false)
    private String color;
    @Column(nullable = false)
    private String pattern;
    @Column(nullable = false)
    private boolean isFavorite;
    @Column(nullable = false)
    private boolean isPublic;
    @Column(nullable = false)
    private boolean isEnabled;
    @OneToMany(mappedBy = "light", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LightLog> logs = new ArrayList<>();

    public Light(String label, LocalDateTime dateAdded, LocalDateTime lastModified, String color, String pattern) {
        this.label = label;
        this.dateAdded = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.color = color;
        this.pattern = pattern;
        this.isEnabled = false;
        this.isFavorite = false;
        this.isPublic = false;
    }

    public String getlastModified(){
        return lastModified.getMonthValue() + "/" + lastModified.getDayOfMonth() + "/" + lastModified.getYear();
    }

    @Transient
    public String getTimeSinceModified() {
        //return lastEdited.getMonthValue() + "/" + lastEdited.getDayOfMonth() + "/" + lastEdited.getYear();
        String unit = "";
        LocalDateTime now = LocalDateTime.now();
        long diff;
        if((diff = ChronoUnit.SECONDS.between(lastModified,now)) < 60){
            unit = "seconds ago";
        } else if ((diff = ChronoUnit.MINUTES.between(lastModified,now)) < 60) {
            unit = "minutes ago";
        } else if ((diff = ChronoUnit.HOURS.between(lastModified,now)) < 24) {
            unit = "hours ago";
        } else if ((diff = ChronoUnit.DAYS.between(lastModified,now)) < 30) {
            unit = "days ago";
        } else if ((diff = ChronoUnit.MONTHS.between(lastModified,now)) < 12) {
            unit = "month ago";
        } else{
            diff = ChronoUnit.YEARS.between(lastModified,now);
        }
        return String.format("%d %s",diff,unit);
    }

    @Transient
    public String getTimeSinceModifiedShort() {
        //return lastEdited.getMonthValue() + "/" + lastEdited.getDayOfMonth() + "/" + lastEdited.getYear();
        String unit = "";
        LocalDateTime now = LocalDateTime.now();
        long diff;
        if((diff = ChronoUnit.SECONDS.between(lastModified,now)) < 60){
            unit = "s";
        } else if ((diff = ChronoUnit.MINUTES.between(lastModified,now)) < 60) {
            unit = "m";
        } else if ((diff = ChronoUnit.HOURS.between(lastModified,now)) < 24) {
            unit = "h";
        } else if ((diff = ChronoUnit.DAYS.between(lastModified,now)) < 30) {
            unit = "d";
        } else if ((diff = ChronoUnit.MONTHS.between(lastModified,now)) < 12) {
            unit = "mo";
        } else{
            diff = ChronoUnit.YEARS.between(lastModified,now);
        }
        return String.format("%d %s",diff,unit);
    }
}
