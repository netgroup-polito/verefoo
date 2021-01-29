package it.polito.verefoo.rest.spring.converter;

import org.springframework.stereotype.Component;

import it.polito.verefoo.DbConnection;
import it.polito.verefoo.DbConnections;
import it.polito.verefoo.DbFunctionalTypes;
import it.polito.verefoo.DbHost;
import it.polito.verefoo.DbHosts;
import it.polito.verefoo.DbNodeRefType;
import it.polito.verefoo.DbSupportedVNFType;
import it.polito.verefoo.DbTypeOfHost;
import it.polito.verefoo.jaxb.Connection;
import it.polito.verefoo.jaxb.Connections;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.Hosts;
import it.polito.verefoo.jaxb.NodeRefType;
import it.polito.verefoo.jaxb.SupportedVNFType;
import it.polito.verefoo.jaxb.TypeOfHost;

@Component
public class SubstrateConverter {

    public DbHosts deserializeHosts(Hosts hosts) {
        DbHosts dbHosts = new DbHosts();
        hosts.getHost().forEach(host -> {
            dbHosts.getHost().add(deserializeHost(host));
        });
        return dbHosts;
    }

    public DbHost deserializeHost(Host host) {
        DbHost dbHost = new DbHost();
        dbHost.setActive(host.isActive());
        dbHost.setCores(host.getCores());
        dbHost.setCpu(host.getCpu());
        dbHost.setDiskStorage(host.getDiskStorage());
        dbHost.setFixedEndpoint(host.getFixedEndpoint());
        dbHost.setMaxVNF(host.getMaxVNF());
        dbHost.setMemory(host.getMemory());
        dbHost.setName(host.getName());
        dbHost.setType(DbTypeOfHost.fromValue(host.getType().name()));
        host.getNodeRef().forEach(nodeRefType -> {
            dbHost.getNodeRef().add(deserializeNodeRefType(nodeRefType));
        });
        host.getSupportedVNF().forEach(supportedVNF -> {
            dbHost.getSupportedVNF().add(deserializeSupportedVNF(supportedVNF));
        });
        return dbHost;
    }

    public DbNodeRefType deserializeNodeRefType(NodeRefType nodeRef) {
        DbNodeRefType dbNodeRefType = new DbNodeRefType();
        dbNodeRefType.setNode(nodeRef.getNode());
        return dbNodeRefType;
    }

    public DbSupportedVNFType deserializeSupportedVNF(SupportedVNFType supportedVNF) {
        DbSupportedVNFType dbSupportedVNFType = new DbSupportedVNFType();
        dbSupportedVNFType.setFunctionalType(DbFunctionalTypes.fromValue(supportedVNF.getFunctionalType().name()));
        return dbSupportedVNFType;
    }

    public DbConnections deserializeConnections(Connections connections) {
        DbConnections dbConnections = new DbConnections();
        connections.getConnection().forEach(connection -> {
            dbConnections.getConnection().add(deserializeConnection(connection));
        });
        return dbConnections;
    }

    /**
     * 
     * @param connections
     * @return the **PARTIAL** deserialization, since the fields for the hosts
     *         require a call to the db
     */
    private DbConnection deserializeConnection(Connection connection) {
        DbConnection dbConnection = new DbConnection();
        dbConnection.setAvgLatency(connection.getAvgLatency());
        dbConnection.setDestHost(connection.getDestHost());
        dbConnection.setSourceHost(connection.getSourceHost());
        return dbConnection;
    }

    public Hosts serializeHosts(DbHosts dbHosts) {
        Hosts hosts = new Hosts();
        dbHosts.getHost().forEach(dbHost -> {
            hosts.getHost().add(serializeHost(dbHost));
        });
        return hosts;
    }


    public Host serializeHost(DbHost dbHost) {
        Host host = new Host();
        host.setActive(dbHost.isActive());
        host.setCores(dbHost.getCores());
        host.setCpu(dbHost.getCpu());
        host.setDiskStorage(dbHost.getDiskStorage());
        host.setFixedEndpoint(dbHost.getFixedEndpoint());
        host.setMaxVNF(dbHost.getMaxVNF());
        host.setMemory(dbHost.getMemory());
        host.setName(dbHost.getName());
        host.setType(TypeOfHost.fromValue(dbHost.getType().name()));
        dbHost.getNodeRef().forEach(dbNodeRefType -> {
            host.getNodeRef().add(serializeNodeRefType(dbNodeRefType));
        });
        dbHost.getSupportedVNF().forEach(dbSupportedVNF -> {
            host.getSupportedVNF().add(serializeSupportedVNFType(dbSupportedVNF));
        });
        return host;
    }

    public NodeRefType serializeNodeRefType(DbNodeRefType dbNodeRefType) {
        NodeRefType nodeRefType = new NodeRefType();
        nodeRefType.setNode(dbNodeRefType.getNode());
        return nodeRefType;
    }

    public SupportedVNFType serializeSupportedVNFType(DbSupportedVNFType dbSupportedVNFType) {
        SupportedVNFType supportedVNFType = new SupportedVNFType();
        supportedVNFType.setFunctionalType(FunctionalTypes.fromValue(dbSupportedVNFType.getFunctionalType().name()));
        return supportedVNFType;
    }

	public Connection serializeConnection(DbConnection dbConnection) {
        Connection connection = new Connection();
        connection.setAvgLatency(dbConnection.getAvgLatency());
        connection.setDestHost(dbConnection.getDestHost());
        connection.setSourceHost(dbConnection.getSourceHost());
        return connection;
	}

}
