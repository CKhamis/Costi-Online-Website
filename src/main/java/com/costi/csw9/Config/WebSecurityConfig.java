package com.costi.csw9.Config;

import com.costi.csw9.Model.UserRole;
import com.costi.csw9.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
//@EnableWebSecurity
@EnableMethodSecurity
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig  {
    private final AuthenticationProvider authenticationProvider;



    @Bean
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        // "/Wiki/**/delete", "/Wiki/**/enable", "/Wiki/**/disable"
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/Upload", "/COMT/**", "/Accounts/**").hasAnyAuthority(UserRole.ADMIN.toString(), UserRole.OWNER.toString())
                        .requestMatchers("/Account", "/Wiki/Create").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(form -> form
                        .logoutUrl("/logout")
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringRequestMatchers("/games/**")
                );

        http.authenticationProvider(authenticationProvider);

        return http.build();
    }
}
