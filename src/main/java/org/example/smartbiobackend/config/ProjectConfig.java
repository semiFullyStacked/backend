package org.example.smartbiobackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .usersByUsernameQuery("select email, password, enabled from users where email=?")
                .authoritiesByUsernameQuery(
                        "SELECT u.email, r.role_name " +
                                "FROM users u " +
                                "JOIN user_roles ur ON u.id = ur.user_id " +
                                "JOIN role r ON ur.role_id = r.id " +
                                "WHERE u.email = ?"
                )
        ;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth ->
                        auth.requestMatchers("/admin/**").hasAuthority("Admin")
                                .requestMatchers("/api/auth/me").permitAll()
                                .requestMatchers("/api/reservation/reserve/**").permitAll()
                                .requestMatchers("/login.html", "/css/**", "/js/**", "/error/**").permitAll()
                                .requestMatchers("/api/employees/**").hasAuthority("Employee")
                                .requestMatchers("/api/csrf-init").permitAll()
                                .requestMatchers("/api/movies/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/api/showings/**").permitAll()
                                .requestMatchers("/api/bookings/**").permitAll()
                                .requestMatchers("/api/tickettypes/**").permitAll()
                                .requestMatchers("/api/auditoriums/**").hasAuthority("Employee")
                                .requestMatchers("/api/payments/**").permitAll()
                                .anyRequest().authenticated()

                ).csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
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
