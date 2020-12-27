
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pool"
})
public class Loadbalancer {

    @JsonProperty("pool")
    @Size(min = 1)
    @Valid
    private List<String> pool = new ArrayList<String>();

    @JsonProperty("pool")
    public List<String> getPool() {
        return pool;
    }

    @JsonProperty("pool")
    public void setPool(List<String> pool) {
        this.pool = pool;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Loadbalancer.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("pool");
        sb.append('=');
        sb.append(((this.pool == null)?"<null>":this.pool));
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
        result = ((result* 31)+((this.pool == null)? 0 :this.pool.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Loadbalancer) == false) {
            return false;
        }
        Loadbalancer rhs = ((Loadbalancer) other);
        return ((this.pool == rhs.pool)||((this.pool!= null)&&this.pool.equals(rhs.pool)));
    }

}
