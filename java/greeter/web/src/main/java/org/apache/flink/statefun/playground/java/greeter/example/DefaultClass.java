package org.apache.flink.statefun.playground.java.greeter.example;

/**
 * Default class needed for deserialization
 */
public class DefaultClass {
	private String field;

	public DefaultClass() {}

	public DefaultClass(String field) {
		this.field = field;
	}

	public String getField() {
		return field;
	}
}
