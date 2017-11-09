 package it.polito.verifoo.components;


import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.microsoft.z3.BoolExpr;


/**
 * <p>Java class for Node complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Node">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="[a-zA-Z][a-zA-Z\d]*"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="functionType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nffg" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Node")
public class Node {

    protected String name;
    protected VNFType functionType;
    protected String nffg;
    
    protected BoolExpr bool;
    
    protected ArrayList<BoolExpr> hosts;

    public ArrayList<BoolExpr> getHosts() {
		return hosts;
	}

	public void setHosts(ArrayList<BoolExpr> hosts) {
		this.hosts = hosts;
	}

	public Node(String string, int i, VNFType vnf) {
		this.setName(string);
		this.setDisk(i);
		this.setFunctionType(vnf);
		hosts= new ArrayList<>();
	}

	public enum VNFType{
		CACHE,
		DPI,
		FW,
		NAT,
		SPAM,
		VPN;
	}
	
	public BoolExpr getBool() {
		return bool;
	}

	public void setBool(BoolExpr bool) {
		this.bool = bool;
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
     * Gets the value of the functionType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public VNFType getFunctionType() {
        return functionType;
    }

    /**
     * Sets the value of the functionType property.
     * 
     * @param vnf
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFunctionType(VNFType vnf) {
        this.functionType = vnf;
    }

    /**
     * Gets the value of the nffg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNffg() {
        return nffg;
    }

    /**
     * Sets the value of the nffg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNffg(String value) {
        this.nffg = value;
    }
    protected Integer disk;
   public Integer getDisk() {
       return disk;
   }

   public void setDisk(Integer value) {
       this.disk = value;
   }

}
