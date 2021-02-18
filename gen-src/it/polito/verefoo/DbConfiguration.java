package it.polito.verefoo;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbConfiguration {
    
    protected DbFirewall firewall;
    protected DbEndhost endhost;
    protected DbEndpoint endpoint;
    protected DbAntispam antispam;
    protected DbCache cache;
    protected DbDpi dpi;
    protected DbMailclient mailclient;
    protected DbMailserver mailserver;
    protected DbNat nat;
    protected DbVpnaccess vpnaccess;
    protected DbVpnexit vpnexit;
    protected DbWebclient webclient;
    protected DbWebserver webserver;
    protected DbFieldmodifier fieldmodifier;
    protected DbForwarder forwarder;
    protected DbLoadbalancer loadbalancer;
    protected DbStatefulFirewall statefulFirewall;
    protected DbWebApplicationFirewall webApplicationFirewall;

    @Id
    @GeneratedValue
    protected Long id;

    protected String name;
    
    protected String description;

    /**
     * Gets the value of the firewall property.
     * 
     * @return
     *     possible object is
     *     {@link Firewall }
     *     
     */
    public DbFirewall getFirewall() {
        return firewall;
    }

    /**
     * Sets the value of the firewall property.
     * 
     * @param value
     *     allowed object is
     *     {@link Firewall }
     *     
     */
    public void setFirewall(DbFirewall value) {
        this.firewall = value;
    }

    /**
     * Gets the value of the endhost property.
     * 
     * @return
     *     possible object is
     *     {@link Endhost }
     *     
     */
    public DbEndhost getEndhost() {
        return endhost;
    }

    /**
     * Sets the value of the endhost property.
     * 
     * @param value
     *     allowed object is
     *     {@link Endhost }
     *     
     */
    public void setEndhost(DbEndhost value) {
        this.endhost = value;
    }

    /**
     * Gets the value of the endpoint property.
     * 
     * @return
     *     possible object is
     *     {@link Endpoint }
     *     
     */
    public DbEndpoint getEndpoint() {
        return endpoint;
    }

    /**
     * Sets the value of the endpoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link Endpoint }
     *     
     */
    public void setEndpoint(DbEndpoint value) {
        this.endpoint = value;
    }

    /**
     * Gets the value of the antispam property.
     * 
     * @return
     *     possible object is
     *     {@link Antispam }
     *     
     */
    public DbAntispam getAntispam() {
        return antispam;
    }

    /**
     * Sets the value of the antispam property.
     * 
     * @param value
     *     allowed object is
     *     {@link Antispam }
     *     
     */
    public void setAntispam(DbAntispam value) {
        this.antispam = value;
    }

    /**
     * Gets the value of the cache property.
     * 
     * @return
     *     possible object is
     *     {@link Cache }
     *     
     */
    public DbCache getCache() {
        return cache;
    }

    /**
     * Sets the value of the cache property.
     * 
     * @param value
     *     allowed object is
     *     {@link Cache }
     *     
     */
    public void setCache(DbCache value) {
        this.cache = value;
    }

    /**
     * Gets the value of the dpi property.
     * 
     * @return
     *     possible object is
     *     {@link Dpi }
     *     
     */
    public DbDpi getDpi() {
        return dpi;
    }

    /**
     * Sets the value of the dpi property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dpi }
     *     
     */
    public void setDpi(DbDpi value) {
        this.dpi = value;
    }

    /**
     * Gets the value of the mailclient property.
     * 
     * @return
     *     possible object is
     *     {@link Mailclient }
     *     
     */
    public DbMailclient getMailclient() {
        return mailclient;
    }

    /**
     * Sets the value of the mailclient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Mailclient }
     *     
     */
    public void setMailclient(DbMailclient value) {
        this.mailclient = value;
    }

    /**
     * Gets the value of the mailserver property.
     * 
     * @return
     *     possible object is
     *     {@link Mailserver }
     *     
     */
    public DbMailserver getMailserver() {
        return mailserver;
    }

    /**
     * Sets the value of the mailserver property.
     * 
     * @param value
     *     allowed object is
     *     {@link Mailserver }
     *     
     */
    public void setMailserver(DbMailserver value) {
        this.mailserver = value;
    }

    /**
     * Gets the value of the nat property.
     * 
     * @return
     *     possible object is
     *     {@link Nat }
     *     
     */
    public DbNat getNat() {
        return nat;
    }

    /**
     * Sets the value of the nat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Nat }
     *     
     */
    public void setNat(DbNat value) {
        this.nat = value;
    }

    /**
     * Gets the value of the vpnaccess property.
     * 
     * @return
     *     possible object is
     *     {@link Vpnaccess }
     *     
     */
    public DbVpnaccess getVpnaccess() {
        return vpnaccess;
    }

    /**
     * Sets the value of the vpnaccess property.
     * 
     * @param value
     *     allowed object is
     *     {@link Vpnaccess }
     *     
     */
    public void setVpnaccess(DbVpnaccess value) {
        this.vpnaccess = value;
    }

    /**
     * Gets the value of the vpnexit property.
     * 
     * @return
     *     possible object is
     *     {@link Vpnexit }
     *     
     */
    public DbVpnexit getVpnexit() {
        return vpnexit;
    }

    /**
     * Sets the value of the vpnexit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Vpnexit }
     *     
     */
    public void setVpnexit(DbVpnexit value) {
        this.vpnexit = value;
    }

    /**
     * Gets the value of the webclient property.
     * 
     * @return
     *     possible object is
     *     {@link Webclient }
     *     
     */
    public DbWebclient getWebclient() {
        return webclient;
    }

    /**
     * Sets the value of the webclient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Webclient }
     *     
     */
    public void setWebclient(DbWebclient value) {
        this.webclient = value;
    }

    /**
     * Gets the value of the webserver property.
     * 
     * @return
     *     possible object is
     *     {@link Webserver }
     *     
     */
    public DbWebserver getWebserver() {
        return webserver;
    }

    /**
     * Sets the value of the webserver property.
     * 
     * @param value
     *     allowed object is
     *     {@link Webserver }
     *     
     */
    public void setWebserver(DbWebserver value) {
        this.webserver = value;
    }

    /**
     * Gets the value of the fieldmodifier property.
     * 
     * @return
     *     possible object is
     *     {@link Fieldmodifier }
     *     
     */
    public DbFieldmodifier getFieldmodifier() {
        return fieldmodifier;
    }

    /**
     * Sets the value of the fieldmodifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link Fieldmodifier }
     *     
     */
    public void setFieldmodifier(DbFieldmodifier value) {
        this.fieldmodifier = value;
    }

    /**
     * Gets the value of the forwarder property.
     * 
     * @return
     *     possible object is
     *     {@link Forwarder }
     *     
     */
    public DbForwarder getForwarder() {
        return forwarder;
    }

    /**
     * Sets the value of the forwarder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Forwarder }
     *     
     */
    public void setForwarder(DbForwarder value) {
        this.forwarder = value;
    }

    /**
     * Gets the value of the loadbalancer property.
     * 
     * @return
     *     possible object is
     *     {@link Loadbalancer }
     *     
     */
    public DbLoadbalancer getLoadbalancer() {
        return loadbalancer;
    }

    /**
     * Sets the value of the loadbalancer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Loadbalancer }
     *     
     */
    public void setLoadbalancer(DbLoadbalancer value) {
        this.loadbalancer = value;
    }

    /**
     * Gets the value of the statefulFirewall property.
     * 
     * @return
     *     possible object is
     *     {@link StatefulFirewall }
     *     
     */
    public DbStatefulFirewall getStatefulFirewall() {
        return statefulFirewall;
    }

    /**
     * Sets the value of the statefulFirewall property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatefulFirewall }
     *     
     */
    public void setStatefulFirewall(DbStatefulFirewall value) {
        this.statefulFirewall = value;
    }

    /**
     * Gets the value of the webApplicationFirewall property.
     * 
     * @return
     *     possible object is
     *     {@link WebApplicationFirewall }
     *     
     */
    public DbWebApplicationFirewall getWebApplicationFirewall() {
        return webApplicationFirewall;
    }

    /**
     * Sets the value of the webApplicationFirewall property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebApplicationFirewall }
     *     
     */
    public void setWebApplicationFirewall(DbWebApplicationFirewall value) {
        this.webApplicationFirewall = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }
}
