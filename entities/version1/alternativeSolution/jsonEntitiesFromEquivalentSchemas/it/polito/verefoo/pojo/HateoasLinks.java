
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Hyperlinks"
})
public class HateoasLinks {

    @JsonProperty("Hyperlinks")
    @Valid
    private List<Hyperlink> hyperlinks = new ArrayList<Hyperlink>();

    @JsonProperty("Hyperlinks")
    public List<Hyperlink> getHyperlinks() {
        return hyperlinks;
    }

    @JsonProperty("Hyperlinks")
    public void setHyperlinks(List<Hyperlink> hyperlinks) {
        this.hyperlinks = hyperlinks;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(HateoasLinks.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("hyperlinks");
        sb.append('=');
        sb.append(((this.hyperlinks == null)?"<null>":this.hyperlinks));
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
        result = ((result* 31)+((this.hyperlinks == null)? 0 :this.hyperlinks.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof HateoasLinks) == false) {
            return false;
        }
        HateoasLinks rhs = ((HateoasLinks) other);
        return ((this.hyperlinks == rhs.hyperlinks)||((this.hyperlinks!= null)&&this.hyperlinks.equals(rhs.hyperlinks)));
    }

}
