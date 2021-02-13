package it.polito.verefoo.rest.spring.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import it.polito.verefoo.DbActionTypes;
import it.polito.verefoo.DbAllocationConstraint;
import it.polito.verefoo.DbAllocationConstraintType;
import it.polito.verefoo.DbAllocationConstraints;
import it.polito.verefoo.DbAntispam;
import it.polito.verefoo.DbCache;
import it.polito.verefoo.DbConfiguration;
import it.polito.verefoo.DbConstraints;
import it.polito.verefoo.DbDpi;
import it.polito.verefoo.DbDpiElements;
import it.polito.verefoo.DbElements;
import it.polito.verefoo.DbEndhost;
import it.polito.verefoo.DbEndpoint;
import it.polito.verefoo.DbFieldmodifier;
import it.polito.verefoo.DbFirewall;
import it.polito.verefoo.DbForwarder;
import it.polito.verefoo.DbFunctionalTypes;
import it.polito.verefoo.DbGraph;
import it.polito.verefoo.DbGraphs;
import it.polito.verefoo.DbL4ProtocolTypes;
import it.polito.verefoo.DbLinkConstraints;
import it.polito.verefoo.DbLinkMetrics;
import it.polito.verefoo.DbLoadbalancer;
import it.polito.verefoo.DbMailclient;
import it.polito.verefoo.DbMailserver;
import it.polito.verefoo.DbNat;
import it.polito.verefoo.DbNeighbour;
import it.polito.verefoo.DbNode;
import it.polito.verefoo.DbNodeConstraints;
import it.polito.verefoo.DbNodeMetrics;
import it.polito.verefoo.DbProtocolTypes;
import it.polito.verefoo.DbStatefulFirewall;
import it.polito.verefoo.DbVpnaccess;
import it.polito.verefoo.DbVpnexit;
import it.polito.verefoo.DbWafElements;
import it.polito.verefoo.DbWebApplicationFirewall;
import it.polito.verefoo.DbWebclient;
import it.polito.verefoo.DbWebserver;
import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.AllocationConstraintType;
import it.polito.verefoo.jaxb.AllocationConstraints;
import it.polito.verefoo.jaxb.Antispam;
import it.polito.verefoo.jaxb.Cache;
import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.Constraints;
import it.polito.verefoo.jaxb.Dpi;
import it.polito.verefoo.jaxb.DpiElements;
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.Endhost;
import it.polito.verefoo.jaxb.Endpoint;
import it.polito.verefoo.jaxb.Fieldmodifier;
import it.polito.verefoo.jaxb.Firewall;
import it.polito.verefoo.jaxb.Forwarder;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Graphs;
import it.polito.verefoo.jaxb.L4ProtocolTypes;
import it.polito.verefoo.jaxb.LinkConstraints;
import it.polito.verefoo.jaxb.Loadbalancer;
import it.polito.verefoo.jaxb.Mailclient;
import it.polito.verefoo.jaxb.Mailserver;
import it.polito.verefoo.jaxb.Nat;
import it.polito.verefoo.jaxb.Neighbour;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.NodeConstraints;
import it.polito.verefoo.jaxb.ProtocolTypes;
import it.polito.verefoo.jaxb.StatefulFirewall;
import it.polito.verefoo.jaxb.Vpnaccess;
import it.polito.verefoo.jaxb.Vpnexit;
import it.polito.verefoo.jaxb.WafElements;
import it.polito.verefoo.jaxb.WebApplicationFirewall;
import it.polito.verefoo.jaxb.Webclient;
import it.polito.verefoo.jaxb.Webserver;
import it.polito.verefoo.jaxb.AllocationConstraints.AllocationConstraint;
import it.polito.verefoo.jaxb.LinkConstraints.LinkMetrics;
import it.polito.verefoo.jaxb.NodeConstraints.NodeMetrics;

@Component
public class GraphConverter {

    public DbGraphs deserializeGraphs(Graphs graphs) {
        if (graphs == null) return null;
        DbGraphs dbGraphs = new DbGraphs();
        graphs.getGraph().forEach(graph -> {
            dbGraphs.getGraph().add(deserializeGraph(graph));
        });
        return dbGraphs;
    }

    public DbGraph deserializeGraph(Graph graph) {
        if (graph == null) return null;
        DbGraph dbGraph = new DbGraph();
        dbGraph.setServiceGraph(graph.isServiceGraph());
        graph.getNode().forEach(node -> {
            dbGraph.getNode().add(deserializeNode(node));
        });
        return dbGraph;
    }

