
package it.polito.verefoo.pojo;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "nodeA",
    "nodeB"
})
public class AllocationConstraint {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    @NotNull
    private AllocationConstraintType type;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nodeA")
    @NotNull
    private String nodeA;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nodeB")
    @NotNull
    private String nodeB;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    public AllocationConstraintType getType() {
        return type;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    public void setType(AllocationConstraintType type) {
        this.type = type;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nodeA")
    public String getNodeA() {
        return nodeA;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nodeA")
    public void setNodeA(String nodeA) {
        this.nodeA = nodeA;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nodeB")
    public String getNodeB() {
        return nodeB;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nodeB")
    public void setNodeB(String nodeB) {
        this.nodeB = nodeB;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AllocationConstraint.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null)?"<null>":this.type));
        sb.append(',');
        sb.append("nodeA");
        sb.append('=');
        sb.append(((this.nodeA == null)?"<null>":this.nodeA));
        sb.append(',');
        sb.append("nodeB");
        sb.append('=');
        sb.append(((this.nodeB == null)?"<null>":this.nodeB));
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
        result = ((result* 31)+((this.nodeB == null)? 0 :this.nodeB.hashCode()));
        result = ((result* 31)+((this.type == null)? 0 :this.type.hashCode()));
        result = ((result* 31)+((this.nodeA == null)? 0 :this.nodeA.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AllocationConstraint) == false) {
            return false;
        }
        AllocationConstraint rhs = ((AllocationConstraint) other);
        return ((((this.nodeB == rhs.nodeB)||((this.nodeB!= null)&&this.nodeB.equals(rhs.nodeB)))&&((this.type == rhs.type)||((this.type!= null)&&this.type.equals(rhs.type))))&&((this.nodeA == rhs.nodeA)||((this.nodeA!= null)&&this.nodeA.equals(rhs.nodeA))));
    }

}
