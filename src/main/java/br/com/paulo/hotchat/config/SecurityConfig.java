package br.com.paulo.hotchat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
	            .antMatchers("/webjars/**").permitAll()
	            .antMatchers("/h2-console/**").permitAll()
	            .antMatchers("/cadastro/**").permitAll()
            	.anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
             .httpBasic()
             	.and()
             .csrf().ignoringAntMatchers("/api/**", "/h2-console/**")
             	.and()
             .headers().frameOptions().disable()
             	.and()
             .logout()
                .permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
    	web
    		.ignoring()
    		.antMatchers("/wejbars/**");
    }
      
    @Autowired
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            	.withUser("paulo").password("senha").roles("ADMIN");
    }
}