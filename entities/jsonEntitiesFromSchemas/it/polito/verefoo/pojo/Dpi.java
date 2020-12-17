
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * dpi
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "dpiRules",
    "defaultAction"
})
public class Dpi
    extends NetworkFunction
{

    @JsonProperty("dpiRules")
    private List<DpiRule> dpiRules = new ArrayList<DpiRule>();
    @JsonProperty("defaultAction")
    private ActionType defaultAction;

    @JsonProperty("dpiRules")
    public List<DpiRule> getDpiRules() {
        return dpiRules;
    }

    @JsonProperty("dpiRules")
    public void setDpiRules(List<DpiRule> dpiRules) {
        this.dpiRules = dpiRules;
    }

    @JsonProperty("defaultAction")
    public ActionType getDefaultAction() {
        return defaultAction;
    }

    @JsonProperty("defaultAction")
    public void setDefaultAction(ActionType defaultAction) {
        this.defaultAction = defaultAction;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Dpi.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        int baseLength = sb.length();
        String superString = super.toString();
        if (superString!= null) {
            int contentStart = superString.indexOf('[');
            int contentEnd = superString.lastIndexOf(']');
            if ((contentStart >= 0)&&(contentEnd >contentStart)) {
                sb.append(superString, (contentStart + 1), contentEnd);
            } else {
                sb.append(superString);
            }
        }
        if (sb.length()>baseLength) {
            sb.append(',');
        }
        sb.append("dpiRules");
        sb.append('=');
        sb.append(((this.dpiRules == null)?"<null>":this.dpiRules));
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
        result = ((result* 31)+((this.dpiRules == null)? 0 :this.dpiRules.hashCode()));
        result = ((result* 31)+((this.defaultAction == null)? 0 :this.defaultAction.hashCode()));
        result = ((result* 31)+ super.hashCode());
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
        return ((super.equals(rhs)&&((this.dpiRules == rhs.dpiRules)||((this.dpiRules!= null)&&this.dpiRules.equals(rhs.dpiRules))))&&((this.defaultAction == rhs.defaultAction)||((this.defaultAction!= null)&&this.defaultAction.equals(rhs.defaultAction))));
    }

}
