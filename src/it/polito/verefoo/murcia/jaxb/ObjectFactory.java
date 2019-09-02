//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.09.02 alle 08:15:42 PM CEST 
//


package it.polito.verefoo.murcia.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.polito.verefoo.murcia.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ITResource_QNAME = new QName("http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd", "ITResource");
    private final static QName _ITResourceOrchestration_QNAME = new QName("http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd", "ITResourceOrchestration");
    private final static QName _TechnologyActionParameters_QNAME = new QName("http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd", "technologyActionParameters");
    private final static QName _Technology_QNAME = new QName("http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd", "technology");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.polito.verefoo.murcia.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ITResourceType }
     * 
     */
    public ITResourceType createITResourceType() {
        return new ITResourceType();
    }

    /**
     * Create an instance of {@link ITResourceOrchestrationType }
     * 
     */
    public ITResourceOrchestrationType createITResourceOrchestrationType() {
        return new ITResourceOrchestrationType();
    }

    /**
     * Create an instance of {@link ActionParameters }
     * 
     */
    public ActionParameters createActionParameters() {
        return new ActionParameters();
    }

    /**
     * Create an instance of {@link Dependencies }
     * 
     */
    public Dependencies createDependencies() {
        return new Dependencies();
    }

    /**
     * Create an instance of {@link Dependency }
     * 
     */
    public Dependency createDependency() {
        return new Dependency();
    }

    /**
     * Create an instance of {@link PolicyDependency }
     * 
     */
    public PolicyDependency createPolicyDependency() {
        return new PolicyDependency();
    }

    /**
     * Create an instance of {@link PolicyDependencyCondition }
     * 
     */
    public PolicyDependencyCondition createPolicyDependencyCondition() {
        return new PolicyDependencyCondition();
    }

    /**
     * Create an instance of {@link EventDependency }
     * 
     */
    public EventDependency createEventDependency() {
        return new EventDependency();
    }

    /**
     * Create an instance of {@link EnablerCandidates }
     * 
     */
    public EnablerCandidates createEnablerCandidates() {
        return new EnablerCandidates();
    }

    /**
     * Create an instance of {@link Configuration }
     * 
     */
    public Configuration createConfiguration() {
        return new Configuration();
    }

    /**
     * Create an instance of {@link Capability }
     * 
     */
    public Capability createCapability() {
        return new Capability();
    }

    /**
     * Create an instance of {@link AuthorizationCapability }
     * 
     */
    public AuthorizationCapability createAuthorizationCapability() {
        return new AuthorizationCapability();
    }

    /**
     * Create an instance of {@link ChannelAuthorizationCapability }
     * 
     */
    public ChannelAuthorizationCapability createChannelAuthorizationCapability() {
        return new ChannelAuthorizationCapability();
    }

    /**
     * Create an instance of {@link FilteringCapability }
     * 
     */
    public FilteringCapability createFilteringCapability() {
        return new FilteringCapability();
    }

    /**
     * Create an instance of {@link TargetAuthzCapability }
     * 
     */
    public TargetAuthzCapability createTargetAuthzCapability() {
        return new TargetAuthzCapability();
    }

    /**
     * Create an instance of {@link AuthzEnforcementCapability }
     * 
     */
    public AuthzEnforcementCapability createAuthzEnforcementCapability() {
        return new AuthzEnforcementCapability();
    }

    /**
     * Create an instance of {@link AuthzDecisionCapabiliy }
     * 
     */
    public AuthzDecisionCapabiliy createAuthzDecisionCapabiliy() {
        return new AuthzDecisionCapabiliy();
    }

    /**
     * Create an instance of {@link AuthenticationCapability }
     * 
     */
    public AuthenticationCapability createAuthenticationCapability() {
        return new AuthenticationCapability();
    }

    /**
     * Create an instance of {@link AuthcEnforcementCapability }
     * 
     */
    public AuthcEnforcementCapability createAuthcEnforcementCapability() {
        return new AuthcEnforcementCapability();
    }

    /**
     * Create an instance of {@link AuthcDecisionCapability }
     * 
     */
    public AuthcDecisionCapability createAuthcDecisionCapability() {
        return new AuthcDecisionCapability();
    }

    /**
     * Create an instance of {@link TrafficAnalysisCapability }
     * 
     */
    public TrafficAnalysisCapability createTrafficAnalysisCapability() {
        return new TrafficAnalysisCapability();
    }

    /**
     * Create an instance of {@link IDSCapability }
     * 
     */
    public IDSCapability createIDSCapability() {
        return new IDSCapability();
    }

    /**
     * Create an instance of {@link IPSCapability }
     * 
     */
    public IPSCapability createIPSCapability() {
        return new IPSCapability();
    }

    /**
     * Create an instance of {@link MalwaresAnalysisCapability }
     * 
     */
    public MalwaresAnalysisCapability createMalwaresAnalysisCapability() {
        return new MalwaresAnalysisCapability();
    }

    /**
     * Create an instance of {@link MaliciousFileAnalysisCapability }
     * 
     */
    public MaliciousFileAnalysisCapability createMaliciousFileAnalysisCapability() {
        return new MaliciousFileAnalysisCapability();
    }

    /**
     * Create an instance of {@link LawfulInterceptionCapability }
     * 
     */
    public LawfulInterceptionCapability createLawfulInterceptionCapability() {
        return new LawfulInterceptionCapability();
    }

    /**
     * Create an instance of {@link ResourceScannerCapability }
     * 
     */
    public ResourceScannerCapability createResourceScannerCapability() {
        return new ResourceScannerCapability();
    }

    /**
     * Create an instance of {@link VulnerabilitiesScannerCapability }
     * 
     */
    public VulnerabilitiesScannerCapability createVulnerabilitiesScannerCapability() {
        return new VulnerabilitiesScannerCapability();
    }

    /**
     * Create an instance of {@link BotnetDetectorCapability }
     * 
     */
    public BotnetDetectorCapability createBotnetDetectorCapability() {
        return new BotnetDetectorCapability();
    }

    /**
     * Create an instance of {@link LoggingCapability }
     * 
     */
    public LoggingCapability createLoggingCapability() {
        return new LoggingCapability();
    }

    /**
     * Create an instance of {@link TrafficRecordCapability }
     * 
     */
    public TrafficRecordCapability createTrafficRecordCapability() {
        return new TrafficRecordCapability();
    }

    /**
     * Create an instance of {@link DataProtectionCapability }
     * 
     */
    public DataProtectionCapability createDataProtectionCapability() {
        return new DataProtectionCapability();
    }

    /**
     * Create an instance of {@link EncryptionCapability }
     * 
     */
    public EncryptionCapability createEncryptionCapability() {
        return new EncryptionCapability();
    }

    /**
     * Create an instance of {@link IdentityProtectionCapability }
     * 
     */
    public IdentityProtectionCapability createIdentityProtectionCapability() {
        return new IdentityProtectionCapability();
    }

    /**
     * Create an instance of {@link AnonimizerCapability }
     * 
     */
    public AnonimizerCapability createAnonimizerCapability() {
        return new AnonimizerCapability();
    }

    /**
     * Create an instance of {@link AddressTranslationCapability }
     * 
     */
    public AddressTranslationCapability createAddressTranslationCapability() {
        return new AddressTranslationCapability();
    }

    /**
     * Create an instance of {@link ForwardProxyCapabiity }
     * 
     */
    public ForwardProxyCapabiity createForwardProxyCapabiity() {
        return new ForwardProxyCapabiity();
    }

    /**
     * Create an instance of {@link NattingCapability }
     * 
     */
    public NattingCapability createNattingCapability() {
        return new NattingCapability();
    }

    /**
     * Create an instance of {@link ReverseProxyCapabiity }
     * 
     */
    public ReverseProxyCapabiity createReverseProxyCapabiity() {
        return new ReverseProxyCapabiity();
    }

    /**
     * Create an instance of {@link URLRewritingCapability }
     * 
     */
    public URLRewritingCapability createURLRewritingCapability() {
        return new URLRewritingCapability();
    }

    /**
     * Create an instance of {@link RoutingCapability }
     * 
     */
    public RoutingCapability createRoutingCapability() {
        return new RoutingCapability();
    }

    /**
     * Create an instance of {@link RuleSetConfiguration }
     * 
     */
    public RuleSetConfiguration createRuleSetConfiguration() {
        return new RuleSetConfiguration();
    }

    /**
     * Create an instance of {@link ConfigurationAction }
     * 
     */
    public ConfigurationAction createConfigurationAction() {
        return new ConfigurationAction();
    }

    /**
     * Create an instance of {@link ConfigurationRule }
     * 
     */
    public ConfigurationRule createConfigurationRule() {
        return new ConfigurationRule();
    }

    /**
     * Create an instance of {@link ConfigurationCondition }
     * 
     */
    public ConfigurationCondition createConfigurationCondition() {
        return new ConfigurationCondition();
    }

    /**
     * Create an instance of {@link ExternalData }
     * 
     */
    public ExternalData createExternalData() {
        return new ExternalData();
    }

    /**
     * Create an instance of {@link Priority }
     * 
     */
    public Priority createPriority() {
        return new Priority();
    }

    /**
     * Create an instance of {@link ResolutionStrategy }
     * 
     */
    public ResolutionStrategy createResolutionStrategy() {
        return new ResolutionStrategy();
    }

    /**
     * Create an instance of {@link FMR }
     * 
     */
    public FMR createFMR() {
        return new FMR();
    }

    /**
     * Create an instance of {@link DTP }
     * 
     */
    public DTP createDTP() {
        return new DTP();
    }

    /**
     * Create an instance of {@link ATP }
     * 
     */
    public ATP createATP() {
        return new ATP();
    }

    /**
     * Create an instance of {@link MSTP }
     * 
     */
    public MSTP createMSTP() {
        return new MSTP();
    }

    /**
     * Create an instance of {@link LSTP }
     * 
     */
    public LSTP createLSTP() {
        return new LSTP();
    }

    /**
     * Create an instance of {@link ALL }
     * 
     */
    public ALL createALL() {
        return new ALL();
    }

    /**
     * Create an instance of {@link DataProtectionCondition }
     * 
     */
    public DataProtectionCondition createDataProtectionCondition() {
        return new DataProtectionCondition();
    }

    /**
     * Create an instance of {@link PurposeCondition }
     * 
     */
    public PurposeCondition createPurposeCondition() {
        return new PurposeCondition();
    }

    /**
     * Create an instance of {@link PrivacyConfigurationCondition }
     * 
     */
    public PrivacyConfigurationCondition createPrivacyConfigurationCondition() {
        return new PrivacyConfigurationCondition();
    }

    /**
     * Create an instance of {@link PrivacyAction }
     * 
     */
    public PrivacyAction createPrivacyAction() {
        return new PrivacyAction();
    }

    /**
     * Create an instance of {@link PrivacyMethod }
     * 
     */
    public PrivacyMethod createPrivacyMethod() {
        return new PrivacyMethod();
    }

    /**
     * Create an instance of {@link PrivacyIBMethod }
     * 
     */
    public PrivacyIBMethod createPrivacyIBMethod() {
        return new PrivacyIBMethod();
    }

    /**
     * Create an instance of {@link PrivacyABMethod }
     * 
     */
    public PrivacyABMethod createPrivacyABMethod() {
        return new PrivacyABMethod();
    }

    /**
     * Create an instance of {@link PrivacyPKIMethod }
     * 
     */
    public PrivacyPKIMethod createPrivacyPKIMethod() {
        return new PrivacyPKIMethod();
    }

    /**
     * Create an instance of {@link KeyValue }
     * 
     */
    public KeyValue createKeyValue() {
        return new KeyValue();
    }

    /**
     * Create an instance of {@link DataAgreggationConfigurationConditions }
     * 
     */
    public DataAgreggationConfigurationConditions createDataAgreggationConfigurationConditions() {
        return new DataAgreggationConfigurationConditions();
    }

    /**
     * Create an instance of {@link DataAggregationConfigurationCondition }
     * 
     */
    public DataAggregationConfigurationCondition createDataAggregationConfigurationCondition() {
        return new DataAggregationConfigurationCondition();
    }

    /**
     * Create an instance of {@link DataAggregationAction }
     * 
     */
    public DataAggregationAction createDataAggregationAction() {
        return new DataAggregationAction();
    }

    /**
     * Create an instance of {@link AnonymityConfigurationCondition }
     * 
     */
    public AnonymityConfigurationCondition createAnonymityConfigurationCondition() {
        return new AnonymityConfigurationCondition();
    }

    /**
     * Create an instance of {@link AnonymityAction }
     * 
     */
    public AnonymityAction createAnonymityAction() {
        return new AnonymityAction();
    }

    /**
     * Create an instance of {@link AnonymityTechnologyParameter }
     * 
     */
    public AnonymityTechnologyParameter createAnonymityTechnologyParameter() {
        return new AnonymityTechnologyParameter();
    }

    /**
     * Create an instance of {@link OnionRoutingTechnologyParameter }
     * 
     */
    public OnionRoutingTechnologyParameter createOnionRoutingTechnologyParameter() {
        return new OnionRoutingTechnologyParameter();
    }

    /**
     * Create an instance of {@link TrafficMIXERTechnologyParameter }
     * 
     */
    public TrafficMIXERTechnologyParameter createTrafficMIXERTechnologyParameter() {
        return new TrafficMIXERTechnologyParameter();
    }

    /**
     * Create an instance of {@link MonitoringConfigurationConditions }
     * 
     */
    public MonitoringConfigurationConditions createMonitoringConfigurationConditions() {
        return new MonitoringConfigurationConditions();
    }

    /**
     * Create an instance of {@link MonitoringConfigurationCondition }
     * 
     */
    public MonitoringConfigurationCondition createMonitoringConfigurationCondition() {
        return new MonitoringConfigurationCondition();
    }

    /**
     * Create an instance of {@link SignatureList }
     * 
     */
    public SignatureList createSignatureList() {
        return new SignatureList();
    }

    /**
     * Create an instance of {@link MonitoringAction }
     * 
     */
    public MonitoringAction createMonitoringAction() {
        return new MonitoringAction();
    }

    /**
     * Create an instance of {@link TrafficDivertConfigurationCondition }
     * 
     */
    public TrafficDivertConfigurationCondition createTrafficDivertConfigurationCondition() {
        return new TrafficDivertConfigurationCondition();
    }

    /**
     * Create an instance of {@link QoSAction }
     * 
     */
    public QoSAction createQoSAction() {
        return new QoSAction();
    }

    /**
     * Create an instance of {@link FilteringConfigurationCondition }
     * 
     */
    public FilteringConfigurationCondition createFilteringConfigurationCondition() {
        return new FilteringConfigurationCondition();
    }

    /**
     * Create an instance of {@link PacketFilterCondition }
     * 
     */
    public PacketFilterCondition createPacketFilterCondition() {
        return new PacketFilterCondition();
    }

    /**
     * Create an instance of {@link QoSCondition }
     * 
     */
    public QoSCondition createQoSCondition() {
        return new QoSCondition();
    }

    /**
     * Create an instance of {@link ApplicationLayerCondition }
     * 
     */
    public ApplicationLayerCondition createApplicationLayerCondition() {
        return new ApplicationLayerCondition();
    }

    /**
     * Create an instance of {@link IoTApplicationLayerCondition }
     * 
     */
    public IoTApplicationLayerCondition createIoTApplicationLayerCondition() {
        return new IoTApplicationLayerCondition();
    }

    /**
     * Create an instance of {@link StatefulCondition }
     * 
     */
    public StatefulCondition createStatefulCondition() {
        return new StatefulCondition();
    }

    /**
     * Create an instance of {@link TimeCondition }
     * 
     */
    public TimeCondition createTimeCondition() {
        return new TimeCondition();
    }

    /**
     * Create an instance of {@link InterfaceSelectionCondition }
     * 
     */
    public InterfaceSelectionCondition createInterfaceSelectionCondition() {
        return new InterfaceSelectionCondition();
    }

    /**
     * Create an instance of {@link DataSelectionCondition }
     * 
     */
    public DataSelectionCondition createDataSelectionCondition() {
        return new DataSelectionCondition();
    }

    /**
     * Create an instance of {@link FileSystemCondition }
     * 
     */
    public FileSystemCondition createFileSystemCondition() {
        return new FileSystemCondition();
    }

    /**
     * Create an instance of {@link EventCondition }
     * 
     */
    public EventCondition createEventCondition() {
        return new EventCondition();
    }

    /**
     * Create an instance of {@link AntiMalwareCondition }
     * 
     */
    public AntiMalwareCondition createAntiMalwareCondition() {
        return new AntiMalwareCondition();
    }

    /**
     * Create an instance of {@link LoggingCondition }
     * 
     */
    public LoggingCondition createLoggingCondition() {
        return new LoggingCondition();
    }

    /**
     * Create an instance of {@link DBDataSelectionCondition }
     * 
     */
    public DBDataSelectionCondition createDBDataSelectionCondition() {
        return new DBDataSelectionCondition();
    }

    /**
     * Create an instance of {@link XMLDataSelectionCondition }
     * 
     */
    public XMLDataSelectionCondition createXMLDataSelectionCondition() {
        return new XMLDataSelectionCondition();
    }

    /**
     * Create an instance of {@link WSSecurityCondition }
     * 
     */
    public WSSecurityCondition createWSSecurityCondition() {
        return new WSSecurityCondition();
    }

    /**
     * Create an instance of {@link TrafficDivertAction }
     * 
     */
    public TrafficDivertAction createTrafficDivertAction() {
        return new TrafficDivertAction();
    }

    /**
     * Create an instance of {@link TrafficDivertEncapsulationAction }
     * 
     */
    public TrafficDivertEncapsulationAction createTrafficDivertEncapsulationAction() {
        return new TrafficDivertEncapsulationAction();
    }

    /**
     * Create an instance of {@link FilteringAction }
     * 
     */
    public FilteringAction createFilteringAction() {
        return new FilteringAction();
    }

    /**
     * Create an instance of {@link EnableAction }
     * 
     */
    public EnableAction createEnableAction() {
        return new EnableAction();
    }

    /**
     * Create an instance of {@link ReduceBandwidthAction }
     * 
     */
    public ReduceBandwidthAction createReduceBandwidthAction() {
        return new ReduceBandwidthAction();
    }

    /**
     * Create an instance of {@link CountAction }
     * 
     */
    public CountAction createCountAction() {
        return new CountAction();
    }

    /**
     * Create an instance of {@link CheckAction }
     * 
     */
    public CheckAction createCheckAction() {
        return new CheckAction();
    }

    /**
     * Create an instance of {@link RemoveAction }
     * 
     */
    public RemoveAction createRemoveAction() {
        return new RemoveAction();
    }

    /**
     * Create an instance of {@link AntiMalwareAction }
     * 
     */
    public AntiMalwareAction createAntiMalwareAction() {
        return new AntiMalwareAction();
    }

    /**
     * Create an instance of {@link LoggingAction }
     * 
     */
    public LoggingAction createLoggingAction() {
        return new LoggingAction();
    }

    /**
     * Create an instance of {@link RemoveAdvertisementAction }
     * 
     */
    public RemoveAdvertisementAction createRemoveAdvertisementAction() {
        return new RemoveAdvertisementAction();
    }

    /**
     * Create an instance of {@link RemoveTrackingTechniquesAction }
     * 
     */
    public RemoveTrackingTechniquesAction createRemoveTrackingTechniquesAction() {
        return new RemoveTrackingTechniquesAction();
    }

    /**
     * Create an instance of {@link ParentalControlAction }
     * 
     */
    public ParentalControlAction createParentalControlAction() {
        return new ParentalControlAction();
    }

    /**
     * Create an instance of {@link AnonimityAction }
     * 
     */
    public AnonimityAction createAnonimityAction() {
        return new AnonimityAction();
    }

    /**
     * Create an instance of {@link BootstrappingAction }
     * 
     */
    public BootstrappingAction createBootstrappingAction() {
        return new BootstrappingAction();
    }

    /**
     * Create an instance of {@link PowerMgmtAction }
     * 
     */
    public PowerMgmtAction createPowerMgmtAction() {
        return new PowerMgmtAction();
    }

    /**
     * Create an instance of {@link AuthorizationAction }
     * 
     */
    public AuthorizationAction createAuthorizationAction() {
        return new AuthorizationAction();
    }

    /**
     * Create an instance of {@link AuthorizationCondition }
     * 
     */
    public AuthorizationCondition createAuthorizationCondition() {
        return new AuthorizationCondition();
    }

    /**
     * Create an instance of {@link AuthenticationAction }
     * 
     */
    public AuthenticationAction createAuthenticationAction() {
        return new AuthenticationAction();
    }

    /**
     * Create an instance of {@link AuthenticationOption }
     * 
     */
    public AuthenticationOption createAuthenticationOption() {
        return new AuthenticationOption();
    }

    /**
     * Create an instance of {@link AuthenticationCondition }
     * 
     */
    public AuthenticationCondition createAuthenticationCondition() {
        return new AuthenticationCondition();
    }

    /**
     * Create an instance of {@link Pics }
     * 
     */
    public Pics createPics() {
        return new Pics();
    }

    /**
     * Create an instance of {@link ICRA }
     * 
     */
    public ICRA createICRA() {
        return new ICRA();
    }

    /**
     * Create an instance of {@link RSAC }
     * 
     */
    public RSAC createRSAC() {
        return new RSAC();
    }

    /**
     * Create an instance of {@link SafeNet }
     * 
     */
    public SafeNet createSafeNet() {
        return new SafeNet();
    }

    /**
     * Create an instance of {@link Vancouver }
     * 
     */
    public Vancouver createVancouver() {
        return new Vancouver();
    }

    /**
     * Create an instance of {@link DataProtectionAction }
     * 
     */
    public DataProtectionAction createDataProtectionAction() {
        return new DataProtectionAction();
    }

    /**
     * Create an instance of {@link TechnologyActionSecurityProperty }
     * 
     */
    public TechnologyActionSecurityProperty createTechnologyActionSecurityProperty() {
        return new TechnologyActionSecurityProperty();
    }

    /**
     * Create an instance of {@link Integrity }
     * 
     */
    public Integrity createIntegrity() {
        return new Integrity();
    }

    /**
     * Create an instance of {@link Authentication }
     * 
     */
    public Authentication createAuthentication() {
        return new Authentication();
    }

    /**
     * Create an instance of {@link Confidentiality }
     * 
     */
    public Confidentiality createConfidentiality() {
        return new Confidentiality();
    }

    /**
     * Create an instance of {@link KeyExchangeParameter }
     * 
     */
    public KeyExchangeParameter createKeyExchangeParameter() {
        return new KeyExchangeParameter();
    }

    /**
     * Create an instance of {@link TechnologySpecificParameters }
     * 
     */
    public TechnologySpecificParameters createTechnologySpecificParameters() {
        return new TechnologySpecificParameters();
    }

    /**
     * Create an instance of {@link IPsecTechnologyParameter }
     * 
     */
    public IPsecTechnologyParameter createIPsecTechnologyParameter() {
        return new IPsecTechnologyParameter();
    }

    /**
     * Create an instance of {@link DTLSTechnologyParameter }
     * 
     */
    public DTLSTechnologyParameter createDTLSTechnologyParameter() {
        return new DTLSTechnologyParameter();
    }

    /**
     * Create an instance of {@link IKETechnologyParameter }
     * 
     */
    public IKETechnologyParameter createIKETechnologyParameter() {
        return new IKETechnologyParameter();
    }

    /**
     * Create an instance of {@link TLSVPNTechnologyParameter }
     * 
     */
    public TLSVPNTechnologyParameter createTLSVPNTechnologyParameter() {
        return new TLSVPNTechnologyParameter();
    }

    /**
     * Create an instance of {@link TLSSSLTechnologyParameter }
     * 
     */
    public TLSSSLTechnologyParameter createTLSSSLTechnologyParameter() {
        return new TLSSSLTechnologyParameter();
    }

    /**
     * Create an instance of {@link AdditionalNetworkConfigurationParameters }
     * 
     */
    public AdditionalNetworkConfigurationParameters createAdditionalNetworkConfigurationParameters() {
        return new AdditionalNetworkConfigurationParameters();
    }

    /**
     * Create an instance of {@link ReencryptNetworkConfiguration }
     * 
     */
    public ReencryptNetworkConfiguration createReencryptNetworkConfiguration() {
        return new ReencryptNetworkConfiguration();
    }

    /**
     * Create an instance of {@link Site2SiteNetworkConfiguration }
     * 
     */
    public Site2SiteNetworkConfiguration createSite2SiteNetworkConfiguration() {
        return new Site2SiteNetworkConfiguration();
    }

    /**
     * Create an instance of {@link RemoteAccessNetworkConfiguration }
     * 
     */
    public RemoteAccessNetworkConfiguration createRemoteAccessNetworkConfiguration() {
        return new RemoteAccessNetworkConfiguration();
    }

    /**
     * Create an instance of {@link AuthenticationParameters }
     * 
     */
    public AuthenticationParameters createAuthenticationParameters() {
        return new AuthenticationParameters();
    }

    /**
     * Create an instance of {@link EnableActionType }
     * 
     */
    public EnableActionType createEnableActionType() {
        return new EnableActionType();
    }

    /**
     * Create an instance of {@link ReduceBandwidthActionType }
     * 
     */
    public ReduceBandwidthActionType createReduceBandwidthActionType() {
        return new ReduceBandwidthActionType();
    }

    /**
     * Create an instance of {@link CountActionType }
     * 
     */
    public CountActionType createCountActionType() {
        return new CountActionType();
    }

    /**
     * Create an instance of {@link RemoveActionType }
     * 
     */
    public RemoveActionType createRemoveActionType() {
        return new RemoveActionType();
    }

    /**
     * Create an instance of {@link CheckActionType }
     * 
     */
    public CheckActionType createCheckActionType() {
        return new CheckActionType();
    }

    /**
     * Create an instance of {@link RemoveAdvertisementActionType }
     * 
     */
    public RemoveAdvertisementActionType createRemoveAdvertisementActionType() {
        return new RemoveAdvertisementActionType();
    }

    /**
     * Create an instance of {@link RemoveTrackingTechniquesActionType }
     * 
     */
    public RemoveTrackingTechniquesActionType createRemoveTrackingTechniquesActionType() {
        return new RemoveTrackingTechniquesActionType();
    }

    /**
     * Create an instance of {@link PurposeConditionType }
     * 
     */
    public PurposeConditionType createPurposeConditionType() {
        return new PurposeConditionType();
    }

    /**
     * Create an instance of {@link HSPL }
     * 
     */
    public HSPL createHSPL() {
        return new HSPL();
    }

    /**
     * Create an instance of {@link HTTPCondition }
     * 
     */
    public HTTPCondition createHTTPCondition() {
        return new HTTPCondition();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ITResourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd", name = "ITResource")
    public JAXBElement<ITResourceType> createITResource(ITResourceType value) {
        return new JAXBElement<ITResourceType>(_ITResource_QNAME, ITResourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ITResourceOrchestrationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd", name = "ITResourceOrchestration")
    public JAXBElement<ITResourceOrchestrationType> createITResourceOrchestration(ITResourceOrchestrationType value) {
        return new JAXBElement<ITResourceOrchestrationType>(_ITResourceOrchestration_QNAME, ITResourceOrchestrationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ActionParameters }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd", name = "technologyActionParameters")
    public JAXBElement<ActionParameters> createTechnologyActionParameters(ActionParameters value) {
        return new JAXBElement<ActionParameters>(_TechnologyActionParameters_QNAME, ActionParameters.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd", name = "technology")
    public JAXBElement<String> createTechnology(String value) {
        return new JAXBElement<String>(_Technology_QNAME, String.class, null, value);
    }

}
