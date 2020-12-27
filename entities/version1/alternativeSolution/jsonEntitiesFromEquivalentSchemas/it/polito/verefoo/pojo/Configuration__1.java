
package it.polito.verefoo.pojo;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "description",
    "firewall",
    "endhost",
    "endpoint",
    "antispam",
    "cache",
    "dpi",
    "mailclient",
    "mailserver",
    "nat",
    "vpnaccess",
    "vpnexit",
    "webclient",
    "webserver",
    "fieldmodifier",
    "forwarder",
    "loadbalancer",
    "stateful_firewall",
    "web_application_firewall"
})
public class Configuration__1 {

    @JsonProperty("id")
    private Long id;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    @NotNull
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("firewall")
    private Firewall firewall;
    @JsonProperty("endhost")
    private Endhost endhost;
    @JsonProperty("endpoint")
    private Endpoint endpoint;
    @JsonProperty("antispam")
    private Antispam antispam;
    @JsonProperty("cache")
    private Cache cache;
    @JsonProperty("dpi")
    private Dpi dpi;
    @JsonProperty("mailclient")
    private Mailclient mailclient;
    @JsonProperty("mailserver")
    private Mailserver mailserver;
    @JsonProperty("nat")
    private Nat nat;
    @JsonProperty("vpnaccess")
    private Vpnaccess vpnaccess;
    @JsonProperty("vpnexit")
    private Vpnexit vpnexit;
    @JsonProperty("webclient")
    private Webclient webclient;
    @JsonProperty("webserver")
    private Webserver webserver;
    @JsonProperty("fieldmodifier")
    private Fieldmodifier fieldmodifier;
    @JsonProperty("forwarder")
    private Forwarder forwarder;
    @JsonProperty("loadbalancer")
    private Loadbalancer loadbalancer;
    @JsonProperty("stateful_firewall")
    private StatefulFirewall statefulFirewall;
    @JsonProperty("web_application_firewall")
    private WebApplicationFirewall webApplicationFirewall;

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
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

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("firewall")
    public Firewall getFirewall() {
        return firewall;
    }

    @JsonProperty("firewall")
    public void setFirewall(Firewall firewall) {
        this.firewall = firewall;
    }

    @JsonProperty("endhost")
    public Endhost getEndhost() {
        return endhost;
    }

    @JsonProperty("endhost")
    public void setEndhost(Endhost endhost) {
        this.endhost = endhost;
    }

    @JsonProperty("endpoint")
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @JsonProperty("endpoint")
    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @JsonProperty("antispam")
    public Antispam getAntispam() {
        return antispam;
    }

    @JsonProperty("antispam")
    public void setAntispam(Antispam antispam) {
        this.antispam = antispam;
    }

    @JsonProperty("cache")
    public Cache getCache() {
        return cache;
    }

    @JsonProperty("cache")
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @JsonProperty("dpi")
    public Dpi getDpi() {
        return dpi;
    }

    @JsonProperty("dpi")
    public void setDpi(Dpi dpi) {
        this.dpi = dpi;
    }

    @JsonProperty("mailclient")
    public Mailclient getMailclient() {
        return mailclient;
    }

    @JsonProperty("mailclient")
    public void setMailclient(Mailclient mailclient) {
        this.mailclient = mailclient;
    }

    @JsonProperty("mailserver")
    public Mailserver getMailserver() {
        return mailserver;
    }

    @JsonProperty("mailserver")
    public void setMailserver(Mailserver mailserver) {
        this.mailserver = mailserver;
    }

    @JsonProperty("nat")
    public Nat getNat() {
        return nat;
    }

    @JsonProperty("nat")
    public void setNat(Nat nat) {
        this.nat = nat;
    }

    @JsonProperty("vpnaccess")
    public Vpnaccess getVpnaccess() {
        return vpnaccess;
    }

    @JsonProperty("vpnaccess")
    public void setVpnaccess(Vpnaccess vpnaccess) {
        this.vpnaccess = vpnaccess;
    }

    @JsonProperty("vpnexit")
    public Vpnexit getVpnexit() {
        return vpnexit;
    }

    @JsonProperty("vpnexit")
    public void setVpnexit(Vpnexit vpnexit) {
        this.vpnexit = vpnexit;
    }

    @JsonProperty("webclient")
    public Webclient getWebclient() {
        return webclient;
    }

    @JsonProperty("webclient")
    public void setWebclient(Webclient webclient) {
        this.webclient = webclient;
    }