    public DbNode deserializeNode(Node node) {
        if (node == null) return null;
        DbNode dbNode = new DbNode();
        dbNode.setName(node.getName());
        dbNode.setFunctionalType(node.getFunctionalType() != null ? DbFunctionalTypes.fromValue(node.getFunctionalType().name()) : null);
        dbNode.setConfiguration(node.getConfiguration() != null ? deserializeConfiguration(node.getConfiguration()) : null);
        node.getNeighbour().forEach(neighbour -> {
            dbNode.getNeighbour().add(deserializeNeighbour(neighbour));
        });
        return dbNode;
    }

    public DbNeighbour deserializeNeighbour(Neighbour neighbour) {
        if (neighbour == null) return null;
        DbNeighbour dbNeighbour = new DbNeighbour();
        dbNeighbour.setName(neighbour.getName());
        return dbNeighbour;
    }

    public DbConfiguration deserializeConfiguration(Configuration configuration) {
        if (configuration == null) return null;
        DbConfiguration dbConfiguration = new DbConfiguration();
        dbConfiguration.setAntispam(deserializeAntispam(configuration.getAntispam()));
        dbConfiguration.setCache(deserializeCache(configuration.getCache()));
        dbConfiguration.setDescription(configuration.getDescription());
        dbConfiguration.setDpi(deserializeDpi(configuration.getDpi()));
        dbConfiguration.setEndhost(deserializeEndhost(configuration.getEndhost()));
        dbConfiguration.setEndpoint(deserializeEndpoint(configuration.getEndpoint()));
        dbConfiguration.setFieldmodifier(deserializeFieldmodifier(configuration.getFieldmodifier()));
        dbConfiguration.setFirewall(deserializeFirewall(configuration.getFirewall()));
        dbConfiguration.setForwarder(deserializeForwarder(configuration.getForwarder()));
        dbConfiguration.setLoadbalancer(deserializeLoadbalancer(configuration.getLoadbalancer()));
        dbConfiguration.setMailclient(deserializeMailclient(configuration.getMailclient()));
        dbConfiguration.setMailserver(deserializeMailserver(configuration.getMailserver()));
        dbConfiguration.setName(configuration.getName());
        dbConfiguration.setNat(deserializeNat(configuration.getNat()));
        dbConfiguration.setStatefulFirewall(deserializeStatefulFirewall(configuration.getStatefulFirewall()));
        dbConfiguration.setVpnaccess(deserializeVpnaccess(configuration.getVpnaccess()));
        dbConfiguration.setVpnexit(deserializeVpnexit(configuration.getVpnexit()));
        dbConfiguration.setWebApplicationFirewall(
                deserializeWebApplicationFirewall(configuration.getWebApplicationFirewall()));
        dbConfiguration.setWebclient(deserializeWebclient(configuration.getWebclient()));
        dbConfiguration.setWebserver(deserializeWebserver(configuration.getWebserver()));
        return dbConfiguration;
    }

    public DbAntispam deserializeAntispam(Antispam antispam) {
        if (antispam == null)
            return null;
        DbAntispam dbAntispam = new DbAntispam();
        dbAntispam.getSource().addAll(antispam.getSource());
        return dbAntispam;
    }

    public DbCache deserializeCache(Cache cache) {
        if (cache == null)
            return null;
        DbCache dbCache = new DbCache();
        dbCache.getResource().addAll(cache.getResource());
        return dbCache;
    }

    public DbDpi deserializeDpi(Dpi dpi) {
        if (dpi == null)
            return null;
        DbDpi dbDpi = new DbDpi();
        dbDpi.setDefaultAction(DbActionTypes.fromValue(dpi.getDefaultAction().name()));
        dbDpi.getDpiElements().addAll(deserializeDpiElements(dpi.getDpiElements()));
        return dbDpi;
    }

    public List<DbDpiElements> deserializeDpiElements(List<DpiElements> list) {
        if (list == null)
            return null;
        List<DbDpiElements> dbList = new ArrayList<>();
        list.forEach(dpiElements -> {
            DbDpiElements dbDpiElements = new DbDpiElements();
            dbDpiElements.setAction(DbActionTypes.fromValue(dpiElements.getAction().name()));
            dbDpiElements.setCondition(dpiElements.getCondition());
            dbList.add(dbDpiElements);
        });
        return dbList;
    }

