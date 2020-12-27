
package it.polito.verefoo.pojo;

import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "graphs",
    "Constraints",
    "PropertyDefinition",
    "ParsingString"
})
public class NFV {

    @JsonProperty("graphs")
    @Valid
    private Graphs graphs;
    @JsonProperty("Constraints")
    private Object constraints;
    @JsonProperty("PropertyDefinition")
    private Object propertyDefinition;
    @JsonProperty("ParsingString")
    private Object parsingString;

    @JsonProperty("graphs")
    public Graphs getGraphs() {
        return graphs;
    }

    @JsonProperty("graphs")
    public void setGraphs(Graphs graphs) {
        this.graphs = graphs;
    }

    @JsonProperty("Constraints")
    public Object getConstraints() {
        return constraints;
    }

    @JsonProperty("Constraints")
    public void setConstraints(Object constraints) {
        this.constraints = constraints;
    }

    @JsonProperty("PropertyDefinition")
    public Object getPropertyDefinition() {
        return propertyDefinition;
    }

    @JsonProperty("PropertyDefinition")
    public void setPropertyDefinition(Object propertyDefinition) {
        this.propertyDefinition = propertyDefinition;
    }

    @JsonProperty("ParsingString")
    public Object getParsingString() {
        return parsingString;
    }

    @JsonProperty("ParsingString")
    public void setParsingString(Object parsingString) {
        this.parsingString = parsingString;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(NFV.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("graphs");
        sb.append('=');
        sb.append(((this.graphs == null)?"<null>":this.graphs));
        sb.append(',');
        sb.append("constraints");
        sb.append('=');
        sb.append(((this.constraints == null)?"<null>":this.constraints));
        sb.append(',');
        sb.append("propertyDefinition");
        sb.append('=');
        sb.append(((this.propertyDefinition == null)?"<null>":this.propertyDefinition));
        sb.append(',');
        sb.append("parsingString");
        sb.append('=');
        sb.append(((this.parsingString == null)?"<null>":this.parsingString));
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
        result = ((result* 31)+((this.propertyDefinition == null)? 0 :this.propertyDefinition.hashCode()));
        result = ((result* 31)+((this.graphs == null)? 0 :this.graphs.hashCode()));
        result = ((result* 31)+((this.constraints == null)? 0 :this.constraints.hashCode()));
        result = ((result* 31)+((this.parsingString == null)? 0 :this.parsingString.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NFV) == false) {
            return false;
        }
        NFV rhs = ((NFV) other);
        return (((((this.propertyDefinition == rhs.propertyDefinition)||((this.propertyDefinition!= null)&&this.propertyDefinition.equals(rhs.propertyDefinition)))&&((this.graphs == rhs.graphs)||((this.graphs!= null)&&this.graphs.equals(rhs.graphs))))&&((this.constraints == rhs.constraints)||((this.constraints!= null)&&this.constraints.equals(rhs.constraints))))&&((this.parsingString == rhs.parsingString)||((this.parsingString!= null)&&this.parsingString.equals(rhs.parsingString))));
    }

}
