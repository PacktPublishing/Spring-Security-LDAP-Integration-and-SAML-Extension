package com.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

/**
 * @author ankidaemon
 *
 */
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "com.demo.config")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final Logger lOGGER = LoggerFactory.getLogger(SecurityConfig.class);

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {	
		
		auth
		.ldapAuthentication()
		.contextSource().url("ldap://localhost:389/dc=packt,dc=COM")
			.managerDn("Jack.Reacher")
			.managerPassword("okmpl@2017")
		.and()
		.userDnPatterns("cn={0},OU=finance,O=packtpublishing")
		.groupSearchFilter("member=cn={0},ou=groups,O=packtpublishing"); 
		
		//auth.authenticationProvider(authProvider());
	}

	ActiveDirectoryLdapAuthenticationProvider authProvider(){			
		ActiveDirectoryLdapAuthenticationProvider provider=
				new ActiveDirectoryLdapAuthenticationProvider ("<domain>","ldap://<domain>/dc=packt,dc=COM");
		provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);
		return provider;
	}
			
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
		.regexMatchers("/chief/.*").hasRole("ADMIN")
		.regexMatchers("/agent/.*").access("hasRole('USER')")
		.anyRequest()
				.authenticated()
				.and().httpBasic()	
				.and().requiresChannel().anyRequest().requiresInsecure();

		http.formLogin().loginPage("/login").permitAll();
		http.logout().logoutSuccessUrl("/");
		http.exceptionHandling().accessDeniedPage("/accessDenied");
		
	}

}
