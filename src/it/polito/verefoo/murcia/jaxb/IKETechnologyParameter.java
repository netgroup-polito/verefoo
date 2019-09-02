//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.09.02 alle 08:15:42 PM CEST 
//


package it.polito.verefoo.murcia.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per IKETechnologyParameter complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="IKETechnologyParameter"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}TechnologySpecificParameters"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="phase2_pfs_group" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="exchangeMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="phase1_dh_group" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="phase2_compression_algorithm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="hash_algorithm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ESN" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="encryptionAlgorithm" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="lifetime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="rekey_margin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="keyring_tries" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MOBIKE" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IKETechnologyParameter", propOrder = {
    "phase2PfsGroup",
    "exchangeMode",
    "phase1DhGroup",
    "phase2CompressionAlgorithm",
    "hashAlgorithm",
    "esn",
    "encryptionAlgorithm",
    "lifetime",
    "rekeyMargin",
    "keyringTries",
    "mobike"
})
public class IKETechnologyParameter
    extends TechnologySpecificParameters
{

    @XmlElement(name = "phase2_pfs_group")
    protected String phase2PfsGroup;
    protected String exchangeMode;
    @XmlElement(name = "phase1_dh_group")
    protected String phase1DhGroup;
    @XmlElement(name = "phase2_compression_algorithm")
    protected String phase2CompressionAlgorithm;
    @XmlElement(name = "hash_algorithm")
    protected String hashAlgorithm;
    @XmlElement(name = "ESN")
    protected Boolean esn;
    @XmlElement(required = true)
    protected String encryptionAlgorithm;
    protected String lifetime;
    @XmlElement(name = "rekey_margin")
    protected String rekeyMargin;
    @XmlElement(name = "keyring_tries")
    protected String keyringTries;
    @XmlElement(name = "MOBIKE")
    protected Boolean mobike;

    /**
     * Recupera il valore della proprietà phase2PfsGroup.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhase2PfsGroup() {
        return phase2PfsGroup;
    }

    /**
     * Imposta il valore della proprietà phase2PfsGroup.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhase2PfsGroup(String value) {
        this.phase2PfsGroup = value;
    }

    /**
     * Recupera il valore della proprietà exchangeMode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExchangeMode() {
        return exchangeMode;
    }

    /**
     * Imposta il valore della proprietà exchangeMode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExchangeMode(String value) {
        this.exchangeMode = value;
    }

    /**
     * Recupera il valore della proprietà phase1DhGroup.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhase1DhGroup() {
        return phase1DhGroup;
    }

    /**
     * Imposta il valore della proprietà phase1DhGroup.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhase1DhGroup(String value) {
        this.phase1DhGroup = value;
    }

    /**
     * Recupera il valore della proprietà phase2CompressionAlgorithm.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhase2CompressionAlgorithm() {
        return phase2CompressionAlgorithm;
    }

    /**
     * Imposta il valore della proprietà phase2CompressionAlgorithm.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhase2CompressionAlgorithm(String value) {
        this.phase2CompressionAlgorithm = value;
    }

    /**
     * Recupera il valore della proprietà hashAlgorithm.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    /**
     * Imposta il valore della proprietà hashAlgorithm.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHashAlgorithm(String value) {
        this.hashAlgorithm = value;
    }

    /**
     * Recupera il valore della proprietà esn.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isESN() {
        return esn;
    }

    /**
     * Imposta il valore della proprietà esn.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setESN(Boolean value) {
        this.esn = value;
    }

    /**
     * Recupera il valore della proprietà encryptionAlgorithm.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    /**
     * Imposta il valore della proprietà encryptionAlgorithm.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncryptionAlgorithm(String value) {
        this.encryptionAlgorithm = value;
    }

    /**
     * Recupera il valore della proprietà lifetime.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLifetime() {
        return lifetime;
    }

    /**
     * Imposta il valore della proprietà lifetime.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLifetime(String value) {
        this.lifetime = value;
    }

    /**
     * Recupera il valore della proprietà rekeyMargin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRekeyMargin() {
        return rekeyMargin;
    }

    /**
     * Imposta il valore della proprietà rekeyMargin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRekeyMargin(String value) {
        this.rekeyMargin = value;
    }

    /**
     * Recupera il valore della proprietà keyringTries.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyringTries() {
        return keyringTries;
    }

    /**
     * Imposta il valore della proprietà keyringTries.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyringTries(String value) {
        this.keyringTries = value;
    }

    /**
     * Recupera il valore della proprietà mobike.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMOBIKE() {
        return mobike;
    }

    /**
     * Imposta il valore della proprietà mobike.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMOBIKE(Boolean value) {
        this.mobike = value;
    }

}
