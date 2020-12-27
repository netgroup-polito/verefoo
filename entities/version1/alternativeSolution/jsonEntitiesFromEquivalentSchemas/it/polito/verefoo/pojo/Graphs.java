
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "graph"
})
public class Graphs {

    @JsonProperty("graph")
    @Valid
    private List<Object> graph = new ArrayList<Object>();

    @JsonProperty("graph")
    public List<Object> getGraph() {
        return graph;
    }

    @JsonProperty("graph")
    public void setGraph(List<Object> graph) {
        this.graph = graph;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Graphs.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("graph");
        sb.append('=');
        sb.append(((this.graph == null)?"<null>":this.graph));
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
        result = ((result* 31)+((this.graph == null)? 0 :this.graph.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Graphs) == false) {
            return false;
        }
        Graphs rhs = ((Graphs) other);
        return ((this.graph == rhs.graph)||((this.graph!= null)&&this.graph.equals(rhs.graph)));
    }

}
