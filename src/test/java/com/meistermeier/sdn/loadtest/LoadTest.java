package com.meistermeier.sdn.loadtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Neo4jConfiguration.class)
class LoadTest {

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

	interface ARepository extends Neo4jRepository<A, Long> {

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
		A doThings();

	}

	@org.springframework.data.neo4j.core.schema.Node("A")
	public static class A {

		@Id
		@GeneratedValue
		private Long id;
		@Relationship("HAS")
		List<B> bs;
	}

	@org.springframework.data.neo4j.core.schema.Node("B")
	public static class B {
		@Id @GeneratedValue private Long id;
		@Relationship("AS")
		List<C> as;
		@Relationship("BS")
		List<C> bs;
	}

	@org.springframework.data.neo4j.core.schema.Node("C")
	public static class C {
		@Id @GeneratedValue private Long id;
		@Relationship("CS")
		List<D> cs;
	}

	@org.springframework.data.neo4j.core.schema.Node("D")
	public static class D {
		@Id @GeneratedValue private Long id;

		@Relationship("CS")
		List<C> cs;
	}

	interface NodeRepository extends Neo4jRepository<Node, Long> {}

	@org.springframework.data.neo4j.core.schema.Node("Blubb")
	public static class Node {
		@Id @GeneratedValue public Long id;
		String value;
		String value2;
	}

}
