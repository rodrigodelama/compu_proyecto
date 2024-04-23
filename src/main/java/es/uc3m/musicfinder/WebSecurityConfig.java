package es.uc3m.musicfinder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.core.userdetails.UserDetailsService;
import es.uc3m.musicfinder.services.UserDetailsServiceImpl;

/*
 * YOU NEED A TABLE:
    CREATE TABLE `user` (
        `id` INT AUTO_INCREMENT PRIMARY KEY,
        `description` TEXT,
        `email` VARCHAR(255),
        `name` VARCHAR(100),
        `password` VARCHAR(100)
        );

 * Table should autogenerate when filling it in

 * TO CHECK TABLE IS FILLING CORRECTLY:
    SELECT * FROM `user`;
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/login", "/signup", "/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .permitAll());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }
}
