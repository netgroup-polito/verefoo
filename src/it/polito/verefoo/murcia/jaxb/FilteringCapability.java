//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.09.02 alle 08:15:42 PM CEST 
//


package it.polito.verefoo.murcia.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per FilteringCapability complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="FilteringCapability"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ChannelAuthorizationCapability"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="stateful" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="applicationLayerFiltering" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="httpFiltering" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="contentInspection" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilteringCapability", propOrder = {
    "stateful",
    "applicationLayerFiltering",
    "httpFiltering",
    "contentInspection"
})
public class FilteringCapability
    extends ChannelAuthorizationCapability
{

    protected boolean stateful;
    protected boolean applicationLayerFiltering;
    protected boolean httpFiltering;
    protected boolean contentInspection;

    /**
     * Recupera il valore della proprietà stateful.
     * 
     */
    public boolean isStateful() {
        return stateful;
    }

    /**
     * Imposta il valore della proprietà stateful.
     * 
     */
    public void setStateful(boolean value) {
        this.stateful = value;
    }

    /**
     * Recupera il valore della proprietà applicationLayerFiltering.
     * 
     */
    public boolean isApplicationLayerFiltering() {
        return applicationLayerFiltering;
    }

    /**
     * Imposta il valore della proprietà applicationLayerFiltering.
     * 
     */
    public void setApplicationLayerFiltering(boolean value) {
        this.applicationLayerFiltering = value;
    }

    /**
     * Recupera il valore della proprietà httpFiltering.
     * 
     */
    public boolean isHttpFiltering() {
        return httpFiltering;
    }

    /**
     * Imposta il valore della proprietà httpFiltering.
     * 
     */
    public void setHttpFiltering(boolean value) {
        this.httpFiltering = value;
    }

    /**
     * Recupera il valore della proprietà contentInspection.
     * 
     */
    public boolean isContentInspection() {
        return contentInspection;
    }

    /**
     * Imposta il valore della proprietà contentInspection.
     * 
     */
    public void setContentInspection(boolean value) {
        this.contentInspection = value;
    }

}