    public DbElements deserializeElements(Elements elements) {
        if (elements == null)
            return null;
        DbElements dbElements = new DbElements();
        dbElements.setAction(DbActionTypes.fromValue(elements.getAction().name()));
        dbElements.setDestination(elements.getDestination());
        dbElements.setDirectional(elements.isDirectional());
        dbElements.setDstPort(elements.getDstPort());
        dbElements.setProtocol(DbL4ProtocolTypes.fromValue(elements.getProtocol().name()));
        dbElements.setSource(elements.getSource());
        dbElements.setSrcPort(elements.getSrcPort());
        return dbElements;
    }

    public DbEndhost deserializeEndhost(Endhost endhost) {
        if (endhost == null)
            return null;
        DbEndhost dbEndhost = new DbEndhost();
        dbEndhost.setBody(endhost.getBody());
        dbEndhost.setDestination(endhost.getDestination());
        dbEndhost.setEmailFrom(endhost.getEmailFrom());
        dbEndhost.setOptions(endhost.getOptions());
        dbEndhost.setProtocol(DbProtocolTypes.fromValue(endhost.getProtocol().name()));
        dbEndhost.setSequence(endhost.getSequence());
        dbEndhost.setUrl(endhost.getUrl());
        return dbEndhost;
    }

    public DbEndpoint deserializeEndpoint(Endpoint endpoint) {
        if (endpoint == null)
            return null;
        DbEndpoint dbEndpoint = new DbEndpoint();
        dbEndpoint.setName(endpoint.getName());
        return dbEndpoint;
    }

    public DbFieldmodifier deserializeFieldmodifier(Fieldmodifier fieldmodifier) {
        if (fieldmodifier == null)
            return null;
        DbFieldmodifier dbFieldModifier = new DbFieldmodifier();
        dbFieldModifier.setName(fieldmodifier.getName());
        return dbFieldModifier;
    }

    public DbFirewall deserializeFirewall(Firewall firewall) {
        if (firewall == null)
            return null;
        DbFirewall dbFirewall = new DbFirewall();
        dbFirewall.setDefaultAction(DbActionTypes.fromValue(firewall.getDefaultAction().name()));
        firewall.getElements().forEach(elements -> {
            dbFirewall.getElements().add(deserializeElements(elements));
        });
        return dbFirewall;
    }

    public DbForwarder deserializeForwarder(Forwarder forwarder) {
        if (forwarder == null)
            return null;
        DbForwarder dbForwarder = new DbForwarder();
        dbForwarder.setName(forwarder.getName());
        return dbForwarder;
    }

    public DbLoadbalancer deserializeLoadbalancer(Loadbalancer loadbalancer) {
        if (loadbalancer == null)
            return null;
        DbLoadbalancer dbLoadbalancer = new DbLoadbalancer();
        dbLoadbalancer.getPool().addAll(loadbalancer.getPool());
        return dbLoadbalancer;
    }

    public DbMailclient deserializeMailclient(Mailclient mailclient) {
        if (mailclient == null)
            return null;
        DbMailclient dbMailclient = new DbMailclient();
        dbMailclient.setMailserver(mailclient.getMailserver());
        return dbMailclient;
    }

    public DbMailserver deserializeMailserver(Mailserver mailserver) {
        if (mailserver == null)
            return null;
        DbMailserver dbMailserver = new DbMailserver();
        dbMailserver.setName(mailserver.getName());
        return dbMailserver;
    }

    public DbNat deserializeNat(Nat nat) {
        if (nat == null)
            return null;
        DbNat dbNat = new DbNat();
        dbNat.getSource().addAll(nat.getSource());
        return dbNat;
    }

    public DbStatefulFirewall deserializeStatefulFirewall(StatefulFirewall statefulFirewall) {
        if (statefulFirewall == null)
            return null;
        DbStatefulFirewall dbStatefulFirewall = new DbStatefulFirewall();
        dbStatefulFirewall.setDefaultAction(DbActionTypes.fromValue(statefulFirewall.getDefaultAction().name()));
        statefulFirewall.getElements().forEach(elements -> {
            dbStatefulFirewall.getElements().add(deserializeElements(elements));
        });
        return dbStatefulFirewall;
    }

