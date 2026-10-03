package ru.checkdev.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig {

    /**
     * Конфиг для Resource Server (OAuth2).
     * Здесь мы говорим: "/actuator/**" не требует OAuth2-токена.
     */
    @Configuration
    @EnableResourceServer
    public static class ResourceServerConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/actuator/**").permitAll()  // Actuator — без токена
                    .antMatchers(
                            "/template/queue",
                            "/template/ping",
                            "/subscribeCategory/**",
                            "/subscribeTopic/**",
                            "/swagger-ui/**",
                            "/v3/**"
                    ).permitAll()
                    .anyRequest().authenticated();
        }
    }

    /**
     * Обычный Web Security.
     * Добавляем @EnableWebSecurity для @PreAuthorize и т.д.
     */
    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web) {
            web.ignoring().antMatchers(
                    "/template/queue",
                    "/template/ping",
                    "/subscribeCategory/**",
                    "/subscribeTopic/**",
                    "/swagger-ui/**",
                    "/v3/**"
            );
        }
    }
}