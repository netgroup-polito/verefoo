
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * firewall
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "rules",
    "defaultAction"
})
public class Firewall
    extends NetworkFunction
{

    @JsonProperty("rules")
    @Size(min = 1)
    @Valid
    private List<Rule> rules = new ArrayList<Rule>();
    @JsonProperty("defaultAction")
    private ActionType defaultAction;

    @JsonProperty("rules")
    public List<Rule> getRules() {
        return rules;
    }

    @JsonProperty("rules")
    public void setRules(List<Rule> rules) {
        this.rules = rules;
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
        sb.append(Firewall.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        sb.append("rules");
        sb.append('=');
        sb.append(((this.rules == null)?"<null>":this.rules));
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
        result = ((result* 31)+((this.rules == null)? 0 :this.rules.hashCode()));
        result = ((result* 31)+((this.defaultAction == null)? 0 :this.defaultAction.hashCode()));
        result = ((result* 31)+ super.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Firewall) == false) {
            return false;
        }
        Firewall rhs = ((Firewall) other);
        return ((super.equals(rhs)&&((this.rules == rhs.rules)||((this.rules!= null)&&this.rules.equals(rhs.rules))))&&((this.defaultAction == rhs.defaultAction)||((this.defaultAction!= null)&&this.defaultAction.equals(rhs.defaultAction))));
    }

}
