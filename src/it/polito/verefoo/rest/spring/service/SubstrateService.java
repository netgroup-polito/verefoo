package it.polito.verefoo.rest.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.polito.verefoo.DbConnections;
import it.polito.verefoo.DbHost;
import it.polito.verefoo.DbHosts;
import it.polito.verefoo.jaxb.Connections;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.Hosts;
import it.polito.verefoo.rest.spring.converter.SubstrateConverter;
import it.polito.verefoo.rest.spring.repository.ConnectionRepository;
import it.polito.verefoo.rest.spring.repository.HostRepository;
import it.polito.verefoo.rest.spring.repository.HostsRepository;
import it.polito.verefoo.rest.spring.repository.SubstrateRepository;

@Service
public class SubstrateService {

    @Autowired
    HostsRepository hostsRepository;

    @Autowired
    SubstrateRepository substrateRepository;

    @Autowired
    HostRepository hostRepository;

    @Autowired
    ConnectionRepository connectionRepository;

    @Autowired
    SubstrateConverter converter;

    public Long createSubstrate() {
        DbHosts dbHosts = new DbHosts();
        return hostsRepository.save(dbHosts).getId();
    }

    public List<Long> getAllSubstrates() {
        List<Long> substrateIds = new ArrayList<>();
        hostsRepository.findAll().forEach(substrate -> substrateIds.add(substrate.getId()));
        return substrateIds;
    }

    public void deleteAllSubstrates() {
        hostsRepository.deleteAll();
    }

    public void deleteSubstrate(Long id) {
        hostsRepository.deleteById(id);
    }

    @Transactional
    public List<Long> createHosts(Long substrateId, Hosts hosts) {
        List<Long> hostsIds = new ArrayList<>();
        hosts.getHost().forEach(host -> {
            DbHost dbHost = hostRepository.save(converter.deserializeHost(host));
            hostsRepository.bindHost(substrateId, dbHost.getId());
            hostsIds.add(dbHost.getId());
        });
        return hostsIds;
    }

    public Hosts getHosts(Long substrateId) {
        Optional<DbHosts> dbHosts = hostsRepository.findById(substrateId, -1);
        if (dbHosts.isPresent()) {
            return converter.serializeHosts(dbHosts.get());
        } else return null;
    }

    public void deleteHosts(Long substrateId) {
        hostsRepository.deleteHosts(substrateId);
    }

    // TODO: write this on docs
    // Do not annotate this method with Transactional, since all the called methods
    // are already annotated with it
    public Long updateHost(Long substrateId, Long hostId, Host host) {
        deleteHost(substrateId, hostId);

        Hosts hosts = new Hosts();
        hosts.getHost().add(host);
        return createHosts(substrateId, hosts).get(0);
    }

    public Host getHost(Long substrateId, Long hostId) {
        Optional<DbHost> dbHost = hostRepository.findById(hostId, -1);
        if (dbHost.isPresent()) {
            return converter.serializeHost(dbHost.get());
        } else return null;
    }

    @Transactional
    public void deleteHost(Long substrateId, Long hostId) {
        hostsRepository.unbindHost(substrateId, hostId);
        hostRepository.deleteById(hostId);
    }

	public void createConnections(Long substrateId, Connections connections) {
        DbConnections dbConnections = converter.deserializeConnections(connections);
        // Some properties can be retrieved only from the db, so this snippet finalizes
        // the deserialization process of connections
        dbConnections.getConnection().forEach(
            dbConnection -> {
                Optional<DbHost> source = hostRepository.findByName(dbConnection.getSourceHost());
                Optional<DbHost> dest = hostRepository.findByName(dbConnection.getDestHost());
                if (source.isPresent() && dest.isPresent()) {
                    dbConnection.setSource(source.get());
                    dbConnection.setDest(dest.get());
                }
            }
        );

        connectionRepository.saveAll(dbConnections.getConnection());
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
        connectionRepository.deleteAllConnectionsBySubstrateId(substrateId);
	}

	public Connections getConnections(Long substrateId) {
        Connections connections = new Connections();
		connectionRepository.findAllConnectionsBySubstrateId(substrateId).forEach(dbConnection -> {
            connections.getConnection().add(converter.serializeConnection(dbConnection));
        });
        return connections;
	}

}
