package net.datatp.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().withUser("admin").password("admin").roles("user", "admin");
    auth.inMemoryAuthentication().withUser("datatp").password("datatp").roles("user");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();
    http.authorizeRequests().
      antMatchers("/css/**", "/js/**").permitAll(). 
      antMatchers("/**").access("hasRole('admin') or hasRole('user')"). //and().formLogin().permitAll();
      and().
      formLogin().loginProcessingUrl("/login").loginPage("/login.jsp").failureUrl("/login-error.jsp").permitAll().
      and().
      logout().logoutUrl("/logout.jsp").logoutSuccessUrl("/").permitAll();
  }
}