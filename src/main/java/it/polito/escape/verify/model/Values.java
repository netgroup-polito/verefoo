package it.polito.escape.verify.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class Values {
	private final Map<String, Value> values = new HashMap<>();

	@JsonAnySetter
	public void setValue(final String property, final Value value) {
		values.put(property, value);
	}

	@Override
	public String toString() {
		return "Values{" + "values=" + values + '}';
	}
}
