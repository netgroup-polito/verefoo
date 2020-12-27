
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
    "elements",
    "defaultAction"
})
public class Dpi {

    @JsonProperty("elements")
    @Size(min = 1)
    @Valid
    private List<DpiElements> elements = new ArrayList<DpiElements>();
    @JsonProperty("defaultAction")
    private ActionTypes defaultAction;

    @JsonProperty("elements")
    public List<DpiElements> getElements() {
        return elements;
    }

    @JsonProperty("elements")
    public void setElements(List<DpiElements> elements) {
        this.elements = elements;
    }

    @JsonProperty("defaultAction")
    public ActionTypes getDefaultAction() {
        return defaultAction;
    }

    @JsonProperty("defaultAction")
    public void setDefaultAction(ActionTypes defaultAction) {
        this.defaultAction = defaultAction;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Dpi.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("elements");
        sb.append('=');
        sb.append(((this.elements == null)?"<null>":this.elements));
        sb.append(',');
        sb.append("defaultAction");
        sb.append('=');
        sb.append(((this.defaultAction == null)?"<null>":this.defaultAction));
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
        result = ((result* 31)+((this.elements == null)? 0 :this.elements.hashCode()));
        result = ((result* 31)+((this.defaultAction == null)? 0 :this.defaultAction.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Dpi) == false) {
            return false;
        }
        Dpi rhs = ((Dpi) other);
        return (((this.elements == rhs.elements)||((this.elements!= null)&&this.elements.equals(rhs.elements)))&&((this.defaultAction == rhs.defaultAction)||((this.defaultAction!= null)&&this.defaultAction.equals(rhs.defaultAction))));
    }

}
