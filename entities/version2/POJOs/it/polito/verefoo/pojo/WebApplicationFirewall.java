
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * webApplicationFirewall
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "webApplicationRules",
    "defaultAction"
})
public class WebApplicationFirewall
    extends NetworkFunction
{

    @JsonProperty("webApplicationRules")
    @Valid
    private List<WebApplicationRule> webApplicationRules = new ArrayList<WebApplicationRule>();
    @JsonProperty("defaultAction")
    private ActionType defaultAction;

    @JsonProperty("webApplicationRules")
    public List<WebApplicationRule> getWebApplicationRules() {
        return webApplicationRules;
    }

    @JsonProperty("webApplicationRules")
    public void setWebApplicationRules(List<WebApplicationRule> webApplicationRules) {
        this.webApplicationRules = webApplicationRules;
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
        sb.append(WebApplicationFirewall.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        sb.append("webApplicationRules");
        sb.append('=');
        sb.append(((this.webApplicationRules == null)?"<null>":this.webApplicationRules));
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
        result = ((result* 31)+((this.webApplicationRules == null)? 0 :this.webApplicationRules.hashCode()));
        result = ((result* 31)+((this.defaultAction == null)? 0 :this.defaultAction.hashCode()));
        result = ((result* 31)+ super.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof WebApplicationFirewall) == false) {
            return false;
        }
        WebApplicationFirewall rhs = ((WebApplicationFirewall) other);
        return ((super.equals(rhs)&&((this.webApplicationRules == rhs.webApplicationRules)||((this.webApplicationRules!= null)&&this.webApplicationRules.equals(rhs.webApplicationRules))))&&((this.defaultAction == rhs.defaultAction)||((this.defaultAction!= null)&&this.defaultAction.equals(rhs.defaultAction))));
    }

}
