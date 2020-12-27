
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "serviceGraph",
    "node"
})
public class Graph {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("serviceGraph")
    private Boolean serviceGraph = false;
    @JsonProperty("node")
    @Valid
    private List<Node> node = new ArrayList<Node>();

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
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

    @JsonProperty("node")
    public List<Node> getNode() {
        return node;
    }

    @JsonProperty("node")
    public void setNode(List<Node> node) {
        this.node = node;
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
        sb.append("node");
        sb.append('=');
        sb.append(((this.node == null)?"<null>":this.node));
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
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.serviceGraph == null)? 0 :this.serviceGraph.hashCode()));
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
        return ((((this.node == rhs.node)||((this.node!= null)&&this.node.equals(rhs.node)))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.serviceGraph == rhs.serviceGraph)||((this.serviceGraph!= null)&&this.serviceGraph.equals(rhs.serviceGraph))));
    }

}
