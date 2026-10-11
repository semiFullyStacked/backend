package org.example.smartbiobackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class ProjectConfig {

    @Autowired
    private DataSource dataSource; // Tells it to use the data source url from application.properties
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/errors/").permitAll()
                        //  .requestMatchers("/api/employees/**").hasRole("Employee")
                        .requestMatchers("/api/employees/**").denyAll()
                        .requestMatchers("/api/auditoriums/").hasRole("Employee")
                        .requestMatchers("/api/payments/**").permitAll()
                        .requestMatchers("/h2-console/**").hasRole("Manager")
                        .anyRequest().authenticated()
        ).exceptionHandling((exceptionHandling ) -> {
                  exceptionHandling.accessDeniedPage("/error/403.html");
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {
                 //   httpSecurityFormLoginConfigurer.loginPage("/api/login").isCustomLoginPage();
                }).logout(Customizer.withDefaults());
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users() {
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}user")
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}admin")
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}
