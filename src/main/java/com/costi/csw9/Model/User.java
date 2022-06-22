package com.costi.csw9.Model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
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
   String firstName;
   String lastName;
   String email;
   String password;
   @Enumerated(EnumType.STRING)
   private UserRole role;
   private Boolean isLocked = false;
   private Boolean enabled =  false;

   @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
   private List<WikiPage> authoredPages = new ArrayList<>();

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
}
