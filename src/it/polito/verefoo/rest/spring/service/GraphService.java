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
                return graphs;
        }

        public void deleteGraphs() {
                if (graphRepository.isAnyReferred()) {
                        // throw an exception
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
        public void updateGraph(Long id, Graph graph) {
                // possible alternative implementation through merge
                // deserialize the graph into dbGraph
                // set the ids of dbGraph as the ids of the existing graph
                // store dbGraph

                DbGraph newDbGraph = converter.deserializeGraph(graph);

                DbGraph oldDbGraph;
                Optional<DbGraph> dbGraph = graphRepository.findById(id, -1);
                if (dbGraph.isPresent())
                        oldDbGraph = dbGraph.get();
                else
                        return;
                
                // merge
                newDbGraph.setId(id);
                graphRepository.save(newDbGraph, 0);
                if (newDbGraph.getNode().size() >= oldDbGraph.getNode().size()) {
                        int i = 0;
                        for ( ; i < oldDbGraph.getNode().size(); i++) {
                                // newDbGraph.getNode().get(i).setId(oldDbGraph.getNode().get(i).getId());
                                updateNode(id, oldDbGraph.getNode().get(i).getId(), graph.getNode().get(i));
                        }
                        for ( ; i < newDbGraph.getNode().size(); i++) {
                                createNode(id, graph.getNode().get(i));
                        }
                } else {
                        int i = 0;
                        for ( ; i < newDbGraph.getNode().size(); i++) {
                                updateNode(id, oldDbGraph.getNode().get(i).getId(), graph.getNode().get(i));
                        }
                        for ( ; i < oldDbGraph.getNode().size(); i++) {
                                // This solution doesn't work because the save method is inspired to MERGE,
                                // so the nodes not referenced are not deleted
                                // oldDbGraph.getNode().remove(i);
                                deleteNode(oldDbGraph.getId(), oldDbGraph.getNode().get(i).getId()); 
                        }
                }

                // old version: update by deleting and creating again, but a new id is generated
                // graphRepository.deleteById(id);
                // DbGraph dbGraph = graphRepository.save(converter.deserializeGraph(graph));
                // return dbGraph.getId();
        }

        public void deleteGraph(Long id) {
                if (graphRepository.isReferred(id)) {
                        // throw exception (status code should be 409)
                } else {
                        graphRepository.deleteById(id);
                }
        }

        public Graph getGraph(Long id) throws Exception {
                Optional<DbGraph> dbGraph = graphRepository.findById(id, -1);
                if (dbGraph.isPresent())
                        return converter.serializeGraph(dbGraph.get());
                else
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The graph with id " + id + " doesn't exist.");
        }

        @Transactional
        public Long createNode(Long id, Node node) {
                Long nodeId = nodeRepository.save(converter.deserializeNode(node)).getId();
                graphRepository.bindNode(id, nodeId);
                return nodeId;
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
                        // throw exception
                } else {
                        graphRepository.unbindNode(id, nodeId);
                        nodeRepository.deleteById(nodeId);   
                }
        }

        /**
         * As usual, {@code node} and its neighbours must not declare any id.
         * 
         */
        @Transactional
        public void updateNode(Long id, Long nodeId, Node node) {
                DbNode newDbNode = converter.deserializeNode(node);
                DbNode oldDbNode;
                Optional<DbNode> dbNode = nodeRepository.findById(nodeId, -1);
                if (dbNode.isPresent()) {
                        oldDbNode = dbNode.get();
                } else
                        return;
                
                // merge
                newDbNode.setId(nodeId);
                nodeRepository.save(newDbNode, 0);
                updateConfiguration(id, nodeId, oldDbNode.getConfiguration().getId(), node.getConfiguration());
                // neighbours can be updated by just deleting all of them and creating new ones, since
                // the ids are not visible to the user
                if (newDbNode.getNeighbour().size() >= oldDbNode.getNeighbour().size()) {
                        int i = 0;
                        for ( ; i < oldDbNode.getNeighbour().size(); i++) {
                                // newDbGraph.getNode().get(i).setId(oldDbGraph.getNode().get(i).getId());
                                updateNeighbour(id, nodeId, oldDbNode.getNeighbour().get(i).getId(), node.getNeighbour().get(i));
                        }
                        for ( ; i < newDbNode.getNeighbour().size(); i++) {
                                createNeighbour(id, nodeId, node.getNeighbour().get(i));
                        }
                } else {
                        int i = 0;
                        for ( ; i < newDbNode.getNeighbour().size(); i++) {
                                updateNeighbour(id, nodeId, oldDbNode.getNeighbour().get(i).getId(), node.getNeighbour().get(i));
                        }
                        for ( ; i < oldDbNode.getNeighbour().size(); i++) {
                                // This solution doesn't work because the save method is inspired to MERGE,
                                // so the nodes not referenced are not deleted
                                // oldDbGraph.getNode().remove(i);
                                deleteNeighbour(id, nodeId, oldDbNode.getNeighbour().get(i).getId()); 
                        }
                }

                // old version: update by deleting and creating again, but a new id is generated
                // deleteNode(id, nodeId);
                // return createNode(id, node);
        }

        public Node getNode(Long id, Long nodeId) {
                Optional<DbNode> dbNode = nodeRepository.findById(nodeId, -1);
                if (dbNode.isPresent()) {
                        return converter.serializeNode(dbNode.get());
                } else
                        return null;
        }

        public Long createNeighbour(Long id, Long nodeId, Neighbour neighbour) {
                DbNeighbour dbNeighbour = neighbourRepository.save(converter.deserializeNeighbour(neighbour));
                nodeRepository.bindNeighbour(nodeId, dbNeighbour.getId());
                return dbNeighbour.getId();
        }

        // TODO: decide whether to add a controller for this function or not
        private void updateNeighbour(Long id, Long nodeId, Long neighbourId, Neighbour neighbour) {
                DbNeighbour newDbNeighbour = converter.deserializeNeighbour(neighbour);
                newDbNeighbour.setId(neighbourId);
                neighbourRepository.save(newDbNeighbour);
        }

        public void deleteNeighbour(Long id, Long nodeId, Long neighbourId) {
                nodeRepository.unbindNeighbour(nodeId, neighbourId);
                neighbourRepository.deleteById(neighbourId);
        }

        public Configuration getConfiguration(Long gid, Long nodeId) {
                return converter.serializeConfiguration(nodeRepository.findConfiguration(nodeId));
        }

        @Transactional
        public void updateConfiguration(Long id, Long nodeId, Long configurationId, Configuration configuration) {
                DbConfiguration newDbConfiguration = converter.deserializeConfiguration(configuration);

                configurationRepository.deleteFunctionsById(configurationId);
                newDbConfiguration.setId(configurationId);
                configurationRepository.save(newDbConfiguration);

                // old version: update by deleting and creating again, but a new id is generated
                // nodeRepository.unbindConfiguration(nodeId);
                // configurationRepository.deleteById(configurationId);
                // DbConfiguration dbConfiguration = configurationRepository
                //                 .save(converter.deserializeConfiguration(configuration));
                // nodeRepository.bindConfiguration(nodeId, dbConfiguration.getId());
                // return dbConfiguration.getId();
        }

        public void createConstraints(Long id, Constraints constraints) {
                DbConstraints dbConstraints = converter.deserializeConstraints(constraints);
                dbConstraints.setGraph(id);
                dbConstraints = constraintsRepository.save(dbConstraints);
                constraintsRepository.bindToGraph(dbConstraints.getId());
        }

        public void deleteConstraints(Long id) {
                constraintsRepository.deleteByGraphId(id);
        }

        public Constraints getConstraints(Long id) {
                Optional<DbConstraints> dbConstraints = constraintsRepository.findByGraphId(id);
                if (dbConstraints.isPresent()) {
                        return converter.serializeConstraints(dbConstraints.get());
                } else
                        return null;
        }

        @Transactional
        public void updateConstraints(Long id, Constraints constraints) {
                deleteConstraints(id);
                createConstraints(id, constraints);
        }

}
