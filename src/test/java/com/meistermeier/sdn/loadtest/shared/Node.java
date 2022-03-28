package com.meistermeier.sdn.loadtest.shared;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@org.springframework.data.neo4j.core.schema.Node("Blubb")
public class Node {
	@Id
	@GeneratedValue
	public Long id;
	public String value;
	public String value2;

	@org.springframework.data.neo4j.core.schema.Node("A")
	public static class A {

		@Id
		@GeneratedValue
		private Long id;
		@Relationship("HAS")
		public List<B> bs;
	}

	@org.springframework.data.neo4j.core.schema.Node("B")
	public static class B {
		@Id @GeneratedValue private Long id;
		@Relationship("AS")
		public List<C> as;
		@Relationship("BS")
		public List<C> bs;
	}

	@org.springframework.data.neo4j.core.schema.Node("C")
	public static class C {
		@Id @GeneratedValue private Long id;
		@Relationship("CS")
		public List<D> cs;
	}

	@org.springframework.data.neo4j.core.schema.Node("D")
	public static class D {
		@Id @GeneratedValue private Long id;

		@Relationship("CS")
		List<C> cs;
	}
}
