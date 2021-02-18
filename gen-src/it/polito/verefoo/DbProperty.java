package it.polito.verefoo;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbProperty {

    @Id
    @GeneratedValue
    Long id;

    protected DbHTTPDefinition httpDefinition;

    protected DbPOP3Definition pop3Definition;

    protected DbPName name;

    protected long graph;

    protected String src;

    protected String dst;

    protected DbL4ProtocolTypes lv4Proto;

    protected String srcPort;

    protected String dstPort;

    protected Boolean isSat;

    protected String body;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the value of the httpDefinition property.
     * 
     * @return
     *     possible object is
     *     {@link HTTPDefinition }
     *     
     */
    public DbHTTPDefinition getHTTPDefinition() {
        return httpDefinition;
    }

    /**
     * Sets the value of the httpDefinition property.
     * 
     * @param value
     *     allowed object is
     *     {@link HTTPDefinition }
     *     
     */
    public void setHTTPDefinition(DbHTTPDefinition value) {
        this.httpDefinition = value;
    }

    /**
     * Gets the value of the pop3Definition property.
     * 
     * @return
     *     possible object is
     *     {@link POP3Definition }
     *     
     */
    public DbPOP3Definition getPOP3Definition() {
        return pop3Definition;
    }

    /**
     * Sets the value of the pop3Definition property.
     * 
     * @param value
     *     allowed object is
     *     {@link POP3Definition }
     *     
     */
    public void setPOP3Definition(DbPOP3Definition value) {
        this.pop3Definition = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link PName }
     *     
     */
    public DbPName getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link PName }
     *     
     */
    public void setName(DbPName value) {
        this.name = value;
    }

    /**
     * Gets the value of the graph property.
     * 
     */
    public long getGraph() {
        return graph;
    }

    /**
     * Sets the value of the graph property.
     * 
     */
    public void setGraph(long value) {
        this.graph = value;
    }

    /**
     * Gets the value of the src property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrc() {
        return src;
    }

    /**
     * Sets the value of the src property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrc(String value) {
        this.src = value;
    }

    /**
     * Gets the value of the dst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDst() {
        return dst;
    }

    /**
     * Sets the value of the dst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDst(String value) {
        this.dst = value;
    }

    /**
     * Gets the value of the lv4Proto property.
     * 
     * @return
     *     possible object is
     *     {@link L4ProtocolTypes }
     *     
     */
    public DbL4ProtocolTypes getLv4Proto() {
        if (lv4Proto == null) {
            return DbL4ProtocolTypes.ANY;
        } else {
            return lv4Proto;
        }
    }

    /**
     * Sets the value of the lv4Proto property.
     * 
     * @param value
     *     allowed object is
     *     {@link L4ProtocolTypes }
     *     
     */
    public void setLv4Proto(DbL4ProtocolTypes value) {
        this.lv4Proto = value;
    }

    /**
     * Gets the value of the srcPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrcPort() {
        return srcPort;
    }

    /**
     * Sets the value of the srcPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrcPort(String value) {
        this.srcPort = value;
    }

    /**
     * Gets the value of the dstPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDstPort() {
        return dstPort;
    }

    /**
     * Sets the value of the dstPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDstPort(String value) {
        this.dstPort = value;
    }

    /**
     * Gets the value of the isSat property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsSat() {
        return isSat;
    }

    /**
     * Sets the value of the isSat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsSat(Boolean value) {
        this.isSat = value;
    }

    /**
     * Gets the value of the body property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the value of the body property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBody(String value) {
        this.body = value;
    }

}
