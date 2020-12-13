
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
    "constraints",
    "propertyDefinitions",
    "hosts",
    "connections",
    "networkForwardingPaths",
    "parsingString"
})
public class NfvSchema {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("graphs")
    private List<Graph> graphs = new ArrayList<Graph>();
    @JsonProperty("constraints")
    private Constraints constraints;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("propertyDefinitions")
    private List<Property> propertyDefinitions = new ArrayList<Property>();
    @JsonProperty("hosts")
    private List<Host> hosts = new ArrayList<Host>();
    @JsonProperty("connections")
    private List<Connection> connections = new ArrayList<Connection>();
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

    @JsonProperty("constraints")
    public Constraints getConstraints() {
        return constraints;
    }

    @JsonProperty("constraints")
    public void setConstraints(Constraints constraints) {
        this.constraints = constraints;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("propertyDefinitions")
    public List<Property> getPropertyDefinitions() {
        return propertyDefinitions;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("propertyDefinitions")
    public void setPropertyDefinitions(List<Property> propertyDefinitions) {
        this.propertyDefinitions = propertyDefinitions;
    }

    @JsonProperty("hosts")
    public List<Host> getHosts() {
        return hosts;
    }

    @JsonProperty("hosts")
    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    @JsonProperty("connections")
    public List<Connection> getConnections() {
        return connections;
    }

    @JsonProperty("connections")
    public void setConnections(List<Connection> connections) {
        this.connections = connections;
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
        sb.append(NfvSchema.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("graphs");
        sb.append('=');
        sb.append(((this.graphs == null)?"<null>":this.graphs));
        sb.append(',');
        sb.append("constraints");
        sb.append('=');
        sb.append(((this.constraints == null)?"<null>":this.constraints));
        sb.append(',');
        sb.append("propertyDefinitions");
        sb.append('=');
        sb.append(((this.propertyDefinitions == null)?"<null>":this.propertyDefinitions));
        sb.append(',');
        sb.append("hosts");
        sb.append('=');
        sb.append(((this.hosts == null)?"<null>":this.hosts));
        sb.append(',');
        sb.append("connections");
        sb.append('=');
        sb.append(((this.connections == null)?"<null>":this.connections));
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
        result = ((result* 31)+((this.propertyDefinitions == null)? 0 :this.propertyDefinitions.hashCode()));
        result = ((result* 31)+((this.graphs == null)? 0 :this.graphs.hashCode()));
        result = ((result* 31)+((this.hosts == null)? 0 :this.hosts.hashCode()));
        result = ((result* 31)+((this.networkForwardingPaths == null)? 0 :this.networkForwardingPaths.hashCode()));
        result = ((result* 31)+((this.parsingString == null)? 0 :this.parsingString.hashCode()));
        result = ((result* 31)+((this.constraints == null)? 0 :this.constraints.hashCode()));
        result = ((result* 31)+((this.connections == null)? 0 :this.connections.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NfvSchema) == false) {
            return false;
        }
        NfvSchema rhs = ((NfvSchema) other);
        return ((((((((this.propertyDefinitions == rhs.propertyDefinitions)||((this.propertyDefinitions!= null)&&this.propertyDefinitions.equals(rhs.propertyDefinitions)))&&((this.graphs == rhs.graphs)||((this.graphs!= null)&&this.graphs.equals(rhs.graphs))))&&((this.hosts == rhs.hosts)||((this.hosts!= null)&&this.hosts.equals(rhs.hosts))))&&((this.networkForwardingPaths == rhs.networkForwardingPaths)||((this.networkForwardingPaths!= null)&&this.networkForwardingPaths.equals(rhs.networkForwardingPaths))))&&((this.parsingString == rhs.parsingString)||((this.parsingString!= null)&&this.parsingString.equals(rhs.parsingString))))&&((this.constraints == rhs.constraints)||((this.constraints!= null)&&this.constraints.equals(rhs.constraints))))&&((this.connections == rhs.connections)||((this.connections!= null)&&this.connections.equals(rhs.connections))));
    }

}
