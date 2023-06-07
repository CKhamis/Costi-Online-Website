package com.costi.csw9.Model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class User implements UserDetails {

   @Id
   @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "user_sequence")
   private Long id;
   @Size(max = 20, message = "First name can only be 20 characters or less")
   String firstName;
   @Size(max = 20, message = "Last name can only be 20 characters or less")
   String lastName;
   String email;
   String password;

   private LocalDateTime dateCreated = LocalDateTime.now();

   @Column(nullable = false)
   private int profilePicture = 0;
   @Enumerated(EnumType.STRING)
   private UserRole role;
   private Boolean isLocked = false;
   private Boolean enabled =  false;

   @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
   private List<AccountLog> logs = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<AccountNotification> notifications = new ArrayList<>();

    public User(String firstName, String lastName, String email, String password, UserRole role) {
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.lastName = lastName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority= new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; //not keeping track
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAdmin(){
        return this.getRole().equals(UserRole.ADMIN) || this.getRole().equals(UserRole.OWNER);
    }

    public boolean isOwner(){
        return this.getRole().equals(UserRole.OWNER);
    }

    public List<AccountLog> getLogs() {
        return logs;
    }

    public void setLogs(List<AccountLog> logs) {
        this.logs = logs;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(int profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<AccountNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<AccountNotification> notifications) {
        this.notifications = notifications;
    }

    public String getFormattedDateCreated(){
        return dateCreated.getMonthValue() + "/" + dateCreated.getDayOfMonth() + "/" + dateCreated.getYear();
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " of ID " + id;
    }
}
