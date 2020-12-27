
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "source",
    "destination",
    "levelFourProtocol",
    "sourcePort",
    "destinationPort",
    "isSatisfied",
    "body",
    "definition"
})
public class Requirement {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    @NotNull
    private Requirement.Name name;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("source")
    @NotNull
    private String source;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("destination")
    @NotNull
    private String destination;
    @JsonProperty("levelFourProtocol")
    private LevelFourProtocolType levelFourProtocol = null;
    @JsonProperty("sourcePort")
    private String sourcePort;
    @JsonProperty("destinationPort")
    private String destinationPort;
    @JsonProperty("isSatisfied")
    private Boolean isSatisfied;
    @JsonProperty("body")
    private String body;
    @JsonProperty("definition")
    private DefinitionType definition;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public Requirement.Name getName() {
        return name;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public void setName(Requirement.Name name) {
        this.name = name;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("destination")
    public String getDestination() {
        return destination;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("destination")
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @JsonProperty("levelFourProtocol")
    public LevelFourProtocolType getLevelFourProtocol() {
        return levelFourProtocol;
    }

    @JsonProperty("levelFourProtocol")
    public void setLevelFourProtocol(LevelFourProtocolType levelFourProtocol) {
        this.levelFourProtocol = levelFourProtocol;
    }

    @JsonProperty("sourcePort")
    public String getSourcePort() {
        return sourcePort;
    }

    @JsonProperty("sourcePort")
    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    @JsonProperty("destinationPort")
    public String getDestinationPort() {
        return destinationPort;
    }

    @JsonProperty("destinationPort")
    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    @JsonProperty("isSatisfied")
    public Boolean getIsSatisfied() {
        return isSatisfied;
    }

    @JsonProperty("isSatisfied")
    public void setIsSatisfied(Boolean isSatisfied) {
        this.isSatisfied = isSatisfied;
    }

    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty("definition")
    public DefinitionType getDefinition() {
        return definition;
    }

    @JsonProperty("definition")
    public void setDefinition(DefinitionType definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Requirement.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("source");
        sb.append('=');
        sb.append(((this.source == null)?"<null>":this.source));
        sb.append(',');
        sb.append("destination");
        sb.append('=');
        sb.append(((this.destination == null)?"<null>":this.destination));
        sb.append(',');
        sb.append("levelFourProtocol");
        sb.append('=');
        sb.append(((this.levelFourProtocol == null)?"<null>":this.levelFourProtocol));
        sb.append(',');
        sb.append("sourcePort");
        sb.append('=');
        sb.append(((this.sourcePort == null)?"<null>":this.sourcePort));
        sb.append(',');
        sb.append("destinationPort");
        sb.append('=');
        sb.append(((this.destinationPort == null)?"<null>":this.destinationPort));
        sb.append(',');
        sb.append("isSatisfied");
        sb.append('=');
        sb.append(((this.isSatisfied == null)?"<null>":this.isSatisfied));
        sb.append(',');
        sb.append("body");
        sb.append('=');
        sb.append(((this.body == null)?"<null>":this.body));
        sb.append(',');
        sb.append("definition");
        sb.append('=');
        sb.append(((this.definition == null)?"<null>":this.definition));
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
        result = ((result* 31)+((this.destinationPort == null)? 0 :this.destinationPort.hashCode()));
        result = ((result* 31)+((this.sourcePort == null)? 0 :this.sourcePort.hashCode()));
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.destination == null)? 0 :this.destination.hashCode()));
        result = ((result* 31)+((this.isSatisfied == null)? 0 :this.isSatisfied.hashCode()));
        result = ((result* 31)+((this.definition == null)? 0 :this.definition.hashCode()));
        result = ((result* 31)+((this.source == null)? 0 :this.source.hashCode()));
        result = ((result* 31)+((this.body == null)? 0 :this.body.hashCode()));
        result = ((result* 31)+((this.levelFourProtocol == null)? 0 :this.levelFourProtocol.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Requirement) == false) {
            return false;
        }
        Requirement rhs = ((Requirement) other);
        return ((((((((((this.destinationPort == rhs.destinationPort)||((this.destinationPort!= null)&&this.destinationPort.equals(rhs.destinationPort)))&&((this.sourcePort == rhs.sourcePort)||((this.sourcePort!= null)&&this.sourcePort.equals(rhs.sourcePort))))&&((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name))))&&((this.destination == rhs.destination)||((this.destination!= null)&&this.destination.equals(rhs.destination))))&&((this.isSatisfied == rhs.isSatisfied)||((this.isSatisfied!= null)&&this.isSatisfied.equals(rhs.isSatisfied))))&&((this.definition == rhs.definition)||((this.definition!= null)&&this.definition.equals(rhs.definition))))&&((this.source == rhs.source)||((this.source!= null)&&this.source.equals(rhs.source))))&&((this.body == rhs.body)||((this.body!= null)&&this.body.equals(rhs.body))))&&((this.levelFourProtocol == rhs.levelFourProtocol)||((this.levelFourProtocol!= null)&&this.levelFourProtocol.equals(rhs.levelFourProtocol))));
    }

    public enum Name {

        ISOLATION("Isolation"),
        REACHABILITY("Reachability");
        private final String value;
        private final static Map<String, Requirement.Name> CONSTANTS = new HashMap<String, Requirement.Name>();

        static {
            for (Requirement.Name c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Name(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Requirement.Name fromValue(String value) {
            Requirement.Name constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