    @JsonProperty("webserver")
    public Webserver getWebserver() {
        return webserver;
    }

    @JsonProperty("webserver")
    public void setWebserver(Webserver webserver) {
        this.webserver = webserver;
    }

    @JsonProperty("fieldmodifier")
    public Fieldmodifier getFieldmodifier() {
        return fieldmodifier;
    }

    @JsonProperty("fieldmodifier")
    public void setFieldmodifier(Fieldmodifier fieldmodifier) {
        this.fieldmodifier = fieldmodifier;
    }

    @JsonProperty("forwarder")
    public Forwarder getForwarder() {
        return forwarder;
    }

    @JsonProperty("forwarder")
    public void setForwarder(Forwarder forwarder) {
        this.forwarder = forwarder;
    }

    @JsonProperty("loadbalancer")
    public Loadbalancer getLoadbalancer() {
        return loadbalancer;
    }

    @JsonProperty("loadbalancer")
    public void setLoadbalancer(Loadbalancer loadbalancer) {
        this.loadbalancer = loadbalancer;
    }

    @JsonProperty("stateful_firewall")
    public StatefulFirewall getStatefulFirewall() {
        return statefulFirewall;
    }

    @JsonProperty("stateful_firewall")
    public void setStatefulFirewall(StatefulFirewall statefulFirewall) {
        this.statefulFirewall = statefulFirewall;
    }

    @JsonProperty("web_application_firewall")
    public WebApplicationFirewall getWebApplicationFirewall() {
        return webApplicationFirewall;
    }

