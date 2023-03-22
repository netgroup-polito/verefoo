package it.polito.verefoo;

public enum DbFunctionalTypes {

    FIREWALL,
    ENDHOST,
    ENDPOINT,
    ANTISPAM,
    CACHE,
    DPI,
    DPI_S,
    MAILCLIENT,
    MAILSERVER,
    NAT,
    VPNACCESS,
    VPNEXIT,
    WEBCLIENT,
    WEBSERVER,
    FIELDMODIFIER,
    FORWARDER,
    LOADBALANCER,
    STATEFUL_FIREWALL,
    PRIORITY_FIREWALL,
    WEB_APPLICATION_FIREWALL,
    TRAFFIC_MONITOR;

    public String value() {
        return name();
    }

    public static DbFunctionalTypes fromValue(String v) {
        return valueOf(v);
    }

}
