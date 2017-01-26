package br.com.paulo.hotchat.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.paulo.hotchat.service.LoginHandlerService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final DataSource dataSource;
	private final LoginHandlerService loginHandlerService;
	
    public SecurityConfig(DataSource dataSource, LoginHandlerService loginHandlerService) {
		this.dataSource = dataSource;
		this.loginHandlerService = loginHandlerService;
	}
    
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

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
             	.logoutSuccessHandler(loginHandlerService)
                .permitAll();
    }

    @Autowired
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.jdbcAuthentication()
    		.dataSource(dataSource)
    		.passwordEncoder(passwordEncoder());
    }
}