    public DbVpnaccess deserializeVpnaccess(Vpnaccess vpnaccess) {
        if (vpnaccess == null)
            return null;
        DbVpnaccess dbVpnaccess = new DbVpnaccess();
        dbVpnaccess.setVpnexit(vpnaccess.getVpnexit());
        return dbVpnaccess;
    }

    public DbVpnexit deserializeVpnexit(Vpnexit vpnexit) {
        if (vpnexit == null)
            return null;
        DbVpnexit dbVpnexit = new DbVpnexit();
        dbVpnexit.setVpnaccess(vpnexit.getVpnaccess());
        return dbVpnexit;
    }

    public DbWebApplicationFirewall deserializeWebApplicationFirewall(WebApplicationFirewall webApplicationFirewall) {
        if (webApplicationFirewall == null)
            return null;
        DbWebApplicationFirewall dbWebApplicationFirewall = new DbWebApplicationFirewall();
        dbWebApplicationFirewall
                .setDefaultAction(DbActionTypes.fromValue(webApplicationFirewall.getDefaultAction().name()));
        webApplicationFirewall.getWafElements().forEach(wafElements -> {
            dbWebApplicationFirewall.getWafElements().add(deserializeWafElements(wafElements));
        });
        return dbWebApplicationFirewall;
    }

    private DbWafElements deserializeWafElements(WafElements wafElements) {
        if (wafElements == null)
            return null;
        DbWafElements dbWafElements = new DbWafElements();
        dbWafElements.setAction(DbActionTypes.fromValue(wafElements.getAction().name()));
        dbWafElements.setDomain(wafElements.getDomain());
        dbWafElements.setUrl(wafElements.getUrl());
        return dbWafElements;
    }

    public DbWebclient deserializeWebclient(Webclient webclient) {
        if (webclient == null)
            return null;
        DbWebclient dbWebclient = new DbWebclient();
        dbWebclient.setNameWebServer(webclient.getNameWebServer());
        return dbWebclient;
    }

    public DbWebserver deserializeWebserver(Webserver webserver) {
        if (webserver == null)
            return null;
        DbWebserver dbWebserver = new DbWebserver();
        dbWebserver.setName(webserver.getName());
        return dbWebserver;
    }

    public DbConstraints deserializeConstraints(Constraints constraints) {
        if (constraints == null) return null;
        DbConstraints dbConstraints = new DbConstraints();
        dbConstraints
                .setAllocationConstraints(deserializeAllocationConstraints(constraints.getAllocationConstraints()));
        dbConstraints.setLinkConstraints(deserializeLinkConstraints(constraints.getLinkConstraints()));
        dbConstraints.setNodeConstraints(deserializeNodeConstraints(constraints.getNodeConstraints()));
        return dbConstraints;
    }

    private DbAllocationConstraints deserializeAllocationConstraints(AllocationConstraints allocationConstraints) {
        if (allocationConstraints == null) return null;
        DbAllocationConstraints dbAllocationConstraints = new DbAllocationConstraints();
        allocationConstraints.getAllocationConstraint().forEach(allocationConstraint -> {
            dbAllocationConstraints.getAllocationConstraint()
                    .add(deserializeAllocationConstraint(allocationConstraint));
        });
        return dbAllocationConstraints;
    }

    private DbAllocationConstraint deserializeAllocationConstraint(AllocationConstraint allocationConstraint) {
        if (allocationConstraint == null) return null;
        DbAllocationConstraint dbAllocationConstraint = new DbAllocationConstraint();
        dbAllocationConstraint.setNodeA(allocationConstraint.getNodeA());
        dbAllocationConstraint.setNodeB(allocationConstraint.getNodeB());
        dbAllocationConstraint.setType(DbAllocationConstraintType.fromValue(allocationConstraint.getType().name()));
        return dbAllocationConstraint;
    }

    private DbLinkConstraints deserializeLinkConstraints(LinkConstraints linkConstraints) {
        if (linkConstraints == null) return null;
        DbLinkConstraints dbLinkConstraints = new DbLinkConstraints();
        linkConstraints.getLinkMetrics().forEach(linkConstraint -> {
            dbLinkConstraints.getLinkMetrics().add(deserializeLinkConstraint(linkConstraint));
        });
        return dbLinkConstraints;
    }

