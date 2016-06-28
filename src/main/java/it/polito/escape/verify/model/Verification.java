package it.polito.escape.verify.model;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Policy verification")
public class Verification {

	@ApiModelProperty(example = "SAT | UNSAT | UNKNOWN")
	private String		result;
	private List<Test>	tests	= new ArrayList<Test>();

	public Verification() {

	}

	public Verification(String result) {
		this.result = result;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<Test> getTests() {
		return tests;
	}

	public void setTests(List<Test> tests) {
		this.tests = tests;
	}

}
