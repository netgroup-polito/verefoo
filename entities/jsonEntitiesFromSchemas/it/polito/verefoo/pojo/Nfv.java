
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * NFV
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "graphs",
    "requirements",
    "substrates",
    "networkForwardingPaths",
    "parsingString"
})
public class Nfv {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("graphs")
    private List<Graph> graphs = new ArrayList<Graph>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requirements")
    private List<Requirement> requirements = new ArrayList<Requirement>();
    @JsonProperty("substrates")
    private List<Substrate> substrates = new ArrayList<Substrate>();
    @JsonProperty("networkForwardingPaths")
    private List<NetworkForwardingPath> networkForwardingPaths = new ArrayList<NetworkForwardingPath>();
    @JsonProperty("parsingString")
    private String parsingString;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("graphs")
    public List<Graph> getGraphs() {
        return graphs;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("graphs")
    public void setGraphs(List<Graph> graphs) {
        this.graphs = graphs;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requirements")
    public List<Requirement> getRequirements() {
        return requirements;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requirements")
    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    @JsonProperty("substrates")
    public List<Substrate> getSubstrates() {
        return substrates;
    }

    @JsonProperty("substrates")
    public void setSubstrates(List<Substrate> substrates) {
        this.substrates = substrates;
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
        sb.append(Nfv.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("graphs");
        sb.append('=');
        sb.append(((this.graphs == null)?"<null>":this.graphs));
        sb.append(',');
        sb.append("requirements");
        sb.append('=');
        sb.append(((this.requirements == null)?"<null>":this.requirements));
        sb.append(',');
        sb.append("substrates");
        sb.append('=');
        sb.append(((this.substrates == null)?"<null>":this.substrates));
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
        result = ((result* 31)+((this.graphs == null)? 0 :this.graphs.hashCode()));
        result = ((result* 31)+((this.requirements == null)? 0 :this.requirements.hashCode()));
        result = ((result* 31)+((this.networkForwardingPaths == null)? 0 :this.networkForwardingPaths.hashCode()));
        result = ((result* 31)+((this.parsingString == null)? 0 :this.parsingString.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Nfv) == false) {
            return false;
        }
        Nfv rhs = ((Nfv) other);
        return ((((((this.substrates == rhs.substrates)||((this.substrates!= null)&&this.substrates.equals(rhs.substrates)))&&((this.graphs == rhs.graphs)||((this.graphs!= null)&&this.graphs.equals(rhs.graphs))))&&((this.requirements == rhs.requirements)||((this.requirements!= null)&&this.requirements.equals(rhs.requirements))))&&((this.networkForwardingPaths == rhs.networkForwardingPaths)||((this.networkForwardingPaths!= null)&&this.networkForwardingPaths.equals(rhs.networkForwardingPaths))))&&((this.parsingString == rhs.parsingString)||((this.parsingString!= null)&&this.parsingString.equals(rhs.parsingString))));
    }

}