    private DbLinkMetrics deserializeLinkConstraint(LinkMetrics linkMetrics) {
        if (linkMetrics == null) return null;
        DbLinkMetrics dbLinkMetrics = new DbLinkMetrics();
        dbLinkMetrics.setDst(linkMetrics.getDst());
        dbLinkMetrics.setReqLatency(linkMetrics.getReqLatency());
        dbLinkMetrics.setSrc(linkMetrics.getSrc());
        return dbLinkMetrics;
    }

    private DbNodeConstraints deserializeNodeConstraints(NodeConstraints nodeConstraints) {
        if (nodeConstraints == null) return null;
        DbNodeConstraints dbNodeConstraints = new DbNodeConstraints();
        nodeConstraints.getNodeMetrics().forEach(nodeConstraint -> {
            dbNodeConstraints.getNodeMetrics().add(deserializeNodeConstraint(nodeConstraint));
        });
        return dbNodeConstraints;
    }

    private DbNodeMetrics deserializeNodeConstraint(NodeMetrics nodeConstraint) {
        if (nodeConstraint == null) return null;
        DbNodeMetrics dbNodeMetrics = new DbNodeMetrics();
        dbNodeMetrics.setCores(nodeConstraint.getCores());
        dbNodeMetrics.setMaxNodeLatency(nodeConstraint.getMaxNodeLatency());
        dbNodeMetrics.setMemory(nodeConstraint.getMemory());
        dbNodeMetrics.setNode(nodeConstraint.getNode());
        dbNodeMetrics.setNrOfOperations(nodeConstraint.getNrOfOperations());
        dbNodeMetrics.setOptional(nodeConstraint.isOptional());
        dbNodeMetrics.setReqStorage(nodeConstraint.getReqStorage());
        return dbNodeMetrics;
    }

    public Graph serializeGraph(DbGraph dbGraph) {
        if (dbGraph == null)
            return null;
        Graph graph = new Graph();
        graph.setId(dbGraph.getId());
        graph.setServiceGraph(dbGraph.getServiceGraph());
        dbGraph.getNode().forEach(node -> {
            graph.getNode().add(serializeNode(node));
        });
        return graph;
    }

    public Node serializeNode(DbNode dbNode) {
        if (dbNode == null)
            return null;
        Node node = new Node();
        node.setId(dbNode.getId());
        node.setName(dbNode.getName());
        node.setFunctionalType(dbNode.getFunctionalType() != null ? FunctionalTypes.fromValue(dbNode.getFunctionalType().name()) : null);
        node.setConfiguration(dbNode.getConfiguration() != null ? serializeConfiguration(dbNode.getConfiguration()) : null);
        dbNode.getNeighbour().forEach(neighbour -> {
            node.getNeighbour().add(serializeNeighbour(neighbour));
        });
        return node;
    }

    public Neighbour serializeNeighbour(DbNeighbour dbNeighbour) {
        if (dbNeighbour == null)
            return null;
        Neighbour neighbour = new Neighbour();
        neighbour.setId(dbNeighbour.getId());
        neighbour.setName(dbNeighbour.getName());
        return neighbour;
    }

    public Configuration serializeConfiguration(DbConfiguration dbConfiguration) {
        if (dbConfiguration == null)
            return null;
        Configuration configuration = new Configuration();
        configuration.setId(dbConfiguration.getId());
        configuration.setAntispam(serializeAntispam(dbConfiguration.getAntispam()));
        configuration.setCache(serializeCache(dbConfiguration.getCache()));
        configuration.setDescription(dbConfiguration.getDescription());
        configuration.setDpi(serializeDpi(dbConfiguration.getDpi()));
        configuration.setEndhost(serializeEndhost(dbConfiguration.getEndhost()));
        configuration.setEndpoint(serializeEndpoint(dbConfiguration.getEndpoint()));
        configuration.setFieldmodifier(serializeFieldmodifier(dbConfiguration.getFieldmodifier()));
        configuration.setFirewall(serializeFirewall(dbConfiguration.getFirewall()));
        configuration.setForwarder(serializeForwarder(dbConfiguration.getForwarder()));
        configuration.setLoadbalancer(serializeLoadbalancer(dbConfiguration.getLoadbalancer()));
        configuration.setMailclient(serializeMailclient(dbConfiguration.getMailclient()));
        configuration.setMailserver(serializeMailserver(dbConfiguration.getMailserver()));
        configuration.setName(dbConfiguration.getName());
        configuration.setNat(serializeNat(dbConfiguration.getNat()));
        configuration.setStatefulFirewall(serializeStatefulFirewall(dbConfiguration.getStatefulFirewall()));
        configuration.setVpnaccess(serializeVpnaccess(dbConfiguration.getVpnaccess()));
        configuration.setVpnexit(serializeVpnexit(dbConfiguration.getVpnexit()));
        configuration.setWebApplicationFirewall(
                serializeWebApplicationFirewall(dbConfiguration.getWebApplicationFirewall()));
        configuration.setWebclient(serializeWebclient(dbConfiguration.getWebclient()));
        configuration.setWebserver(serializeWebserver(dbConfiguration.getWebserver()));
        return configuration;
    }

