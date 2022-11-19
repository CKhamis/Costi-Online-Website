package com.costi.csw9.Model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Events")
public class FriendEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Page details
    @Lob
    private byte[] image;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition="text")
    private String body;

    private String category;

    private LocalDateTime dateCreated = LocalDateTime.now();

    private LocalDateTime start;

    private LocalDateTime end;

    private String location;

    // Visibility
    @Column(nullable = false)
    private boolean enabled = false;

    @Column(nullable = false)
    private boolean isUniversal = false;

    // Invited & Organizer
    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @ManyToMany(mappedBy = "eventsInvited")
    private List<User> invited = new ArrayList<>();

    public FriendEvent() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FriendEvent(String title, boolean enabled, boolean isUniversal, User organizer) {
        this.title = title;
        this.enabled = enabled;
        this.isUniversal = isUniversal;
        this.organizer = organizer;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUniversal() {
        return isUniversal;
    }

    public void setUniversal(boolean universal) {
        isUniversal = universal;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public List<User> getInvited() {
        return invited;
    }

    public void setInvited(List<User> invited) {
        this.invited = invited;
    }
}
