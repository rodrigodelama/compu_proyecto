package es.uc3m.musicfinder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Password encoder for security
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// UserDetailsService 
import org.springframework.security.core.userdetails.UserDetailsService;
import es.uc3m.musicfinder.services.UserDetailsServiceImpl;

/*
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
            //autorization rules
            .authorizeHttpRequests((authorize) -> authorize
            .requestMatchers("/**", "/index", "/login", "/signup", "/public/**", "/static/**", "/images/**", "/error", "/event/**").permitAll()
            // .requestMatchers("/user/**").hasRole("USER")
            .requestMatchers("/user/**", "/admin_panel/**", "/data_dashboard/**").hasRole("ADMIN")
            // Rompe nuestro código
            // .requestMatchers("/user/**", "/create_event/**").hasRole("ORGANIZER") // añadir para organizador solo el de create event ? 
                .anyRequest().authenticated()
            )
            //login page, especifies the loging page, all requests are permitted to acces it
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .permitAll());
            
        return http.build(); //called to build and return the OBjectsecutiry object
    }

    // for encoiding the password
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //handling user authentication and spring application
    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

}
