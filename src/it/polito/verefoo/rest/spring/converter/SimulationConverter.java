package it.polito.verefoo.rest.spring.converter;

import org.springframework.stereotype.Component;

import it.polito.verefoo.DbNetworkForwardingPaths;
import it.polito.verefoo.DbPath;
import it.polito.verefoo.DbPathNode;
import it.polito.verefoo.jaxb.NetworkForwardingPaths;
import it.polito.verefoo.jaxb.Path;
import it.polito.verefoo.jaxb.Path.PathNode;

@Component
public class SimulationConverter {

    public DbNetworkForwardingPaths deserializeNetworkForwardingPaths(NetworkForwardingPaths networkForwardingPaths) {
        if (networkForwardingPaths == null) return null;
        DbNetworkForwardingPaths dbNetworkForwardingPaths = new DbNetworkForwardingPaths();
        networkForwardingPaths.getPath().forEach(networkForwardingPath -> {
            dbNetworkForwardingPaths.getPath().add(deserializeNetworkForwardingPath(networkForwardingPath));
        });
        return dbNetworkForwardingPaths;
    }

    public DbPath deserializeNetworkForwardingPath(Path networkForwardingPath) {
        if (networkForwardingPath == null) return null;
        DbPath dbNetworkForwardingPath = new DbPath();
        networkForwardingPath.getPathNode().forEach(pathNode -> {
            dbNetworkForwardingPath.getPathNode().add(deserializePathNode(pathNode));
        });
        return dbNetworkForwardingPath;
    }

    public DbPathNode deserializePathNode(PathNode pathNode) {
        if (pathNode == null) return null;
        DbPathNode dbPathNode = new DbPathNode();
        dbPathNode.setName(pathNode.getName());
        return dbPathNode;
    }

	public NetworkForwardingPaths serializeNetworkForwardingPaths(DbNetworkForwardingPaths dbNetworkForwardingPaths) {
		if (dbNetworkForwardingPaths == null) return null;
        NetworkForwardingPaths networkForwardingPaths = new NetworkForwardingPaths();
        dbNetworkForwardingPaths.getPath().forEach(dbNetworkForwardingPath -> {
            networkForwardingPaths.getPath().add(serializeNetworkForwardingPath(dbNetworkForwardingPath));
        });
        return networkForwardingPaths;
	}

    public Path serializeNetworkForwardingPath(DbPath dbNetworkForwardingPath) {
        if (dbNetworkForwardingPath == null) return null;
        Path networkForwardingPath = new Path();
        dbNetworkForwardingPath.getPathNode().forEach(dbPathNode -> {
            networkForwardingPath.getPathNode().add(serializePathNode(dbPathNode));
        });
        return networkForwardingPath;
    }

    public PathNode serializePathNode(DbPathNode dbPathNode) {
        if (dbPathNode == null) return null;
        PathNode pathNode = new PathNode();
        pathNode.setName(dbPathNode.getName());
        return pathNode;
    }

}
