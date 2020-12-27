
package it.polito.verefoo.pojo;

import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ApplicationError"
})
public class ErrorSchema {

    @JsonProperty("ApplicationError")
    @Valid
    private ApplicationError__1 applicationError;

    @JsonProperty("ApplicationError")
    public ApplicationError__1 getApplicationError() {
        return applicationError;
    }

    @JsonProperty("ApplicationError")
    public void setApplicationError(ApplicationError__1 applicationError) {
        this.applicationError = applicationError;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ErrorSchema.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("applicationError");
        sb.append('=');
        sb.append(((this.applicationError == null)?"<null>":this.applicationError));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.applicationError == null)? 0 :this.applicationError.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ErrorSchema) == false) {
            return false;
        }
        ErrorSchema rhs = ((ErrorSchema) other);
        return ((this.applicationError == rhs.applicationError)||((this.applicationError!= null)&&this.applicationError.equals(rhs.applicationError)));
    }

}
