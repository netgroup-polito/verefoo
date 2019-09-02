//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.09.02 alle 08:15:42 PM CEST 
//


package it.polito.verefoo.murcia.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per CapabilityType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CapabilityType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Filtering_L4"/&gt;
 *     &lt;enumeration value="Filtering_L3"/&gt;
 *     &lt;enumeration value="Filtering_L7"/&gt;
 *     &lt;enumeration value="Timing"/&gt;
 *     &lt;enumeration value="TrafficInspection_L7"/&gt;
 *     &lt;enumeration value="Filtering_3G4G"/&gt;
 *     &lt;enumeration value="Filtering_DNS"/&gt;
 *     &lt;enumeration value="Offline_malware_analysis"/&gt;
 *     &lt;enumeration value="Online_SPAM_analysis"/&gt;
 *     &lt;enumeration value="Online_antivirus_analysis"/&gt;
 *     &lt;enumeration value="Network_traffic_analysis"/&gt;
 *     &lt;enumeration value="DDos_attack_protection"/&gt;
 *     &lt;enumeration value="lawful_interception"/&gt;
 *     &lt;enumeration value="Count_L4Connection"/&gt;
 *     &lt;enumeration value="Count_DNS"/&gt;
 *     &lt;enumeration value="Protection_confidentiality"/&gt;
 *     &lt;enumeration value="Protection_integrity"/&gt;
 *     &lt;enumeration value="Compress"/&gt;
 *     &lt;enumeration value="Logging"/&gt;
 *     &lt;enumeration value="AuthoriseAccess_resurce"/&gt;
 *     &lt;enumeration value="Reduce_bandwidth"/&gt;
 *     &lt;enumeration value="Online_security_analyzer"/&gt;
 *     &lt;enumeration value="Basic_parental_control"/&gt;
 *     &lt;enumeration value="Advanced_parental_control"/&gt;
 *     &lt;enumeration value="IPSec_protocol"/&gt;
 *     &lt;enumeration value="TLS_protocol"/&gt;
 *     &lt;enumeration value="reencrypt"/&gt;
 *     &lt;enumeration value="antiPhishing"/&gt;
 *     &lt;enumeration value="Network_Anonymity"/&gt;
 *     &lt;enumeration value="IoT_control"/&gt;
 *     &lt;enumeration value="DTLS_protocol"/&gt;
 *     &lt;enumeration value="Authentication"/&gt;
 *     &lt;enumeration value="Traffic_Divert"/&gt;
 *     &lt;enumeration value="IoT_honeynet"/&gt;
 *     &lt;enumeration value="Privacy"/&gt;
 *     &lt;enumeration value="QoS"/&gt;
 *     &lt;enumeration value="Data_aggregation"/&gt;
 *     &lt;enumeration value="Traffic_MIX"/&gt;
 *     &lt;enumeration value="Onion_Routing"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CapabilityType")
@XmlEnum
public enum CapabilityType {

    @XmlEnumValue("Filtering_L4")
    FILTERING_L_4("Filtering_L4"),
    @XmlEnumValue("Filtering_L3")
    FILTERING_L_3("Filtering_L3"),
    @XmlEnumValue("Filtering_L7")
    FILTERING_L_7("Filtering_L7"),
    @XmlEnumValue("Timing")
    TIMING("Timing"),
    @XmlEnumValue("TrafficInspection_L7")
    TRAFFIC_INSPECTION_L_7("TrafficInspection_L7"),
    @XmlEnumValue("Filtering_3G4G")
    FILTERING_3_G_4_G("Filtering_3G4G"),
    @XmlEnumValue("Filtering_DNS")
    FILTERING_DNS("Filtering_DNS"),
    @XmlEnumValue("Offline_malware_analysis")
    OFFLINE_MALWARE_ANALYSIS("Offline_malware_analysis"),
    @XmlEnumValue("Online_SPAM_analysis")
    ONLINE_SPAM_ANALYSIS("Online_SPAM_analysis"),
    @XmlEnumValue("Online_antivirus_analysis")
    ONLINE_ANTIVIRUS_ANALYSIS("Online_antivirus_analysis"),
    @XmlEnumValue("Network_traffic_analysis")
    NETWORK_TRAFFIC_ANALYSIS("Network_traffic_analysis"),
    @XmlEnumValue("DDos_attack_protection")
    D_DOS_ATTACK_PROTECTION("DDos_attack_protection"),
    @XmlEnumValue("lawful_interception")
    LAWFUL_INTERCEPTION("lawful_interception"),
    @XmlEnumValue("Count_L4Connection")
    COUNT_L_4_CONNECTION("Count_L4Connection"),
    @XmlEnumValue("Count_DNS")
    COUNT_DNS("Count_DNS"),
    @XmlEnumValue("Protection_confidentiality")
    PROTECTION_CONFIDENTIALITY("Protection_confidentiality"),
    @XmlEnumValue("Protection_integrity")
    PROTECTION_INTEGRITY("Protection_integrity"),
    @XmlEnumValue("Compress")
    COMPRESS("Compress"),
    @XmlEnumValue("Logging")
    LOGGING("Logging"),
    @XmlEnumValue("AuthoriseAccess_resurce")
    AUTHORISE_ACCESS_RESURCE("AuthoriseAccess_resurce"),
    @XmlEnumValue("Reduce_bandwidth")
    REDUCE_BANDWIDTH("Reduce_bandwidth"),
    @XmlEnumValue("Online_security_analyzer")
    ONLINE_SECURITY_ANALYZER("Online_security_analyzer"),
    @XmlEnumValue("Basic_parental_control")
    BASIC_PARENTAL_CONTROL("Basic_parental_control"),
    @XmlEnumValue("Advanced_parental_control")
    ADVANCED_PARENTAL_CONTROL("Advanced_parental_control"),
    @XmlEnumValue("IPSec_protocol")
    IP_SEC_PROTOCOL("IPSec_protocol"),
    @XmlEnumValue("TLS_protocol")
    TLS_PROTOCOL("TLS_protocol"),
    @XmlEnumValue("reencrypt")
    REENCRYPT("reencrypt"),
    @XmlEnumValue("antiPhishing")
    ANTI_PHISHING("antiPhishing"),
    @XmlEnumValue("Network_Anonymity")
    NETWORK_ANONYMITY("Network_Anonymity"),
    @XmlEnumValue("IoT_control")
    IO_T_CONTROL("IoT_control"),
    @XmlEnumValue("DTLS_protocol")
    DTLS_PROTOCOL("DTLS_protocol"),
    @XmlEnumValue("Authentication")
    AUTHENTICATION("Authentication"),
    @XmlEnumValue("Traffic_Divert")
    TRAFFIC_DIVERT("Traffic_Divert"),
    @XmlEnumValue("IoT_honeynet")
    IO_T_HONEYNET("IoT_honeynet"),
    @XmlEnumValue("Privacy")
    PRIVACY("Privacy"),
    @XmlEnumValue("QoS")
    QO_S("QoS"),
    @XmlEnumValue("Data_aggregation")
    DATA_AGGREGATION("Data_aggregation"),
    @XmlEnumValue("Traffic_MIX")
    TRAFFIC_MIX("Traffic_MIX"),
    @XmlEnumValue("Onion_Routing")
    ONION_ROUTING("Onion_Routing");
    private final String value;

    CapabilityType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CapabilityType fromValue(String v) {
        for (CapabilityType c: CapabilityType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
