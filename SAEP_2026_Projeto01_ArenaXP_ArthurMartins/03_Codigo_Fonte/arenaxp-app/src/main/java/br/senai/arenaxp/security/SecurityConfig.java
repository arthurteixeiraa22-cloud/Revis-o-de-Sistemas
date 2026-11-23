package br.senai.arenaxp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/error").permitAll()
                .anyRequest().authenticated())      // RNF03: rotas internas exigem login
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("senha")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?erro")
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")               // POST + CSRF
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .permitAll());
        return http.build();
    }

    /** Senhas armazenadas com hash BCrypt (RNF03). */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
