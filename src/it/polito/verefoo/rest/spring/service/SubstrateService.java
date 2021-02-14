package it.polito.verefoo.rest.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import it.polito.verefoo.DbConnections;
import it.polito.verefoo.DbHost;
import it.polito.verefoo.DbHosts;
import it.polito.verefoo.DbNodeRefType;
import it.polito.verefoo.DbSupportedVNFType;
import it.polito.verefoo.jaxb.Connections;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.Hosts;
import it.polito.verefoo.rest.spring.converter.SubstrateConverter;
import it.polito.verefoo.rest.spring.repository.ConnectionRepository;
import it.polito.verefoo.rest.spring.repository.HostRepository;
import it.polito.verefoo.rest.spring.repository.HostsRepository;
import it.polito.verefoo.rest.spring.repository.NodeRefTypeRepository;
import it.polito.verefoo.rest.spring.repository.SupportedVNFTypeRepository;

@Service
public class SubstrateService {

    @Autowired
    HostsRepository hostsRepository;

    @Autowired
    HostRepository hostRepository;

    @Autowired
    ConnectionRepository connectionRepository;

    @Autowired
    NodeRefTypeRepository nodeRefTypeRepository;

    @Autowired
    SupportedVNFTypeRepository supportedVNFTypeRepository;

    @Autowired
    SubstrateConverter converter;

    public Long createSubstrate() {
        DbHosts dbHosts = new DbHosts();
        return hostsRepository.save(dbHosts).getId();
    }

    public List<Long> getAllSubstrates() {
        List<Long> substrateIds = new ArrayList<>();
        hostsRepository.findAll(-1).forEach(substrate -> substrateIds.add(substrate.getId()));

        if (substrateIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No substrate is in the workspace.");
        } else {
            return substrateIds;
        }
    }

