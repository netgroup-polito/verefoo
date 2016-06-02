package it.polito.escape.verify.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Policy")
public class Verification {

	@ApiModelProperty(example="SAT | UNSAT")
	private String result;
	
	public Verification(){
		
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
}
