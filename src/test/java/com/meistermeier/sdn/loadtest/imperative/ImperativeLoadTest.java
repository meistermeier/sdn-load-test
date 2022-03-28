package com.meistermeier.sdn.loadtest.imperative;

import com.meistermeier.sdn.loadtest.Neo4jConfiguration;
import com.meistermeier.sdn.loadtest.shared.Node;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Neo4jConfiguration.class)
class ImperativeLoadTest {

	@Test
	@Timeout(15)
	void loadLotsOfPaths(@Autowired ARepository repository) {
		assertThat(repository.doThings().bs).hasSize(84);
	}

	@Test
	@Timeout(15)
	void loadLotsOfNode(@Autowired NodeRepository repository) {
		assertThat(repository.findAll()).hasSize(300_000);
	}

	interface ARepository extends Neo4jRepository<Node.A, Long> {

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
		Node.A doThings();

	}

	interface NodeRepository extends Neo4jRepository<Node, Long> {}

}
