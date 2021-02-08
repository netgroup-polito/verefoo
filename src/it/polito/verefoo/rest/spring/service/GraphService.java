package it.polito.verefoo.rest.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import it.polito.verefoo.DbConfiguration;
import it.polito.verefoo.DbConstraints;
import it.polito.verefoo.DbFunctionalTypes;
import it.polito.verefoo.DbGraph;
import it.polito.verefoo.DbNeighbour;
import it.polito.verefoo.DbNode;
import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.Constraints;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Graphs;
import it.polito.verefoo.jaxb.Neighbour;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.rest.spring.converter.GraphConverter;
import it.polito.verefoo.rest.spring.repository.ConfigurationRepository;
import it.polito.verefoo.rest.spring.repository.ConstraintsRepository;
import it.polito.verefoo.rest.spring.repository.GraphRepository;
import it.polito.verefoo.rest.spring.repository.NeighbourRepository;
import it.polito.verefoo.rest.spring.repository.NodeRepository;

@Service
public class GraphService {

        @Autowired
        GraphRepository graphRepository;

        @Autowired
        NodeRepository nodeRepository;

        @Autowired
        NeighbourRepository neighbourRepository;

        @Autowired
        ConfigurationRepository configurationRepository;

        @Autowired
        ConstraintsRepository constraintsRepository;

        @Autowired
        GraphConverter converter;

        /**
         * The order of returned ids is the same as that of the input graphs
         * 
         * @param graphs
         * @return the generated ids for the graphs
         */
        public List<Long> createGraphs(Graphs graphs) {
                List<Long> ids = new ArrayList<>();
                graphs.getGraph().forEach(graph -> {
                        DbGraph dbGraph = graphRepository.save(converter.deserializeGraph(graph));
                        ids.add(dbGraph.getId());
                });

                return ids;
        }

        public Graphs getGraphs() {
                Graphs graphs = new Graphs();
                graphRepository.findAll(-1).forEach(dbGraph -> {
                        graphs.getGraph().add(converter.serializeGraph(dbGraph));
                });

                if (graphs.getGraph().isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No graph is in the workspace.");
                } else {
                        return graphs;
                }
        }

