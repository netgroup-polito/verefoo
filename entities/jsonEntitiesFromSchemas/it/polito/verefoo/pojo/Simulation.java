
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "graph",
    "substrates",
    "networkFunctions",
    "networkForwardingPaths",
    "parsingString"
})
public class Simulation {

    @JsonProperty("graph")
    @Valid
    private Graph graph;
    @JsonProperty("substrates")
    @Valid
    private List<Substrate> substrates = new ArrayList<Substrate>();
    /**
     * The array of functions that the system can use for each graph.
     * (Required)
     * 
     */
    @JsonProperty("networkFunctions")
    @JsonPropertyDescription("The array of functions that the system can use for each graph.")
    @Valid
    @NotNull
    private List<FunctionalType> networkFunctions = new ArrayList<FunctionalType>();
    @JsonProperty("networkForwardingPaths")
    @Valid
    private List<NetworkForwardingPath> networkForwardingPaths = new ArrayList<NetworkForwardingPath>();
    @JsonProperty("parsingString")
    private String parsingString;

    @JsonProperty("graph")
    public Graph getGraph() {
        return graph;
    }

    @JsonProperty("graph")
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    @JsonProperty("substrates")
    public List<Substrate> getSubstrates() {
        return substrates;
    }

    @JsonProperty("substrates")
    public void setSubstrates(List<Substrate> substrates) {
        this.substrates = substrates;
    }

    /**
     * The array of functions that the system can use for each graph.
     * (Required)
     * 
     */
    @JsonProperty("networkFunctions")
    public List<FunctionalType> getNetworkFunctions() {
        return networkFunctions;
    }

    /**
     * The array of functions that the system can use for each graph.
     * (Required)
     * 
     */
    @JsonProperty("networkFunctions")
    public void setNetworkFunctions(List<FunctionalType> networkFunctions) {
        this.networkFunctions = networkFunctions;
    }

    @JsonProperty("networkForwardingPaths")
    public List<NetworkForwardingPath> getNetworkForwardingPaths() {
        return networkForwardingPaths;
    }

    @JsonProperty("networkForwardingPaths")
    public void setNetworkForwardingPaths(List<NetworkForwardingPath> networkForwardingPaths) {
        this.networkForwardingPaths = networkForwardingPaths;
    }

    @JsonProperty("parsingString")
    public String getParsingString() {
        return parsingString;
    }

    @JsonProperty("parsingString")
    public void setParsingString(String parsingString) {
        this.parsingString = parsingString;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Simulation.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("graph");
        sb.append('=');
        sb.append(((this.graph == null)?"<null>":this.graph));
        sb.append(',');
        sb.append("substrates");
        sb.append('=');
        sb.append(((this.substrates == null)?"<null>":this.substrates));
        sb.append(',');
        sb.append("networkFunctions");
        sb.append('=');
        sb.append(((this.networkFunctions == null)?"<null>":this.networkFunctions));
        sb.append(',');
        sb.append("networkForwardingPaths");
        sb.append('=');
        sb.append(((this.networkForwardingPaths == null)?"<null>":this.networkForwardingPaths));
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
        result = ((result* 31)+((this.substrates == null)? 0 :this.substrates.hashCode()));
        result = ((result* 31)+((this.networkFunctions == null)? 0 :this.networkFunctions.hashCode()));
        result = ((result* 31)+((this.graph == null)? 0 :this.graph.hashCode()));
        result = ((result* 31)+((this.networkForwardingPaths == null)? 0 :this.networkForwardingPaths.hashCode()));
        result = ((result* 31)+((this.parsingString == null)? 0 :this.parsingString.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Simulation) == false) {
            return false;
        }
        Simulation rhs = ((Simulation) other);
        return ((((((this.substrates == rhs.substrates)||((this.substrates!= null)&&this.substrates.equals(rhs.substrates)))&&((this.networkFunctions == rhs.networkFunctions)||((this.networkFunctions!= null)&&this.networkFunctions.equals(rhs.networkFunctions))))&&((this.graph == rhs.graph)||((this.graph!= null)&&this.graph.equals(rhs.graph))))&&((this.networkForwardingPaths == rhs.networkForwardingPaths)||((this.networkForwardingPaths!= null)&&this.networkForwardingPaths.equals(rhs.networkForwardingPaths))))&&((this.parsingString == rhs.parsingString)||((this.parsingString!= null)&&this.parsingString.equals(rhs.parsingString))));
    }

}
