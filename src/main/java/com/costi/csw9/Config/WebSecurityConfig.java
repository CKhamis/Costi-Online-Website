package com.costi.csw9.Config;

import com.costi.csw9.Model.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
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
                        .invalidateHttpSession(true)
                        .deleteCookies("COSID")
                        .logoutUrl("/logout")
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringRequestMatchers("/games/**")
                );

        http.authenticationProvider(authenticationProvider);

        return http.build();
    }
}
