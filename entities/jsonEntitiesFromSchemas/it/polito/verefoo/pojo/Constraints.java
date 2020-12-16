
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "nodeConstraints",
    "linkConstraints",
    "allocationConstraints"
})
public class Constraints {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nodeConstraints")
    private List<NodeConstraint> nodeConstraints = new ArrayList<NodeConstraint>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("linkConstraints")
    private List<LinkConstraint> linkConstraints = new ArrayList<LinkConstraint>();
    @JsonProperty("allocationConstraints")
    private List<AllocationConstraint> allocationConstraints = new ArrayList<AllocationConstraint>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nodeConstraints")
    public List<NodeConstraint> getNodeConstraints() {
        return nodeConstraints;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nodeConstraints")
    public void setNodeConstraints(List<NodeConstraint> nodeConstraints) {
        this.nodeConstraints = nodeConstraints;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("linkConstraints")
    public List<LinkConstraint> getLinkConstraints() {
        return linkConstraints;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("linkConstraints")
    public void setLinkConstraints(List<LinkConstraint> linkConstraints) {
        this.linkConstraints = linkConstraints;
    }

    @JsonProperty("allocationConstraints")
    public List<AllocationConstraint> getAllocationConstraints() {
        return allocationConstraints;
    }

    @JsonProperty("allocationConstraints")
    public void setAllocationConstraints(List<AllocationConstraint> allocationConstraints) {
        this.allocationConstraints = allocationConstraints;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Constraints.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("nodeConstraints");
        sb.append('=');
        sb.append(((this.nodeConstraints == null)?"<null>":this.nodeConstraints));
        sb.append(',');
        sb.append("linkConstraints");
        sb.append('=');
        sb.append(((this.linkConstraints == null)?"<null>":this.linkConstraints));
        sb.append(',');
        sb.append("allocationConstraints");
        sb.append('=');
        sb.append(((this.allocationConstraints == null)?"<null>":this.allocationConstraints));
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
        result = ((result* 31)+((this.nodeConstraints == null)? 0 :this.nodeConstraints.hashCode()));
        result = ((result* 31)+((this.linkConstraints == null)? 0 :this.linkConstraints.hashCode()));
        result = ((result* 31)+((this.allocationConstraints == null)? 0 :this.allocationConstraints.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Constraints) == false) {
            return false;
        }
        Constraints rhs = ((Constraints) other);
        return ((((this.nodeConstraints == rhs.nodeConstraints)||((this.nodeConstraints!= null)&&this.nodeConstraints.equals(rhs.nodeConstraints)))&&((this.linkConstraints == rhs.linkConstraints)||((this.linkConstraints!= null)&&this.linkConstraints.equals(rhs.linkConstraints))))&&((this.allocationConstraints == rhs.allocationConstraints)||((this.allocationConstraints!= null)&&this.allocationConstraints.equals(rhs.allocationConstraints))));
    }

}
