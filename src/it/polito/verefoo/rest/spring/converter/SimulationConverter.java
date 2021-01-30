package it.polito.verefoo.rest.spring.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.polito.verefoo.DbNFV;
import it.polito.verefoo.DbNetworkForwardingPaths;
import it.polito.verefoo.DbPath;
import it.polito.verefoo.DbPathNode;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.NetworkForwardingPaths;
import it.polito.verefoo.jaxb.Path;
import it.polito.verefoo.jaxb.Path.PathNode;

@Component
public class SimulationConverter {

    @Autowired
    GraphConverter graphConverter;

    @Autowired
    RequirementConverter requirementConverter;

    @Autowired
    SubstrateConverter substrateConverter;

    public DbNFV deserializeNFV(NFV nfv) {
        if (nfv == null) return null;
        DbNFV dbNFV = new DbNFV();
        dbNFV.setGraphs(graphConverter.deserializeGraphs(nfv.getGraphs()));
        dbNFV.setConstraints(graphConverter.deserializeConstraints(nfv.getConstraints()));
        dbNFV.setPropertyDefinition(requirementConverter.deserializePropertyDefinition(nfv.getPropertyDefinition()));
        dbNFV.setHosts(substrateConverter.deserializeHosts(nfv.getHosts()));
        dbNFV.setConnections(substrateConverter.deserializeConnections(nfv.getConnections()));
        dbNFV.setNetworkForwardingPaths(deserializeNetworkForwardingPaths(nfv.getNetworkForwardingPaths()));
        dbNFV.setParsingString(nfv.getParsingString());
        return dbNFV;
    }

    private DbNetworkForwardingPaths deserializeNetworkForwardingPaths(NetworkForwardingPaths networkForwardingPaths) {
        if (networkForwardingPaths == null) return null;
        DbNetworkForwardingPaths dbNetworkForwardingPaths = new DbNetworkForwardingPaths();
        networkForwardingPaths.getPath().forEach(networkForwardingPath -> {
            dbNetworkForwardingPaths.getPath().add(deserializeNetworkForwardingPath(networkForwardingPath));
        });
        return dbNetworkForwardingPaths;
    }

    private DbPath deserializeNetworkForwardingPath(Path networkForwardingPath) {
        if (networkForwardingPath == null) return null;
        DbPath dbNetworkForwardingPath = new DbPath();
        networkForwardingPath.getPathNode().forEach(pathNode -> {
            dbNetworkForwardingPath.getPathNode().add(deserializePathNode(pathNode));
        });
        return dbNetworkForwardingPath;
    }

    private DbPathNode deserializePathNode(PathNode pathNode) {
        if (pathNode == null) return null;
        DbPathNode dbPathNode = new DbPathNode();
        dbPathNode.setName(pathNode.getName());
        return dbPathNode;
    }

}