    public void deleteAllSubstrates() {
        if (hostsRepository.count() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "The workspace is already clean of substrates.");
        } else {
            hostsRepository.deleteAll();
        }
    }

    public void deleteSubstrate(Long id) {
        if (hostsRepository.existsById(id) == false) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The substrate " + id + " doesn't exist.");
        } else {
                hostsRepository.deleteById(id);
        }
    }

    @Transactional
    public void createHosts(Long substrateId, Hosts hosts) {
        try {
            hosts.getHost().forEach(host -> {
                DbHost dbHost = hostRepository.save(converter.deserializeHost(host));
                hostsRepository.bindHost(substrateId, dbHost.getId());
                dbHost.getNodeRef().forEach(nodeRef -> {
                    nodeRefTypeRepository.bindToNode(nodeRef.getId());
                });
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "One referred node from the substrate " + substrateId + " doesn't exist.");
        }
    }

    public Hosts getHosts(Long substrateId) {
        Optional<DbHosts> dbHosts = hostsRepository.findById(substrateId, -1);
        if (dbHosts.isPresent()) {
            return converter.serializeHosts(dbHosts.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The substrate " + substrateId + " doesn't exist.");
        }
    }

    public void deleteHosts(Long substrateId) {
        if (hostsRepository.existsById(substrateId)) {
            hostsRepository.deleteHosts(substrateId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The substrate " + substrateId + " doesn't exist.");
        }
    }

    @Transactional
    public void updateHost(Long substrateId, String hostName, Host host) {
        DbHost newDbHost = converter.deserializeHost(host);
        DbHost oldDbHost;
        Optional<DbHost> dbHost = hostRepository.findByName(hostName);
        if (dbHost.isPresent()) {
            oldDbHost = dbHost.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The host " + hostName + " doesn't exist.");
        }

        Long oldDbHostId = oldDbHost.getId();
        Long newDbHostNodeRefSize = Long.valueOf(newDbHost.getNodeRef().size());
        Long oldDbHostNodeRefSize = Long.valueOf(oldDbHost.getNodeRef().size());
        Long newDbHostSupportedVNFSize = Long.valueOf(newDbHost.getSupportedVNF().size());
        Long oldDbHostSupportedVNFSize = Long.valueOf(oldDbHost.getSupportedVNF().size());

        // merge
        newDbHost.setId(oldDbHostId);
        hostRepository.save(newDbHost, 0);
        // merge nodeRef nodes, updating the foreign keys
        if (newDbHostNodeRefSize >= oldDbHostNodeRefSize) {
            int i = 0;
            for ( ; i < oldDbHostNodeRefSize; i++) {
                // update in place
                newDbHost.getNodeRef().get(i).setId(oldDbHost.getNodeRef().get(i).getId());
                nodeRefTypeRepository.unbindFromNode(oldDbHost.getNodeRef().get(i).getId());
                nodeRefTypeRepository.save(newDbHost.getNodeRef().get(i), 0);
                try {
                    nodeRefTypeRepository.bindToNode(oldDbHost.getNodeRef().get(i).getId());
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "The referred node " + oldDbHost.getNodeRef().get(i).getNode() + " for the host " + hostName + "doesn't exist");
                }
                
            }
            for ( ; i < newDbHostNodeRefSize; i++) {
                DbNodeRefType newDbNodeRefType = nodeRefTypeRepository.save(newDbHost.getNodeRef().get(i), 0);
                hostRepository.bindNodeRefType(oldDbHostId, newDbNodeRefType.getId());
                nodeRefTypeRepository.bindToNode(newDbNodeRefType.getId());
            }
        } else {
            int i = 0;
            for ( ; i < newDbHostNodeRefSize; i++) {
                // update in place
                newDbHost.getNodeRef().get(i).setId(oldDbHost.getNodeRef().get(i).getId());
                nodeRefTypeRepository.unbindFromNode(oldDbHost.getNodeRef().get(i).getId());
                nodeRefTypeRepository.save(newDbHost.getNodeRef().get(i), 0);
                try {
                    nodeRefTypeRepository.bindToNode(oldDbHost.getNodeRef().get(i).getId());
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "The referred node " + oldDbHost.getNodeRef().get(i).getNode() + " for the host " + hostName + "doesn't exist");
                }
            }
            for ( ; i < oldDbHostNodeRefSize; i++) {
                nodeRefTypeRepository.unbindFromNode(oldDbHost.getNodeRef().get(i).getId());
                nodeRefTypeRepository.delete(oldDbHost.getNodeRef().get(i)); 
            }
        }
        // merge SupportedVNFType nodes
        if (newDbHostSupportedVNFSize >= oldDbHostSupportedVNFSize) {
            int i = 0;
            for ( ; i < oldDbHostSupportedVNFSize; i++) {
                // update in place
                newDbHost.getSupportedVNF().get(i).setId(oldDbHost.getSupportedVNF().get(i).getId());
                supportedVNFTypeRepository.save(newDbHost.getSupportedVNF().get(i));
            }
            for ( ; i < newDbHostSupportedVNFSize; i++) {
                // create the remaining ones
                DbSupportedVNFType dbSupportedVNFType = supportedVNFTypeRepository.save(newDbHost.getSupportedVNF().get(i), 0);
                hostRepository.bindSupportedVNFType(oldDbHostId, dbSupportedVNFType.getId());
            }
        } else {
            int i = 0;
            for ( ; i < newDbHostSupportedVNFSize; i++) {
                // update in place
                newDbHost.getSupportedVNF().get(i).setId(oldDbHost.getSupportedVNF().get(i).getId());
                supportedVNFTypeRepository.save(newDbHost.getSupportedVNF().get(i));
            }
            for ( ; i < oldDbHostSupportedVNFSize; i++) {
                // delete the remaining ones
                supportedVNFTypeRepository.delete(oldDbHost.getSupportedVNF().get(i)); 
            }
        }
    }

    public Host getHost(Long substrateId, String hostName) {
        Optional<DbHost> dbHost = hostRepository.findByName(hostName);
        if (dbHost.isPresent()) {
            return converter.serializeHost(dbHost.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The host " + hostName + " doesn't exist.");
        }
    }

    @Transactional
    public void deleteHost(Long substrateId, String hostName) {
        DbHost dbHost;
        Optional<DbHost> tmp = hostRepository.findByName(hostName);
        if (tmp.isPresent()) {
            dbHost = tmp.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The host " + hostName + " doesn't exist.");
        }

        hostsRepository.unbindHost(substrateId, dbHost.getId());
        hostRepository.deleteById(dbHost.getId());
    }

	public void createConnections(Long substrateId, Connections connections) {
        DbConnections dbConnections = converter.deserializeConnections(connections);
        // Some properties can be retrieved only from the db, so this snippet finalizes
        // the deserialization process of connections
        try {
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
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "The host " + substrateId + " doesn't exist.");
        }

        connectionRepository.saveAll(dbConnections.getConnection());
	}

    /**
     * In the current version, this method is just a shortcut for 
     * deleting and creating new connections in once;
     * @param substrateId
     * @param connections
     */
	public void updateConnections(Long substrateId, Connections connections) {
        // this methodology is applicable because the id of connections is not visible to the
        // user
        deleteConnections(substrateId);
        createConnections(substrateId, connections);
        
	}

	public void deleteConnections(Long substrateId) {
        if (hostsRepository.existsById(substrateId)) {
            connectionRepository.deleteAllConnectionsBySubstrateId(substrateId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The substrate " + substrateId + " doesn't exist.");
        }
        
	}

	public Connections getConnections(Long substrateId) {
        Connections connections = new Connections();
        if (hostsRepository.existsById(substrateId)) {
            connectionRepository.findAllConnectionsBySubstrateId(substrateId).forEach(dbConnection -> {
                connections.getConnection().add(converter.serializeConnection(dbConnection));
            });
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The substrate " + substrateId + " doesn't exist.");
        }
		
        return connections;
	}

}