    @JsonProperty("web_application_firewall")
    public void setWebApplicationFirewall(WebApplicationFirewall webApplicationFirewall) {
        this.webApplicationFirewall = webApplicationFirewall;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Configuration__1 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("firewall");
        sb.append('=');
        sb.append(((this.firewall == null)?"<null>":this.firewall));
        sb.append(',');
        sb.append("endhost");
        sb.append('=');
        sb.append(((this.endhost == null)?"<null>":this.endhost));
        sb.append(',');
        sb.append("endpoint");
        sb.append('=');
        sb.append(((this.endpoint == null)?"<null>":this.endpoint));
        sb.append(',');
        sb.append("antispam");
        sb.append('=');
        sb.append(((this.antispam == null)?"<null>":this.antispam));
        sb.append(',');
        sb.append("cache");
        sb.append('=');
        sb.append(((this.cache == null)?"<null>":this.cache));
        sb.append(',');
        sb.append("dpi");
        sb.append('=');
        sb.append(((this.dpi == null)?"<null>":this.dpi));
        sb.append(',');
        sb.append("mailclient");
        sb.append('=');
        sb.append(((this.mailclient == null)?"<null>":this.mailclient));
        sb.append(',');
        sb.append("mailserver");
        sb.append('=');
        sb.append(((this.mailserver == null)?"<null>":this.mailserver));
        sb.append(',');
        sb.append("nat");
        sb.append('=');
        sb.append(((this.nat == null)?"<null>":this.nat));
        sb.append(',');
        sb.append("vpnaccess");
        sb.append('=');
        sb.append(((this.vpnaccess == null)?"<null>":this.vpnaccess));
        sb.append(',');
        sb.append("vpnexit");
        sb.append('=');
        sb.append(((this.vpnexit == null)?"<null>":this.vpnexit));
        sb.append(',');
        sb.append("webclient");
        sb.append('=');
        sb.append(((this.webclient == null)?"<null>":this.webclient));
        sb.append(',');
        sb.append("webserver");
        sb.append('=');
        sb.append(((this.webserver == null)?"<null>":this.webserver));
        sb.append(',');
        sb.append("fieldmodifier");
        sb.append('=');
        sb.append(((this.fieldmodifier == null)?"<null>":this.fieldmodifier));
        sb.append(',');
        sb.append("forwarder");
        sb.append('=');
        sb.append(((this.forwarder == null)?"<null>":this.forwarder));
        sb.append(',');
        sb.append("loadbalancer");
        sb.append('=');
        sb.append(((this.loadbalancer == null)?"<null>":this.loadbalancer));
        sb.append(',');
        sb.append("statefulFirewall");
        sb.append('=');
        sb.append(((this.statefulFirewall == null)?"<null>":this.statefulFirewall));
        sb.append(',');
        sb.append("webApplicationFirewall");
        sb.append('=');
        sb.append(((this.webApplicationFirewall == null)?"<null>":this.webApplicationFirewall));
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
        result = ((result* 31)+((this.mailclient == null)? 0 :this.mailclient.hashCode()));
        result = ((result* 31)+((this.nat == null)? 0 :this.nat.hashCode()));
        result = ((result* 31)+((this.cache == null)? 0 :this.cache.hashCode()));
        result = ((result* 31)+((this.fieldmodifier == null)? 0 :this.fieldmodifier.hashCode()));
        result = ((result* 31)+((this.antispam == null)? 0 :this.antispam.hashCode()));
        result = ((result* 31)+((this.webclient == null)? 0 :this.webclient.hashCode()));
        result = ((result* 31)+((this.description == null)? 0 :this.description.hashCode()));
        result = ((result* 31)+((this.vpnexit == null)? 0 :this.vpnexit.hashCode()));
        result = ((result* 31)+((this.mailserver == null)? 0 :this.mailserver.hashCode()));
        result = ((result* 31)+((this.webApplicationFirewall == null)? 0 :this.webApplicationFirewall.hashCode()));
        result = ((result* 31)+((this.endpoint == null)? 0 :this.endpoint.hashCode()));
        result = ((result* 31)+((this.loadbalancer == null)? 0 :this.loadbalancer.hashCode()));
        result = ((result* 31)+((this.firewall == null)? 0 :this.firewall.hashCode()));
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.forwarder == null)? 0 :this.forwarder.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.vpnaccess == null)? 0 :this.vpnaccess.hashCode()));
        result = ((result* 31)+((this.dpi == null)? 0 :this.dpi.hashCode()));
        result = ((result* 31)+((this.webserver == null)? 0 :this.webserver.hashCode()));
        result = ((result* 31)+((this.statefulFirewall == null)? 0 :this.statefulFirewall.hashCode()));
        result = ((result* 31)+((this.endhost == null)? 0 :this.endhost.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Configuration__1) == false) {
            return false;
        }
        Configuration__1 rhs = ((Configuration__1) other);
        return ((((((((((((((((((((((this.mailclient == rhs.mailclient)||((this.mailclient!= null)&&this.mailclient.equals(rhs.mailclient)))&&((this.nat == rhs.nat)||((this.nat!= null)&&this.nat.equals(rhs.nat))))&&((this.cache == rhs.cache)||((this.cache!= null)&&this.cache.equals(rhs.cache))))&&((this.fieldmodifier == rhs.fieldmodifier)||((this.fieldmodifier!= null)&&this.fieldmodifier.equals(rhs.fieldmodifier))))&&((this.antispam == rhs.antispam)||((this.antispam!= null)&&this.antispam.equals(rhs.antispam))))&&((this.webclient == rhs.webclient)||((this.webclient!= null)&&this.webclient.equals(rhs.webclient))))&&((this.description == rhs.description)||((this.description!= null)&&this.description.equals(rhs.description))))&&((this.vpnexit == rhs.vpnexit)||((this.vpnexit!= null)&&this.vpnexit.equals(rhs.vpnexit))))&&((this.mailserver == rhs.mailserver)||((this.mailserver!= null)&&this.mailserver.equals(rhs.mailserver))))&&((this.webApplicationFirewall == rhs.webApplicationFirewall)||((this.webApplicationFirewall!= null)&&this.webApplicationFirewall.equals(rhs.webApplicationFirewall))))&&((this.endpoint == rhs.endpoint)||((this.endpoint!= null)&&this.endpoint.equals(rhs.endpoint))))&&((this.loadbalancer == rhs.loadbalancer)||((this.loadbalancer!= null)&&this.loadbalancer.equals(rhs.loadbalancer))))&&((this.firewall == rhs.firewall)||((this.firewall!= null)&&this.firewall.equals(rhs.firewall))))&&((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name))))&&((this.forwarder == rhs.forwarder)||((this.forwarder!= null)&&this.forwarder.equals(rhs.forwarder))))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.vpnaccess == rhs.vpnaccess)||((this.vpnaccess!= null)&&this.vpnaccess.equals(rhs.vpnaccess))))&&((this.dpi == rhs.dpi)||((this.dpi!= null)&&this.dpi.equals(rhs.dpi))))&&((this.webserver == rhs.webserver)||((this.webserver!= null)&&this.webserver.equals(rhs.webserver))))&&((this.statefulFirewall == rhs.statefulFirewall)||((this.statefulFirewall!= null)&&this.statefulFirewall.equals(rhs.statefulFirewall))))&&((this.endhost == rhs.endhost)||((this.endhost!= null)&&this.endhost.equals(rhs.endhost))));
    }

}
