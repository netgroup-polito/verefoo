
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "supportedVNFTypes",
    "nodeRefs",
    "name",
    "cpu",
    "cores",
    "diskStorage",
    "memory",
    "maxVNF",
    "type",
    "fixedEndPoint",
    "active"
})
public class Host {

    @JsonProperty("supportedVNFTypes")
    @Valid
    private List<FunctionalType> supportedVNFTypes = new ArrayList<FunctionalType>();
    @JsonProperty("nodeRefs")
    @Valid
    private List<String> nodeRefs = new ArrayList<String>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    @NotNull
    private String name;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cpu")
    @DecimalMin("0")
    @NotNull
    private Integer cpu;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cores")
    @DecimalMin("0")
    @NotNull
    private Integer cores;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("diskStorage")
    @DecimalMin("0")
    @NotNull
    private Integer diskStorage;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("memory")
    @DecimalMin("0")
    @NotNull
    private Integer memory;
    @JsonProperty("maxVNF")
    @DecimalMin("0")
    private Integer maxVNF;
    @JsonProperty("type")
    private Host.Type type;
    @JsonProperty("fixedEndPoint")
    private String fixedEndPoint;
    @JsonProperty("active")
    private Boolean active = false;

    @JsonProperty("supportedVNFTypes")
    public List<FunctionalType> getSupportedVNFTypes() {
        return supportedVNFTypes;
    }

    @JsonProperty("supportedVNFTypes")
    public void setSupportedVNFTypes(List<FunctionalType> supportedVNFTypes) {
        this.supportedVNFTypes = supportedVNFTypes;
    }

    @JsonProperty("nodeRefs")
    public List<String> getNodeRefs() {
        return nodeRefs;
    }

    @JsonProperty("nodeRefs")
    public void setNodeRefs(List<String> nodeRefs) {
        this.nodeRefs = nodeRefs;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cpu")
    public Integer getCpu() {
        return cpu;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cpu")
    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cores")
    public Integer getCores() {
        return cores;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cores")
    public void setCores(Integer cores) {
        this.cores = cores;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("diskStorage")
    public Integer getDiskStorage() {
        return diskStorage;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("diskStorage")
    public void setDiskStorage(Integer diskStorage) {
        this.diskStorage = diskStorage;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("memory")
    public Integer getMemory() {
        return memory;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("memory")
    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    @JsonProperty("maxVNF")
    public Integer getMaxVNF() {
        return maxVNF;
    }

    @JsonProperty("maxVNF")
    public void setMaxVNF(Integer maxVNF) {
        this.maxVNF = maxVNF;
    }

    @JsonProperty("type")
    public Host.Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Host.Type type) {
        this.type = type;
    }

    @JsonProperty("fixedEndPoint")
    public String getFixedEndPoint() {
        return fixedEndPoint;
    }

    @JsonProperty("fixedEndPoint")
    public void setFixedEndPoint(String fixedEndPoint) {
        this.fixedEndPoint = fixedEndPoint;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Host.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("supportedVNFTypes");
        sb.append('=');
        sb.append(((this.supportedVNFTypes == null)?"<null>":this.supportedVNFTypes));
        sb.append(',');
        sb.append("nodeRefs");
        sb.append('=');
        sb.append(((this.nodeRefs == null)?"<null>":this.nodeRefs));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("cpu");
        sb.append('=');
        sb.append(((this.cpu == null)?"<null>":this.cpu));
        sb.append(',');
        sb.append("cores");
        sb.append('=');
        sb.append(((this.cores == null)?"<null>":this.cores));
        sb.append(',');
        sb.append("diskStorage");
        sb.append('=');
        sb.append(((this.diskStorage == null)?"<null>":this.diskStorage));
        sb.append(',');
        sb.append("memory");
        sb.append('=');
        sb.append(((this.memory == null)?"<null>":this.memory));
        sb.append(',');
        sb.append("maxVNF");
        sb.append('=');
        sb.append(((this.maxVNF == null)?"<null>":this.maxVNF));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null)?"<null>":this.type));
        sb.append(',');
        sb.append("fixedEndPoint");
        sb.append('=');
        sb.append(((this.fixedEndPoint == null)?"<null>":this.fixedEndPoint));
        sb.append(',');
        sb.append("active");
        sb.append('=');
        sb.append(((this.active == null)?"<null>":this.active));
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
        result = ((result* 31)+((this.supportedVNFTypes == null)? 0 :this.supportedVNFTypes.hashCode()));
        result = ((result* 31)+((this.fixedEndPoint == null)? 0 :this.fixedEndPoint.hashCode()));
        result = ((result* 31)+((this.cores == null)? 0 :this.cores.hashCode()));
        result = ((result* 31)+((this.memory == null)? 0 :this.memory.hashCode()));
        result = ((result* 31)+((this.maxVNF == null)? 0 :this.maxVNF.hashCode()));
        result = ((result* 31)+((this.diskStorage == null)? 0 :this.diskStorage.hashCode()));
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.cpu == null)? 0 :this.cpu.hashCode()));
        result = ((result* 31)+((this.active == null)? 0 :this.active.hashCode()));
        result = ((result* 31)+((this.type == null)? 0 :this.type.hashCode()));
        result = ((result* 31)+((this.nodeRefs == null)? 0 :this.nodeRefs.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Host) == false) {
            return false;
        }
        Host rhs = ((Host) other);
        return ((((((((((((this.supportedVNFTypes == rhs.supportedVNFTypes)||((this.supportedVNFTypes!= null)&&this.supportedVNFTypes.equals(rhs.supportedVNFTypes)))&&((this.fixedEndPoint == rhs.fixedEndPoint)||((this.fixedEndPoint!= null)&&this.fixedEndPoint.equals(rhs.fixedEndPoint))))&&((this.cores == rhs.cores)||((this.cores!= null)&&this.cores.equals(rhs.cores))))&&((this.memory == rhs.memory)||((this.memory!= null)&&this.memory.equals(rhs.memory))))&&((this.maxVNF == rhs.maxVNF)||((this.maxVNF!= null)&&this.maxVNF.equals(rhs.maxVNF))))&&((this.diskStorage == rhs.diskStorage)||((this.diskStorage!= null)&&this.diskStorage.equals(rhs.diskStorage))))&&((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name))))&&((this.cpu == rhs.cpu)||((this.cpu!= null)&&this.cpu.equals(rhs.cpu))))&&((this.active == rhs.active)||((this.active!= null)&&this.active.equals(rhs.active))))&&((this.type == rhs.type)||((this.type!= null)&&this.type.equals(rhs.type))))&&((this.nodeRefs == rhs.nodeRefs)||((this.nodeRefs!= null)&&this.nodeRefs.equals(rhs.nodeRefs))));
    }

    public enum Type {

        CLIENT("CLIENT"),
        SERVER("SERVER"),
        MIDDLEBOX("MIDDLEBOX");
        private final String value;
        private final static Map<String, Host.Type> CONSTANTS = new HashMap<String, Host.Type>();

        static {
            for (Host.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Type(String value) {
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
        public static Host.Type fromValue(String value) {
            Host.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
