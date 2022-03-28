package com.meistermeier.sdn.loadtest;

import ac.simons.neo4j.migrations.core.MigrationVersion;
import ac.simons.neo4j.migrations.core.Migrations;
import ac.simons.neo4j.migrations.core.MigrationsConfig;
import org.jetbrains.annotations.NotNull;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.AbstractNeo4jConfig;
import org.springframework.data.neo4j.config.AbstractReactiveNeo4jConfig;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories;
import org.testcontainers.containers.Neo4jContainer;

@Configuration
@EnableReactiveNeo4jRepositories(considerNestedRepositories = true)
public class Neo4jReactiveConfiguration extends AbstractReactiveNeo4jConfig {

	@Override
	@NotNull
	@Bean
	public Driver driver() {
		return GraphDatabase.driver(neo4jContainer().getBoltUrl());
	}

	private Neo4jContainer<?> neo4jContainer() {
		Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:4.4")
				.withReuse(true)
				.withoutAuthentication();
		neo4jContainer.start();
		return neo4jContainer;
	}

	@Bean
	public MigrationVersion migrationVersion() {
		MigrationsConfig migrationsConfig = MigrationsConfig.builder().withLocationsToScan("classpath:/neo4j").build();
		var migrations = new Migrations(migrationsConfig, driver());
		var migrationVersionOptional = migrations.apply();
		return migrationVersionOptional.get();
	}

}