    public Antispam serializeAntispam(DbAntispam dbAntispam) {
        if (dbAntispam == null)
            return null;
        Antispam antispam = new Antispam();
        antispam.getSource().addAll(dbAntispam.getSource());
        return antispam;
    }

    public Cache serializeCache(DbCache dbCache) {
        if (dbCache == null)
            return null;
        Cache cache = new Cache();
        cache.getResource().addAll(dbCache.getResource());
        return cache;
    }

    public Dpi serializeDpi(DbDpi dbDpi) {
        if (dbDpi == null)
            return null;
        Dpi dpi = new Dpi();
        dpi.setDefaultAction(ActionTypes.fromValue(dbDpi.getDefaultAction().name()));
        dpi.getDpiElements().addAll(serializeDpiElements(dbDpi.getDpiElements()));
        return dpi;
    }

    public List<DpiElements> serializeDpiElements(List<DbDpiElements> dbList) {
        if (dbList == null)
            return null;
        List<DpiElements> list = new ArrayList<>();
        dbList.forEach(dbDpiElements -> {
            DpiElements dpiElements = new DpiElements();
            dpiElements.setAction(ActionTypes.fromValue(dbDpiElements.getAction().name()));
            dpiElements.setCondition(dbDpiElements.getCondition());
            list.add(dpiElements);
        });
        return list;
    }

    public Elements serializeElements(DbElements dbElements) {
        if (dbElements == null)
            return null;
        Elements elements = new Elements();
        elements.setAction(ActionTypes.fromValue(dbElements.getAction().name()));
        elements.setDestination(dbElements.getDestination());
        elements.setDirectional(dbElements.isDirectional());
        elements.setDstPort(dbElements.getDstPort());
        elements.setProtocol(L4ProtocolTypes.fromValue(dbElements.getProtocol().name()));
        elements.setSource(dbElements.getSource());
        elements.setSrcPort(dbElements.getSrcPort());
        return elements;
    }

    public Endhost serializeEndhost(DbEndhost dbEndhost) {
        if (dbEndhost == null)
            return null;
        Endhost endhost = new Endhost();
        endhost.setBody(dbEndhost.getBody());
        endhost.setDestination(dbEndhost.getDestination());
        endhost.setEmailFrom(dbEndhost.getEmailFrom());
        endhost.setOptions(dbEndhost.getOptions());
        endhost.setProtocol(ProtocolTypes.fromValue(dbEndhost.getProtocol().name()));
        endhost.setSequence(dbEndhost.getSequence());
        endhost.setUrl(dbEndhost.getUrl());
        return endhost;
    }

    public Endpoint serializeEndpoint(DbEndpoint dbEndpoint) {
        if (dbEndpoint == null)
            return null;
        Endpoint endpoint = new Endpoint();
        endpoint.setName(dbEndpoint.getName());
        return endpoint;
    }

    public Fieldmodifier serializeFieldmodifier(DbFieldmodifier dbFieldmodifier) {
        if (dbFieldmodifier == null)
            return null;
        Fieldmodifier fieldmodifier = new Fieldmodifier();
        fieldmodifier.setName(dbFieldmodifier.getName());
        return fieldmodifier;
    }

    public Firewall serializeFirewall(DbFirewall dbFirewall) {
        if (dbFirewall == null)
            return null;
        Firewall firewall = new Firewall();
        firewall.setDefaultAction(ActionTypes.fromValue(dbFirewall.getDefaultAction().name()));
        dbFirewall.getElements().forEach(dbElements -> {
            firewall.getElements().add(serializeElements(dbElements));
        });
        return firewall;
    }

