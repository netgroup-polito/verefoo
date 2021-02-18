package it.polito.verefoo;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

public class DbHost {

    @Id
    @GeneratedValue
    Long id;

    protected List<DbSupportedVNFType> supportedVNF;

    protected List<DbNodeRefType> nodeRef;

    protected String name;

    protected int cpu;

    protected int cores;

    protected int diskStorage;

    protected int memory;

    protected Integer maxVNF;

    protected DbTypeOfHost type;

    protected String fixedEndpoint;

    protected Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the value of the supportedVNF property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedVNF property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedVNF().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupportedVNFType }
     * 
     * 
     */
    public List<DbSupportedVNFType> getSupportedVNF() {
        if (supportedVNF == null) {
            supportedVNF = new ArrayList<DbSupportedVNFType>();
        }
        return this.supportedVNF;
    }

    /**
     * Gets the value of the nodeRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodeRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NodeRefType }
     * 
     * 
     */
    public List<DbNodeRefType> getNodeRef() {
        if (nodeRef == null) {
            nodeRef = new ArrayList<DbNodeRefType>();
        }
        return this.nodeRef;
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
     * Gets the value of the cpu property.
     * 
     */
    public int getCpu() {
        return cpu;
    }

    /**
     * Sets the value of the cpu property.
     * 
     */
    public void setCpu(int value) {
        this.cpu = value;
    }

    /**
     * Gets the value of the cores property.
     * 
     */
    public int getCores() {
        return cores;
    }

    /**
     * Sets the value of the cores property.
     * 
     */
    public void setCores(int value) {
        this.cores = value;
    }

    /**
     * Gets the value of the diskStorage property.
     * 
     */
    public int getDiskStorage() {
        return diskStorage;
    }

    /**
     * Sets the value of the diskStorage property.
     * 
     */
    public void setDiskStorage(int value) {
        this.diskStorage = value;
    }

    /**
     * Gets the value of the memory property.
     * 
     */
    public int getMemory() {
        return memory;
    }

    /**
     * Sets the value of the memory property.
     * 
     */
    public void setMemory(int value) {
        this.memory = value;
    }

    /**
     * Gets the value of the maxVNF property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxVNF() {
        return maxVNF;
    }

    /**
     * Sets the value of the maxVNF property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxVNF(Integer value) {
        this.maxVNF = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link TypeOfHost }
     *     
     */
    public DbTypeOfHost getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeOfHost }
     *     
     */
    public void setType(DbTypeOfHost value) {
        this.type = value;
    }

    /**
     * Gets the value of the fixedEndpoint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFixedEndpoint() {
        return fixedEndpoint;
    }

    /**
     * Sets the value of the fixedEndpoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFixedEndpoint(String value) {
        this.fixedEndpoint = value;
    }

    /**
     * Gets the value of the active property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isActive() {
        if (active == null) {
            return false;
        } else {
            return active;
        }
    }

    /**
     * Sets the value of the active property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setActive(Boolean value) {
        this.active = value;
    }

}
