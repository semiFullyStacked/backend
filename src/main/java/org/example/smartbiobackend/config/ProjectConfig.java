package org.example.smartbiobackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class ProjectConfig {

    @Autowired
    private DataSource dataSource; // Tells it to use the data source url from application.properties

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) {
        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .dataSource(dataSource)
                .usersByUsernameQuery("select name, password, enabled from users where name=?")
                .authoritiesByUsernameQuery(
                        "SELECT u.name, r.role_name " +
                                "FROM users u " +
                                "JOIN user_roles ur ON u.id = ur.user_id " +
                                "JOIN role r ON ur.role_id = r.id " +
                                "WHERE u.name = ?"
                )
        ;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth ->
                        auth.requestMatchers("/admin/**").hasRole("Admin")
                                .requestMatchers("/api/auth/me").permitAll()
                                .requestMatchers("/login.html", "/css/**", "/js/**", "/error/**").permitAll()                              //.requestMatchers("/api/employees/**").hasRole("Employee")
                                .requestMatchers("/api/ employees/**").denyAll()
                                .requestMatchers("/api/auditoriums/").hasRole("Employee")
                                .requestMatchers("/api/payments/**").permitAll()
                                .requestMatchers("/h2-console/**").hasRole("Admin")
                                .anyRequest().authenticated()

                ).csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling.accessDeniedPage("/error/403.html");
                })
                .formLogin(form -> {
                    form.loginProcessingUrl("/login")
                            .defaultSuccessUrl("/", true)
                            .successHandler((req, res, auth) -> res.setStatus(200))
                            .failureHandler((req, res, ex) -> res.setStatus(401))
                            .permitAll();
                }).logout(Customizer.withDefaults());
        // http.csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
