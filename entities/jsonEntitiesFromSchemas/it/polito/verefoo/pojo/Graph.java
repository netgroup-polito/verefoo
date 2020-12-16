
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "serviceGraph",
    "nodes",
    "constraints"
})
public class Graph {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("serviceGraph")
    private Boolean serviceGraph = false;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nodes")
    private List<Node> nodes = new ArrayList<Node>();
    @JsonProperty("constraints")
    private Constraints constraints;

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

    @JsonProperty("serviceGraph")
    public Boolean getServiceGraph() {
        return serviceGraph;
    }

    @JsonProperty("serviceGraph")
    public void setServiceGraph(Boolean serviceGraph) {
        this.serviceGraph = serviceGraph;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nodes")
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nodes")
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    @JsonProperty("constraints")
    public Constraints getConstraints() {
        return constraints;
    }

    @JsonProperty("constraints")
    public void setConstraints(Constraints constraints) {
        this.constraints = constraints;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Graph.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("serviceGraph");
        sb.append('=');
        sb.append(((this.serviceGraph == null)?"<null>":this.serviceGraph));
        sb.append(',');
        sb.append("nodes");
        sb.append('=');
        sb.append(((this.nodes == null)?"<null>":this.nodes));
        sb.append(',');
        sb.append("constraints");
        sb.append('=');
        sb.append(((this.constraints == null)?"<null>":this.constraints));
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
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.serviceGraph == null)? 0 :this.serviceGraph.hashCode()));
        result = ((result* 31)+((this.nodes == null)? 0 :this.nodes.hashCode()));
        result = ((result* 31)+((this.constraints == null)? 0 :this.constraints.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Graph) == false) {
            return false;
        }
        Graph rhs = ((Graph) other);
        return (((((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id)))&&((this.serviceGraph == rhs.serviceGraph)||((this.serviceGraph!= null)&&this.serviceGraph.equals(rhs.serviceGraph))))&&((this.nodes == rhs.nodes)||((this.nodes!= null)&&this.nodes.equals(rhs.nodes))))&&((this.constraints == rhs.constraints)||((this.constraints!= null)&&this.constraints.equals(rhs.constraints))));
    }

}
