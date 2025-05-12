package com.parcheggio.parcheggio_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
@SpringBootApplication
public class ParcheggioBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParcheggioBackendApplication.class, args);
	}
	@Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Permette l'uso di credenziali (es. cookie)
        config.addAllowedOrigin("http://localhost:4200"); // URL del frontend Angular
        config.addAllowedHeader("*"); // Permette tutti gli header
        config.addAllowedMethod("*"); // Permette tutti i metodi (GET, POST, ecc.)
        source.registerCorsConfiguration("/**", config); // Applica a tutti gli endpoint
        return new CorsFilter(source);
    }

}
