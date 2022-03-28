package com.meistermeier.sdn.loadtest.reactive;

import com.meistermeier.sdn.loadtest.Neo4jReactiveConfiguration;
import com.meistermeier.sdn.loadtest.shared.Node;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Neo4jReactiveConfiguration.class)
public class ReactiveLoadTest {
	@Test
	@Timeout(15)
	void loadLotsOfPaths(@Autowired ARepository repository) {
		StepVerifier.create(repository.doThings())
				.assertNext(a -> assertThat(a.bs).hasSize(84))
				.verifyComplete();
	}

	@Test
	@Timeout(15)
	void loadLotsOfNode(@Autowired NodeRepository repository) {
		StepVerifier.create(repository.findAll())
				.expectNextCount(300_000)
				.verifyComplete();
	}

	interface ARepository extends ReactiveNeo4jRepository<Node.A, Long> {

		@Query("""
				MATCH aPath = (a:A)
				MATCH bPath = (a)-[:HAS]->(b:B)-[:AS|:BS]->(c:C)
				OPTIONAL MATCH dPath =
				(c)-[:CS]->(d:D)-[:CS]->(c2:C),
				(b)-[:AS|:BS]->(c2)
				RETURN
				aPath, collect(nodes(bPath)), collect(relationships(bPath)),
				collect(nodes(dPath)), collect(relationships(dPath))
				""")
		Mono<Node.A> doThings();

	}


	interface NodeRepository extends ReactiveNeo4jRepository<Node, Long> {
	}
}
