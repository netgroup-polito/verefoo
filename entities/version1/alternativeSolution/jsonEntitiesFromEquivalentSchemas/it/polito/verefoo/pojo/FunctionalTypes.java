
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FunctionalTypes {

    FIREWALL("FIREWALL"),
    ENDHOST("ENDHOST"),
    ENDPOINT("ENDPOINT"),
    ANTISPAM("ANTISPAM"),
    CACHE("CACHE"),
    DPI("DPI"),
    DPI_S("DPI_S"),
    MAILCLIENT("MAILCLIENT"),
    MAILSERVER("MAILSERVER"),
    NAT("NAT"),
    VPNACCESS("VPNACCESS"),
    VPNEXIT("VPNEXIT"),
    WEBCLIENT("WEBCLIENT"),
    WEBSERVER("WEBSERVER"),
    FIELDMODIFIER("FIELDMODIFIER"),
    FORWARDER("FORWARDER"),
    LOADBALANCER("LOADBALANCER"),
    STATEFUL_FIREWALL("STATEFUL_FIREWALL"),
    PRIORITY_FIREWALL("PRIORITY_FIREWALL"),
    WEB_APPLICATION_FIREWALL("WEB_APPLICATION_FIREWALL"),
    TRAFFIC_MONITOR("TRAFFIC_MONITOR");
    private final String value;
    private final static Map<String, FunctionalTypes> CONSTANTS = new HashMap<String, FunctionalTypes>();

    static {
        for (FunctionalTypes c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private FunctionalTypes(String value) {
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
    public static FunctionalTypes fromValue(String value) {
        FunctionalTypes constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
