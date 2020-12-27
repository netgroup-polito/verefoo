
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "functional_type",
    "neighbour",
    "configuration"
})
public class Node__1 {

    @JsonProperty("id")
    private Long id;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    @NotNull
    private String name;
    @JsonProperty("functional_type")
    private FunctionalTypes functionalType;
    @JsonProperty("neighbour")
    @Valid
    private List<Neighbour> neighbour = new ArrayList<Neighbour>();
    @JsonProperty("configuration")
    @Valid
    private Configuration__1 configuration;

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("functional_type")
    public FunctionalTypes getFunctionalType() {
        return functionalType;
    }

    @JsonProperty("functional_type")
    public void setFunctionalType(FunctionalTypes functionalType) {
        this.functionalType = functionalType;
    }

    @JsonProperty("neighbour")
    public List<Neighbour> getNeighbour() {
        return neighbour;
    }

    @JsonProperty("neighbour")
    public void setNeighbour(List<Neighbour> neighbour) {
        this.neighbour = neighbour;
    }

    @JsonProperty("configuration")
    public Configuration__1 getConfiguration() {
        return configuration;
    }

    @JsonProperty("configuration")
    public void setConfiguration(Configuration__1 configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Node__1 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("functionalType");
        sb.append('=');
        sb.append(((this.functionalType == null)?"<null>":this.functionalType));
        sb.append(',');
        sb.append("neighbour");
        sb.append('=');
        sb.append(((this.neighbour == null)?"<null>":this.neighbour));
        sb.append(',');
        sb.append("configuration");
        sb.append('=');
        sb.append(((this.configuration == null)?"<null>":this.configuration));
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
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.functionalType == null)? 0 :this.functionalType.hashCode()));
        result = ((result* 31)+((this.neighbour == null)? 0 :this.neighbour.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.configuration == null)? 0 :this.configuration.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Node__1) == false) {
            return false;
        }
        Node__1 rhs = ((Node__1) other);
        return ((((((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name)))&&((this.functionalType == rhs.functionalType)||((this.functionalType!= null)&&this.functionalType.equals(rhs.functionalType))))&&((this.neighbour == rhs.neighbour)||((this.neighbour!= null)&&this.neighbour.equals(rhs.neighbour))))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.configuration == rhs.configuration)||((this.configuration!= null)&&this.configuration.equals(rhs.configuration))));
    }

}