        public void deleteGraphs() {
                if (graphRepository.count() == 0) {
                        throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "The workspace is already clean of graphs.");
                } else if (graphRepository.isAnyReferred()) {
                        throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "A graph cannot be deleted because it is referred by other resources: delete them first.");
                } else {
                        graphRepository.deleteAll();
                }
        }

        /**
         * The graph given in input and its neighbours must not have set any id
         * 
         * @param gid
         * @param graph
         * @return a new id for the modified graph
         */
        @Transactional
        public void updateGraph(Long id, Graph graph) {
                DbGraph newDbGraph = converter.deserializeGraph(graph);

                DbGraph oldDbGraph;
                Optional<DbGraph> dbGraph = graphRepository.findById(id, -1);
                if (dbGraph.isPresent())
                        oldDbGraph = dbGraph.get();
                else
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The graph " + id + " doesn't exist.");

                // merge
                newDbGraph.setId(id);
                graphRepository.save(newDbGraph, 0);
                if (newDbGraph.getNode().size() >= oldDbGraph.getNode().size()) {
                        int i = 0;
                        for (; i < oldDbGraph.getNode().size(); i++) {
                                // newDbGraph.getNode().get(i).setId(oldDbGraph.getNode().get(i).getId());
                                updateNode(id, oldDbGraph.getNode().get(i).getId(), graph.getNode().get(i));
                        }
                        for (; i < newDbGraph.getNode().size(); i++) {
                                createNode(id, graph.getNode().get(i));
                        }
                } else {
                        int i = 0;
                        for (; i < newDbGraph.getNode().size(); i++) {
                                updateNode(id, oldDbGraph.getNode().get(i).getId(), graph.getNode().get(i));
                        }
                        for (; i < oldDbGraph.getNode().size(); i++) {
                                deleteNode(oldDbGraph.getId(), oldDbGraph.getNode().get(i).getId());
                        }
                }

        }

        public void deleteGraph(Long id) {
                if (graphRepository.existsById(id) == false) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The graph " + id + " doesn't exist.");
                } else if (graphRepository.isReferred(id)) {
                        throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "The graph is currently referred by other resources. First delete them.");
                } else  {
                        graphRepository.deleteById(id);
                }
        }

        public Graph getGraph(Long id) throws Exception {
                Optional<DbGraph> dbGraph = graphRepository.findById(id, -1);
                if (dbGraph.isPresent())
                        return converter.serializeGraph(dbGraph.get());
                else
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "The graph " + id + " doesn't exist.");
        }

        @Transactional
        public Long createNode(Long id, Node node) {
                if (graphRepository.existsById(id)) {
                        Long nodeId = nodeRepository.save(converter.deserializeNode(node)).getId();
                        graphRepository.bindNode(id, nodeId);
                        return nodeId;   
                } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "The graph " + id + " doesn't exist.");
                }
        }

        /**
         * TODO: decide what happens if this node is defined as neighbour of another
         * node
         * 
         * @param id
         * @param nodeId
         */
        @Transactional
        public void deleteNode(Long id, Long nodeId) {
                if (nodeRepository.isReferred(nodeId)) {
                        throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "The node is currently referred by other resources. First delete them.");
                } else {
                        graphRepository.unbindNode(id, nodeId);
                        nodeRepository.deleteById(nodeId);
                }
        }

        @Transactional
        public void updateNode(Long id, Long nodeId, Node node) {
                DbNode newDbNode = converter.deserializeNode(node);
                DbNode oldDbNode;
                Optional<DbNode> dbNode = nodeRepository.findById(nodeId, -1);
                if (dbNode.isPresent()) {
                        oldDbNode = dbNode.get();
                } else
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The node " + nodeId + " doesn't exist.");
                
                // merge
                newDbNode.setId(nodeId);
                nodeRepository.save(newDbNode, 0);

                updateConfiguration(id, nodeId, oldDbNode.getConfiguration().getId(), node.getConfiguration());

                if (newDbNode.getNeighbour().size() >= oldDbNode.getNeighbour().size()) {
                        int i = 0;
                        for (; i < oldDbNode.getNeighbour().size(); i++) {
                                updateNeighbour(id, nodeId, oldDbNode.getNeighbour().get(i).getId(),
                                                node.getNeighbour().get(i));
                        }
                        for (; i < newDbNode.getNeighbour().size(); i++) {
                                createNeighbour(id, nodeId, node.getNeighbour().get(i));
                        }
                } else {
                        int i = 0;
                        for (; i < newDbNode.getNeighbour().size(); i++) {
                                updateNeighbour(id, nodeId, oldDbNode.getNeighbour().get(i).getId(),
                                                node.getNeighbour().get(i));
                        }
                        for (; i < oldDbNode.getNeighbour().size(); i++) {
                                deleteNeighbour(id, nodeId, oldDbNode.getNeighbour().get(i).getId());
                        }
                }
        }

        public Node getNode(Long id, Long nodeId) {
                Optional<DbNode> dbNode = nodeRepository.findById(nodeId, -1);
                if (dbNode.isPresent()) {
                        return converter.serializeNode(dbNode.get());
                } else
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The node " + nodeId + " doesn't exist.");
        }

        public Long createNeighbour(Long id, Long nodeId, Neighbour neighbour) {
                if (nodeRepository.existsById(nodeId)) {
                        DbNeighbour dbNeighbour = neighbourRepository.save(converter.deserializeNeighbour(neighbour));
                        nodeRepository.bindNeighbour(nodeId, dbNeighbour.getId());
                        return dbNeighbour.getId();
                } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The node " + nodeId + " doesn't exist.");
                }
                
        }

        // This method may become public and bound to a controller
        private void updateNeighbour(Long id, Long nodeId, Long neighbourId, Neighbour neighbour) {
                DbNeighbour newDbNeighbour = converter.deserializeNeighbour(neighbour);
                newDbNeighbour.setId(neighbourId);
                neighbourRepository.save(newDbNeighbour);
        }

        @Transactional
        public void deleteNeighbour(Long id, Long nodeId, Long neighbourId) {
                if (neighbourRepository.existsById(neighbourId)) {
                        nodeRepository.unbindNeighbour(nodeId, neighbourId);
                        neighbourRepository.deleteById(neighbourId);
                } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The neighbour " + neighbourId + " doesn't exist.");
                }
        }

        public Configuration getConfiguration(Long id, Long nodeId) {
                return converter.serializeConfiguration(nodeRepository.findConfiguration(nodeId));
        }

        @Transactional
        public void updateConfiguration(Long id, Long nodeId, Long configurationId, Configuration configuration) {
                if (nodeRepository.existsById(nodeId)) {
                        DbConfiguration newDbConfiguration = converter.deserializeConfiguration(configuration);

                        configurationRepository.deleteFunctionsById(configurationId);
                        newDbConfiguration.setId(configurationId);
                        configurationRepository.save(newDbConfiguration);
                        configurationRepository.updateNodeFunctionalType(nodeId, inferFunctionalType(configuration));
                } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The node " + nodeId + " doesn't exist.");
                }
        }

        private DbFunctionalTypes inferFunctionalType(Configuration configuration) {
                if (configuration.getAntispam() != null) return DbFunctionalTypes.ANTISPAM;
                else if (configuration.getCache() != null) return DbFunctionalTypes.CACHE;
                else if (configuration.getDpi() != null) return DbFunctionalTypes.DPI;
                else if (configuration.getEndhost() != null) return DbFunctionalTypes.ENDHOST;
                else if (configuration.getEndpoint() != null) return DbFunctionalTypes.ENDPOINT;
                else if (configuration.getFieldmodifier() != null) return DbFunctionalTypes.FIELDMODIFIER;
                else if (configuration.getFirewall() != null) return DbFunctionalTypes.FIREWALL;
                else if (configuration.getForwarder() != null) return DbFunctionalTypes.FORWARDER;
                else if (configuration.getLoadbalancer() != null) return DbFunctionalTypes.LOADBALANCER;
                else if (configuration.getMailclient()!= null) return DbFunctionalTypes.MAILCLIENT;
                else if (configuration.getMailserver() != null) return DbFunctionalTypes.MAILSERVER;
                else if (configuration.getNat() != null) return DbFunctionalTypes.NAT;
                else if (configuration.getStatefulFirewall() != null) return DbFunctionalTypes.STATEFUL_FIREWALL;
                else if (configuration.getVpnaccess() != null) return DbFunctionalTypes.VPNACCESS;
                else if (configuration.getVpnexit() != null) return DbFunctionalTypes.VPNEXIT;
                else if (configuration.getWebApplicationFirewall() != null) return DbFunctionalTypes.WEB_APPLICATION_FIREWALL;
                else if (configuration.getWebclient() != null) return DbFunctionalTypes.WEBCLIENT;
                else if (configuration.getWebserver() != null) return DbFunctionalTypes.WEBSERVER;
                // fallback value to avoid exception throwing
                else return DbFunctionalTypes.ANTISPAM;
        }

        public void createConstraints(Long id, Constraints constraints) {
                if (graphRepository.existsById(id)) {
                        DbConstraints dbConstraints = converter.deserializeConstraints(constraints);
                        dbConstraints.setGraph(id);
                        dbConstraints = constraintsRepository.save(dbConstraints);
                        constraintsRepository.bindToGraph(dbConstraints.getId());   
                } else {
                        throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "The graph " + id + " doesn't exist.");
                }
                
        }

        public void deleteConstraints(Long id) {
                if (graphRepository.existsById(id)) {
                        constraintsRepository.deleteByGraphId(id);
                } else {
                        throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "The graph " + id + " doesn't exist.");
                }
        }

        public Constraints getConstraints(Long id) {
                Optional<DbConstraints> dbConstraints = constraintsRepository.findByGraphId(id);
                if (dbConstraints.isPresent()) {
                        return converter.serializeConstraints(dbConstraints.get());
                } else {
                        throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No constraints for the graph " + id + " exist.");
                }     
        }

        @Transactional
        public void updateConstraints(Long id, Constraints constraints) {
                if (graphRepository.existsById(id)) {
                        deleteConstraints(id);
                        createConstraints(id, constraints);
                } else {
                        throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "The graph " + id + " doesn't exist.");
                }
        }

}