    public Forwarder serializeForwarder(DbForwarder dbForwarder) {
        if (dbForwarder == null)
            return null;
        Forwarder forwarder = new Forwarder();
        forwarder.setName(dbForwarder.getName());
        return forwarder;
    }

    public Loadbalancer serializeLoadbalancer(DbLoadbalancer dbLoadbalancer) {
        if (dbLoadbalancer == null)
            return null;
        Loadbalancer loadbalancer = new Loadbalancer();
        loadbalancer.getPool().addAll(dbLoadbalancer.getPool());
        return loadbalancer;
    }

    public Mailclient serializeMailclient(DbMailclient dbMailclient) {
        if (dbMailclient == null)
            return null;
        Mailclient mailclient = new Mailclient();
        mailclient.setMailserver(dbMailclient.getMailserver());
        return mailclient;
    }

    public Mailserver serializeMailserver(DbMailserver dbMailserver) {
        if (dbMailserver == null)
            return null;
        Mailserver mailserver = new Mailserver();
        mailserver.setName(dbMailserver.getName());
        return mailserver;
    }

    public Nat serializeNat(DbNat dbNat) {
        if (dbNat == null)
            return null;
        Nat nat = new Nat();
        nat.getSource().addAll(dbNat.getSource());
        return nat;
    }

    public StatefulFirewall serializeStatefulFirewall(DbStatefulFirewall dbStatefulFirewall) {
        if (dbStatefulFirewall == null)
            return null;
        StatefulFirewall statefulFirewall = new StatefulFirewall();
        statefulFirewall.setDefaultAction(ActionTypes.fromValue(dbStatefulFirewall.getDefaultAction().name()));
        dbStatefulFirewall.getElements().forEach(dbElements -> {
            statefulFirewall.getElements().add(serializeElements(dbElements));
        });
        return statefulFirewall;
    }

    public Vpnaccess serializeVpnaccess(DbVpnaccess dbVpnaccess) {
        if (dbVpnaccess == null)
            return null;
        Vpnaccess vpnaccess = new Vpnaccess();
        vpnaccess.setVpnexit(dbVpnaccess.getVpnexit());
        return vpnaccess;
    }

    public Vpnexit serializeVpnexit(DbVpnexit dbVpnexit) {
        if (dbVpnexit == null)
            return null;
        Vpnexit vpnexit = new Vpnexit();
        vpnexit.setVpnaccess(dbVpnexit.getVpnaccess());
        return vpnexit;
    }

    public WebApplicationFirewall serializeWebApplicationFirewall(DbWebApplicationFirewall dbWebApplicationFirewall) {
        if (dbWebApplicationFirewall == null)
            return null;
        WebApplicationFirewall webApplicationFirewall = new WebApplicationFirewall();
        webApplicationFirewall
                .setDefaultAction(ActionTypes.fromValue(dbWebApplicationFirewall.getDefaultAction().name()));
        dbWebApplicationFirewall.getWafElements().forEach(dbWafElements -> {
            webApplicationFirewall.getWafElements().add(serializeWafElements(dbWafElements));
        });
        return webApplicationFirewall;
    }

    private WafElements serializeWafElements(DbWafElements dbWafElements) {
        if (dbWafElements == null)
            return null;
        WafElements wafElements = new WafElements();
        wafElements.setAction(ActionTypes.fromValue(dbWafElements.getAction().name()));
        wafElements.setDomain(dbWafElements.getDomain());
        wafElements.setUrl(dbWafElements.getUrl());
        return wafElements;
    }

    public Webclient serializeWebclient(DbWebclient dbWebclient) {
        if (dbWebclient == null)
            return null;
        Webclient webclient = new Webclient();
        webclient.setNameWebServer(dbWebclient.getNameWebServer());
        return webclient;
    }

    public Webserver serializeWebserver(DbWebserver dbWebserver) {
        if (dbWebserver == null)
            return null;
        Webserver webserver = new Webserver();
        webserver.setName(dbWebserver.getName());
        return webserver;
    }

    
    public Constraints serializeConstraints(DbConstraints dbConstraints) {
        if (dbConstraints == null) return null;
        Constraints constraints = new Constraints();
        constraints.setAllocationConstraints(serializeAllocationConstraints(dbConstraints.getAllocationConstraints()));
        constraints.setLinkConstraints(serializeLinkConstraints(dbConstraints.getLinkConstraints()));
        constraints.setNodeConstraints(serializeNodeConstraints(dbConstraints.getNodeConstraints()));
        return constraints;
    }

