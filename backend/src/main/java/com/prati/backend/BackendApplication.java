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

		if (dbUrl == null || dbUsername == null || dbPassword == null) {
			throw new IllegalStateException(
					"Configuração do banco não encontrada. " +
							"Verifique se o arquivo .env existe e contém " +
							"DB_URL, DB_USERNAME e DB_PASSWORD."
			);
		}

		System.setProperty("DB_URL", dbUrl);
		System.setProperty("DB_USERNAME", dbUsername);
		System.setProperty("DB_PASSWORD", dbPassword);

		SpringApplication.run(BackendApplication.class, args);
	}
}