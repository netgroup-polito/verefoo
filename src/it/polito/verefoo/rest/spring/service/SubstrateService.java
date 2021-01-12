package it.polito.verefoo.rest.spring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.polito.verefoo.Neo4jConnection;
import it.polito.verefoo.SubstrateId;
import it.polito.verefoo.jaxb.Connection;
import it.polito.verefoo.jaxb.Connections;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.Hosts;
import it.polito.verefoo.rest.spring.repository.ConnectionRepository;
import it.polito.verefoo.rest.spring.repository.HostRepository;
import it.polito.verefoo.rest.spring.repository.SubstrateRepository;

@Service
public class SubstrateService {

    @Autowired
    SubstrateRepository substrateRepository;

    @Autowired
    HostRepository hostRepository;

    @Autowired
    ConnectionRepository connectionRepository;

    public Long createSubstrate() {
        return substrateRepository.save(new SubstrateId()).getId();
    }

    public List<Long> getAllSubstrates() {
        List<Long> substrateIds = new ArrayList<>();
        substrateRepository.findAll().forEach(substrateId -> substrateIds.add(substrateId.getId()));
        return substrateIds;
    }

    public void deleteAllSubstrates() {
        substrateRepository.deleteAll();
    }

    public void deleteSubstrate(Long id) {
        substrateRepository.deleteById(id);
    }

    public void createHosts(Long substrateId, Hosts hosts) {
        // List<Neo4jHost> neo4jHosts = hosts.getHost().stream().map(host -> new Neo4jHost(host)).collect(Collectors.toList());
        // neo4jHosts.forEach(hostRepository::save);

        hosts.getHost().forEach(hostRepository::save);
        hostRepository.bindHostsToSubstrate(substrateId, hosts.getHost());
        
        // hostRepository.createAndBindHosts(substrateId, hosts.getHost());
    }

    public Hosts getHosts(Long substrateId) {
        Hosts hosts = new Hosts();
        hosts.getHost().addAll(hostRepository.getHostsBySubstrate(substrateId));
        return hosts;
    }

    public void deleteHosts(Long substrateId) {
        // calls deleteHost repeatedly; unfortunately, due to the two-level cache mechanism of the db, other ways to delete all
        // hosts don't work, unless some cache management is configured/done
        hostRepository.getHostsBySubstrate(substrateId).forEach(host -> deleteHost(substrateId, host.getName()));
    }

    public void updateHost(Long substrateId, String hostId, Host host) {
        // hostRepository.findById(hostId);
        hostRepository.save(host);
    }

    public Host getHost(Long substrateId, String hostId) {
        return hostRepository.findById(hostId).get();
    }

    public void deleteHost(Long substrateId, String hostId) {
        // this workaround is necessary because deleteById executes a DETACH DELETE of a host
        hostRepository.unbindHostFromSubstrate(substrateId, hostId);
        hostRepository.delete(hostRepository.findById(hostId).get());
    }

	public void createConnections(Long substrateId, Connections connections) {
        connections.getConnection().forEach(
            connection -> {
                Host source = getHost(substrateId, connection.getSourceHost());
                Host destination = getHost(substrateId, connection.getDestHost());
                Neo4jConnection neo4jConnection = new Neo4jConnection(connection, source, destination);
                connectionRepository.save(neo4jConnection);
            }
        );
	}

    /**
     * In the current version, this method is just a shortcut for 
     * deleting and creating new connections in once;
     * @param substrateId
     * @param connections
     */
	public void updateConnections(Long substrateId, Connections connections) {
        deleteConnections(substrateId);
        createConnections(substrateId, connections);
	}

	public void deleteConnections(Long substrateId) {
        connectionRepository.deleteBySubstrateId(substrateId);
	}

	public Connections getConnections(Long substrateId) {
        Connections connections = new Connections();
		connectionRepository.findAllBySubstrateId(substrateId).forEach(neo4jConnection -> {
            connections.getConnection().add(neo4jConnection.getConnection());
        });
        return connections;
	}

}