    private AllocationConstraints serializeAllocationConstraints(DbAllocationConstraints dbAllocationConstraints) {
        if (dbAllocationConstraints == null) return null;
        AllocationConstraints allocationConstraints = new AllocationConstraints();
        dbAllocationConstraints.getAllocationConstraint().forEach(dbAllocationConstraint -> {
            allocationConstraints.getAllocationConstraint()
                    .add(serializeAllocationConstraint(dbAllocationConstraint));
        });
        return allocationConstraints;
    }

    private AllocationConstraint serializeAllocationConstraint(DbAllocationConstraint dbAllocationConstraint) {
        if (dbAllocationConstraint == null) return null;
        AllocationConstraint allocationConstraint = new AllocationConstraint();
        allocationConstraint.setNodeA(dbAllocationConstraint.getNodeA());
        allocationConstraint.setNodeB(dbAllocationConstraint.getNodeB());
        allocationConstraint.setType(AllocationConstraintType.fromValue(dbAllocationConstraint.getType().name().toLowerCase()));
        return allocationConstraint;
    }

    private LinkConstraints serializeLinkConstraints(DbLinkConstraints dbLinkConstraints) {
        if (dbLinkConstraints == null) return null;
        LinkConstraints linkConstraints = new LinkConstraints();
        dbLinkConstraints.getLinkMetrics().forEach(dbLinkConstraint -> {
            linkConstraints.getLinkMetrics().add(serializeLinkConstraint(dbLinkConstraint));
        });
        return linkConstraints;
    }

    private LinkMetrics serializeLinkConstraint(DbLinkMetrics dbLinkMetrics) {
        if (dbLinkMetrics == null) return null;
        LinkMetrics linkMetrics = new LinkMetrics();
        linkMetrics.setDst(dbLinkMetrics.getDst());
        linkMetrics.setReqLatency(dbLinkMetrics.getReqLatency());
        linkMetrics.setSrc(dbLinkMetrics.getSrc());
        return linkMetrics;
    }

    private NodeConstraints serializeNodeConstraints(DbNodeConstraints dbNodeConstraints) {
        if (dbNodeConstraints == null) return null;
        NodeConstraints nodeConstraints = new NodeConstraints();
        dbNodeConstraints.getNodeMetrics().forEach(dbNodeConstraint -> {
            nodeConstraints.getNodeMetrics().add(serializeNodeConstraint(dbNodeConstraint));
        });
        return nodeConstraints;
    }

    private NodeMetrics serializeNodeConstraint(DbNodeMetrics dbNodeMetrics) {
        if (dbNodeMetrics == null) return null;
        NodeMetrics nodeMetrics = new NodeMetrics();
        nodeMetrics.setCores(dbNodeMetrics.getCores());
        nodeMetrics.setMaxNodeLatency(dbNodeMetrics.getMaxNodeLatency());
        nodeMetrics.setMemory(dbNodeMetrics.getMemory());
        nodeMetrics.setNode(dbNodeMetrics.getNode());
        nodeMetrics.setNrOfOperations(dbNodeMetrics.getNrOfOperations());
        nodeMetrics.setOptional(dbNodeMetrics.isOptional());
        nodeMetrics.setReqStorage(dbNodeMetrics.getReqStorage());
        return nodeMetrics;
    }

    // public DbGraph mergeGraphs(DbGraph oldDbGraph, DbGraph newDbGraph) {
    //     // unnecessary for graphs, for the sake of completeness
    //     newDbGraph.setId(oldDbGraph.getId());

    //     if (newDbGraph.getNode().size() >= oldDbGraph.getNode().size()) {
    //         for (int i = 0; i < oldDbGraph.getNode().size(); i++) {
    //                 newDbGraph.getNode().get(i).setId(oldDbGraph.getNode().get(i).getId());
    //         }
    //     } else {
    //         int i = 0;
    //         for ( ; i < newDbGraph.getNode().size(); i++) {
    //                 newDbGraph.getNode().get(i).setId(oldDbGraph.getNode().get(i).getId());
    //         }
    //         for ( ; i < oldDbGraph.getNode().size(); i++) {
    //                 //deleteNode(oldDbGraph.getId(), oldDbGraph.getNode().get(i).getId()); 
    //         }
    //     }
    //     return null;
    // }

}
