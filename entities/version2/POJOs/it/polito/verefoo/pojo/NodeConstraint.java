
package it.polito.verefoo.pojo;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "node",
    "numberOfOperations",
    "maxNodeLatency",
    "requestStorage",
    "cores",
    "memory",
    "optional"
})
public class NodeConstraint {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("node")
    @NotNull
    private String node;
    @JsonProperty("numberOfOperations")
    private Double numberOfOperations;
    @JsonProperty("maxNodeLatency")
    private Double maxNodeLatency;
    @JsonProperty("requestStorage")
    private Double requestStorage = 0.0D;
    @JsonProperty("cores")
    private Double cores = 0.0D;
    @JsonProperty("memory")
    private Double memory = 0.0D;
    @JsonProperty("optional")
    private Boolean optional = false;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("node")
    public String getNode() {
        return node;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("node")
    public void setNode(String node) {
        this.node = node;
    }

    @JsonProperty("numberOfOperations")
    public Double getNumberOfOperations() {
        return numberOfOperations;
    }

    @JsonProperty("numberOfOperations")
    public void setNumberOfOperations(Double numberOfOperations) {
        this.numberOfOperations = numberOfOperations;
    }

    @JsonProperty("maxNodeLatency")
    public Double getMaxNodeLatency() {
        return maxNodeLatency;
    }

    @JsonProperty("maxNodeLatency")
    public void setMaxNodeLatency(Double maxNodeLatency) {
        this.maxNodeLatency = maxNodeLatency;
    }

    @JsonProperty("requestStorage")
    public Double getRequestStorage() {
        return requestStorage;
    }

    @JsonProperty("requestStorage")
    public void setRequestStorage(Double requestStorage) {
        this.requestStorage = requestStorage;
    }

    @JsonProperty("cores")
    public Double getCores() {
        return cores;
    }

    @JsonProperty("cores")
    public void setCores(Double cores) {
        this.cores = cores;
    }

    @JsonProperty("memory")
    public Double getMemory() {
        return memory;
    }

    @JsonProperty("memory")
    public void setMemory(Double memory) {
        this.memory = memory;
    }

    @JsonProperty("optional")
    public Boolean getOptional() {
        return optional;
    }

    @JsonProperty("optional")
    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(NodeConstraint.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("node");
        sb.append('=');
        sb.append(((this.node == null)?"<null>":this.node));
        sb.append(',');
        sb.append("numberOfOperations");
        sb.append('=');
        sb.append(((this.numberOfOperations == null)?"<null>":this.numberOfOperations));
        sb.append(',');
        sb.append("maxNodeLatency");
        sb.append('=');
        sb.append(((this.maxNodeLatency == null)?"<null>":this.maxNodeLatency));
        sb.append(',');
        sb.append("requestStorage");
        sb.append('=');
        sb.append(((this.requestStorage == null)?"<null>":this.requestStorage));
        sb.append(',');
        sb.append("cores");
        sb.append('=');
        sb.append(((this.cores == null)?"<null>":this.cores));
        sb.append(',');
        sb.append("memory");
        sb.append('=');
        sb.append(((this.memory == null)?"<null>":this.memory));
        sb.append(',');
        sb.append("optional");
        sb.append('=');
        sb.append(((this.optional == null)?"<null>":this.optional));
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
        result = ((result* 31)+((this.node == null)? 0 :this.node.hashCode()));
        result = ((result* 31)+((this.requestStorage == null)? 0 :this.requestStorage.hashCode()));
        result = ((result* 31)+((this.cores == null)? 0 :this.cores.hashCode()));
        result = ((result* 31)+((this.memory == null)? 0 :this.memory.hashCode()));
        result = ((result* 31)+((this.numberOfOperations == null)? 0 :this.numberOfOperations.hashCode()));
        result = ((result* 31)+((this.optional == null)? 0 :this.optional.hashCode()));
        result = ((result* 31)+((this.maxNodeLatency == null)? 0 :this.maxNodeLatency.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NodeConstraint) == false) {
            return false;
        }
        NodeConstraint rhs = ((NodeConstraint) other);
        return ((((((((this.node == rhs.node)||((this.node!= null)&&this.node.equals(rhs.node)))&&((this.requestStorage == rhs.requestStorage)||((this.requestStorage!= null)&&this.requestStorage.equals(rhs.requestStorage))))&&((this.cores == rhs.cores)||((this.cores!= null)&&this.cores.equals(rhs.cores))))&&((this.memory == rhs.memory)||((this.memory!= null)&&this.memory.equals(rhs.memory))))&&((this.numberOfOperations == rhs.numberOfOperations)||((this.numberOfOperations!= null)&&this.numberOfOperations.equals(rhs.numberOfOperations))))&&((this.optional == rhs.optional)||((this.optional!= null)&&this.optional.equals(rhs.optional))))&&((this.maxNodeLatency == rhs.maxNodeLatency)||((this.maxNodeLatency!= null)&&this.maxNodeLatency.equals(rhs.maxNodeLatency))));
    }

}
