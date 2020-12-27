
package it.polito.verefoo.pojo;

import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "paths"
})
public class Paths {

    @JsonProperty("paths")
    @Valid
    private Paths__1 paths;

    @JsonProperty("paths")
    public Paths__1 getPaths() {
        return paths;
    }

    @JsonProperty("paths")
    public void setPaths(Paths__1 paths) {
        this.paths = paths;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Paths.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("paths");
        sb.append('=');
        sb.append(((this.paths == null)?"<null>":this.paths));
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
        result = ((result* 31)+((this.paths == null)? 0 :this.paths.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Paths) == false) {
            return false;
        }
        Paths rhs = ((Paths) other);
        return ((this.paths == rhs.paths)||((this.paths!= null)&&this.paths.equals(rhs.paths)));
    }

}
