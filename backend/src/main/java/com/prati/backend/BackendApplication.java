package com.prati.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load();

		String dbUrl = dotenv.get("DB_URL");
		String dbUsername = dotenv.get("DB_USERNAME");
		String dbPassword = dotenv.get("DB_PASSWORD");
		String jwtSecret = dotenv.get("JWT_SECRET");
		String jwtExpiration = dotenv.get("JWT_EXPIRATION");

		if (dbUrl == null || dbUsername == null || dbPassword == null
				|| jwtSecret == null || jwtExpiration == null) {
			throw new IllegalStateException(
					"Configuração do banco ou JWT não encontrada. " +
							"Verifique se o arquivo .env contém todas as variáveis necessárias."
			);
		}

		System.setProperty("DB_URL", dbUrl);
		System.setProperty("DB_USERNAME", dbUsername);
		System.setProperty("DB_PASSWORD", dbPassword);
		System.setProperty("JWT_SECRET", jwtSecret);
		System.setProperty("JWT_EXPIRATION", jwtExpiration);

		SpringApplication.run(BackendApplication.class, args);
	}
}