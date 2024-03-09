package com.costi.csw9.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@org.springframework.data.relational.core.mapping.Table
public class User implements UserDetails {

   @Id
   @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "user_sequence")
   private Long id;
   @Size(max = 20, message = "First name can only be 20 characters or less")
   String firstName;
   @Size(max = 20, message = "Last name can only be 20 characters or less")
   String lastName;
   @Column(nullable = false, unique = true)
   String email;
   @NotNull
   @JsonIgnore
   String password;

   private LocalDateTime dateCreated = LocalDateTime.now();

   @Column(nullable = false)
   private int profilePicture = 0;
   @Enumerated(EnumType.STRING)
   private UserRole role;
   private Boolean isLocked = false;
   private Boolean enabled =  false;

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

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public int getProfilePicture() {
        return profilePicture;
    }

    public String getFormattedDateCreated(){
        return dateCreated.getMonthValue() + "/" + dateCreated.getDayOfMonth() + "/" + dateCreated.getYear();
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " of ID " + id;
    }
}
