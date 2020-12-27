
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "address",
    "functionalType",
    "configuration",
    "neighbours"
})
public class Node {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    @NotNull
    private Integer id;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("address")
    @Pattern(regexp = "^([0-9]|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5]))([.]([0-9]|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5]))){3}$")
    @NotNull
    private String address;
    @JsonProperty("functionalType")
    private FunctionalType functionalType;
    @JsonProperty("configuration")
    @Valid
    private Configuration configuration;
    @JsonProperty("neighbours")
    @Valid
    private List<Neighbour> neighbours = new ArrayList<Neighbour>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("functionalType")
    public FunctionalType getFunctionalType() {
        return functionalType;
    }

    @JsonProperty("functionalType")
    public void setFunctionalType(FunctionalType functionalType) {
        this.functionalType = functionalType;
    }

    @JsonProperty("configuration")
    public Configuration getConfiguration() {
        return configuration;
    }

    @JsonProperty("configuration")
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @JsonProperty("neighbours")
    public List<Neighbour> getNeighbours() {
        return neighbours;
    }

    @JsonProperty("neighbours")
    public void setNeighbours(List<Neighbour> neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Node.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("address");
        sb.append('=');
        sb.append(((this.address == null)?"<null>":this.address));
        sb.append(',');
        sb.append("functionalType");
        sb.append('=');
        sb.append(((this.functionalType == null)?"<null>":this.functionalType));
        sb.append(',');
        sb.append("configuration");
        sb.append('=');
        sb.append(((this.configuration == null)?"<null>":this.configuration));
        sb.append(',');
        sb.append("neighbours");
        sb.append('=');
        sb.append(((this.neighbours == null)?"<null>":this.neighbours));
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
        result = ((result* 31)+((this.functionalType == null)? 0 :this.functionalType.hashCode()));
        result = ((result* 31)+((this.neighbours == null)? 0 :this.neighbours.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.address == null)? 0 :this.address.hashCode()));
        result = ((result* 31)+((this.configuration == null)? 0 :this.configuration.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Node) == false) {
            return false;
        }
        Node rhs = ((Node) other);
        return ((((((this.functionalType == rhs.functionalType)||((this.functionalType!= null)&&this.functionalType.equals(rhs.functionalType)))&&((this.neighbours == rhs.neighbours)||((this.neighbours!= null)&&this.neighbours.equals(rhs.neighbours))))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.address == rhs.address)||((this.address!= null)&&this.address.equals(rhs.address))))&&((this.configuration == rhs.configuration)||((this.configuration!= null)&&this.configuration.equals(rhs.configuration))));
    }

}
