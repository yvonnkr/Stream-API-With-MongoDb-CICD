package com.yvolabs.streamapi.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.springframework.http.HttpMethod.*;

/**
 * @author Yvonne N
 */
@Configuration
public class SecurityConfiguration {

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;
    private final CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;
    private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;

    // jwt signing keys
    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;


    public SecurityConfiguration(CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint,
                                 CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint,
                                 CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler)
            throws NoSuchAlgorithmException {
        this.customBasicAuthenticationEntryPoint = customBasicAuthenticationEntryPoint;
        this.customBearerTokenAuthenticationEntryPoint = customBearerTokenAuthenticationEntryPoint;
        this.customBearerTokenAccessDeniedHandler = customBearerTokenAccessDeniedHandler;

        // Generate a public/private key pair in java, everytime we restart the project will have a new set of keys
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthService authService) throws Exception {

        return http
                .authorizeHttpRequests((authorizeRequests) -> {
                    authorizeRequests
                            .requestMatchers(GET, baseUrl + "/movies/**").permitAll()
                            .requestMatchers(POST, baseUrl + "/movies/**").hasAuthority("ROLE_user")
                            .requestMatchers(PATCH, baseUrl + "/movies/**").hasAuthority("ROLE_user")
                            .requestMatchers(DELETE, baseUrl + "/movies/**").hasAuthority("ROLE_user")

                            .requestMatchers(HttpMethod.GET, baseUrl + "/users/**").hasAuthority("ROLE_admin")
                            .requestMatchers(HttpMethod.POST, baseUrl + "/users/**").hasAuthority("ROLE_admin")
                            .requestMatchers(HttpMethod.PUT, baseUrl + "/users/**").hasAuthority("ROLE_admin")
                            .requestMatchers(HttpMethod.DELETE, baseUrl + "/users/**").hasAuthority("ROLE_admin")
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic((httpBasic) -> httpBasic.authenticationEntryPoint(this.customBasicAuthenticationEntryPoint))
                .oauth2ResourceServer((oauth2) ->
                        oauth2.jwt(Customizer.withDefaults())
                                .authenticationEntryPoint(this.customBearerTokenAuthenticationEntryPoint)
                                .accessDeniedHandler(customBearerTokenAccessDeniedHandler))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //turn off session management when using oauth2
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // used when login returns encoded token
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jsonWebKey = new RSAKey.Builder(this.publicKey)
                .privateKey(this.privateKey)
                .build();

        JWKSource<SecurityContext> jwkSet = new ImmutableJWKSet<>(new JWKSet(jsonWebKey));

        return new NimbusJwtEncoder(jwkSet);
    }

    // used by spring-security to decode Encoded token when used to authenticate a request
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withPublicKey(this.publicKey)
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }


}
