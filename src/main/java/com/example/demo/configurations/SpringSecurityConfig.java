package com.example.demo.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String USER_ROLE = "USER";
  private static final String ADMIN_ROLE = "ADMIN";

  @Bean
  public UserDetailsService users() {
    // The builder will ensure the passwords are encoded before saving in memory
    UserBuilder users = User.builder();
    UserDetails user = users
      .username("user")
      .password(
        "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"
      ) // password
      .roles(USER_ROLE)
      .build();
    UserDetails admin = users
      .username("admin")
      .password(
        "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"
      ) // password
      .roles(USER_ROLE, ADMIN_ROLE)
      .build();
    return new InMemoryUserDetailsManager(user, admin);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    String booksUrl = "/books/**";
    String authorsUrl = "/authors/**";
    http
      //HTTP Basic authentication
      .httpBasic()
      .and()
      .authorizeRequests()
      .antMatchers(HttpMethod.GET, booksUrl)
      .hasRole(USER_ROLE)
      .antMatchers(HttpMethod.POST, "/books")
      .hasRole(ADMIN_ROLE)
      .antMatchers(HttpMethod.PUT, booksUrl)
      .hasRole(ADMIN_ROLE)
      .antMatchers(HttpMethod.PATCH, booksUrl)
      .hasRole(ADMIN_ROLE)
      .antMatchers(HttpMethod.DELETE, booksUrl)
      .hasRole(ADMIN_ROLE)
      .antMatchers(HttpMethod.GET, authorsUrl)
      .hasRole(USER_ROLE)
      .antMatchers(HttpMethod.POST, "/authors")
      .hasRole(ADMIN_ROLE)
      .antMatchers(HttpMethod.PUT, authorsUrl)
      .hasRole(ADMIN_ROLE)
      .antMatchers(HttpMethod.PATCH, authorsUrl)
      .hasRole(ADMIN_ROLE)
      .antMatchers(HttpMethod.DELETE, authorsUrl)
      .hasRole(ADMIN_ROLE)
      .and()
      .csrf()
      .disable()
      .formLogin()
      .disable();
  }
}
