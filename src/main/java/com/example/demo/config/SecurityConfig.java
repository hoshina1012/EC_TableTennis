package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/signUp","/signUp/save","/signUp/seller","/signUp/seller/save", "/login", "/login/save", "/css/**", "/products").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/login/save")
                .usernameParameter("mailAddress")
                .passwordParameter("password")
                .successHandler(customAuthenticationSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
        		.logoutUrl("/logout")
                .logoutSuccessUrl("/") // ログアウト後のリダイレクト先
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(sessionManagement -> sessionManagement
                .invalidSessionUrl("/login?invalid")
                .maximumSessions(1)
                .expiredUrl("/login?expired")
            )
            .rememberMe(rememberMe -> rememberMe
                .tokenValiditySeconds(86400) // 1日（86400秒）
                .key("uniqueAndSecret")
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UsersRepository usersRepository) {
        return username -> {
            Users user = usersRepository.findByMailAddress(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            return User.withUsername(user.getMailAddress())
                    .password(user.getPassword()) // ハッシュ化されたパスワードをそのまま使用
                    .roles("USER")
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCryptPasswordEncoderを使用してパスワードをハッシュ化
    }
}
