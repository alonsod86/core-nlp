package org.vedas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication(scanBasePackages = "org.vedas")
@EnableNeo4jRepositories("org.vedas.storage")
public class CoreNlpApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreNlpApplication.class, args);
	}
}
