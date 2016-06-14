package it.polito.escape.verify.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Value {
	private final String value;

	@JsonCreator
	Value(@JsonProperty("value")
	final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Value{" + "value='" + value + '\'' + '}';
	}

}
