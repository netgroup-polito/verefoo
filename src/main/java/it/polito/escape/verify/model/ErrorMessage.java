package it.polito.escape.verify.model;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Error")
@XmlRootElement
public class ErrorMessage {

	@ApiModelProperty(example = "Error message")
	private String	errorMessage;
	@ApiModelProperty(allowableValues = "400,403,404,500", value = "HTTP error code", example = "[400,403,404,500]")
	private int		errorCode;
	@ApiModelProperty(example = "http://localhost:8080/verify/api-docs/")
	private String	documentation;

	public ErrorMessage() {

	}

	public ErrorMessage(String errorMessage, int errorCode, String documentation) {
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
		this.documentation = documentation;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

}
